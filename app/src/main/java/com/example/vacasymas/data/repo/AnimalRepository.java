package com.example.vacasymas.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.Animal;
import com.example.vacasymas.network.ApiClient;
import com.example.vacasymas.network.services.AnimalService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class AnimalRepository {

    private static final String TAG = "AnimalRepository";
    private static final String TABLA = "animales";

    private final DBHelper dbHelper;
    private final AnimalService api;

    public AnimalRepository(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.api = ApiClient.get().create(AnimalService.class);
    }

    // =========================================================
    // CONSULTAS LOCALES
    // =========================================================

    public List<Animal> buscarPorUltimosDigitos(String ultimosDigitos) {
        List<Animal> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "SELECT a.*, e.nombre AS nombre_explotacion " +
                    "FROM animales a " +
                    "LEFT JOIN explotaciones e ON a.id_explotacion_uuid = e.id " +
                    "WHERE a.crotal LIKE ? AND a.eliminado = 0 " +
                    "ORDER BY a.crotal ASC";

            cursor = db.rawQuery(sql, new String[]{"%" + ultimosDigitos});

            while (cursor.moveToNext()) {
                lista.add(mapCursorToAnimal(cursor));
            }

        } catch (Exception e) {
            Log.e(TAG, "Error buscando animales por últimos dígitos", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return lista;
    }

    public Animal buscarPorId(String idAnimal) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        Animal animal = null;

        try {
            String sql = "SELECT a.*, e.nombre AS nombre_explotacion " +
                    "FROM animales a " +
                    "LEFT JOIN explotaciones e ON a.id_explotacion_uuid = e.id " +
                    "WHERE a.id = ? AND a.eliminado = 0 " +
                    "LIMIT 1";

            cursor = db.rawQuery(sql, new String[]{idAnimal});

            if (cursor.moveToFirst()) {
                animal = mapCursorToAnimal(cursor);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error buscando animal por id: " + idAnimal, e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return animal;
    }

    public List<Animal> obtenerAnimalesPorExplotacion(String idExplotacionUuid) {
        List<Animal> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "SELECT a.*, e.nombre AS nombre_explotacion " +
                    "FROM animales a " +
                    "LEFT JOIN explotaciones e ON a.id_explotacion_uuid = e.id " +
                    "WHERE a.id_explotacion_uuid = ? AND a.eliminado = 0 " +
                    "ORDER BY a.crotal ASC";

            cursor = db.rawQuery(sql, new String[]{idExplotacionUuid});

            while (cursor.moveToNext()) {
                lista.add(mapCursorToAnimal(cursor));
            }

        } catch (Exception e) {
            Log.e(TAG, "Error obteniendo animales por explotación: " + idExplotacionUuid, e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return lista;
    }

    public boolean hayAnimales() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        boolean hay = false;

        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLA, null);
            if (cursor.moveToFirst()) {
                hay = cursor.getInt(0) > 0;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error comprobando si hay animales", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return hay;
    }

    // =========================================================
    // OPERACIONES LOCALES
    // =========================================================

    public boolean insertarOActualizarAnimalLocal(Animal animal) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean ok = false;

        try {
            ContentValues values = animalToContentValuesLocal(animal);

            int filas = db.update(
                    TABLA,
                    values,
                    "id = ?",
                    new String[]{animal.getId()}
            );

            if (filas == 0) {
                long id = db.insert(TABLA, null, values);
                ok = id != -1;
                Log.d(TAG, "Animal insertado localmente: " + animal.getId());
            } else {
                ok = true;
                Log.d(TAG, "Animal actualizado localmente: " + animal.getId());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error insertando/actualizando animal local: " + animal.getId(), e);
        } finally {
            db.close();
        }

        return ok;
    }

    public boolean insertarOActualizarAnimalDesdeServidor(Animal animal) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean ok = false;

        try {
            ContentValues values = animalToContentValuesDesdeServidor(animal);

            int filas = db.update(
                    TABLA,
                    values,
                    "id = ?",
                    new String[]{animal.getId()}
            );

            if (filas == 0) {
                long id = db.insert(TABLA, null, values);
                ok = id != -1;
            } else {
                ok = true;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error guardando animal desde servidor: " + animal.getId(), e);
        } finally {
            db.close();
        }

        return ok;
    }

    public boolean insertarOActualizarAnimalesDesdeServidor(List<Animal> animales) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean ok = false;

        db.beginTransaction();
        try {
            for (Animal animal : animales) {
                ContentValues values = animalToContentValuesDesdeServidor(animal);

                int filas = db.update(TABLA, values, "id = ?", new String[]{animal.getId()});

                if (filas == 0) {
                    db.insert(TABLA, null, values);
                }
            }

            db.setTransactionSuccessful();
            ok = true;
            Log.d(TAG, "Animales guardados desde servidor: " + animales.size());

        } catch (Exception e) {
            Log.e(TAG, "Error insertando/actualizando animales desde servidor", e);
        } finally {
            db.endTransaction();
            db.close();
        }

        return ok;
    }

    public boolean eliminarAnimalLogico(String idAnimal, String fechaEliminado, String fechaActualizacion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean ok = false;

        try {
            ContentValues values = new ContentValues();
            values.put("eliminado", 1);
            values.put("sincronizado", 0);
            values.put("fecha_eliminado", fechaEliminado);
            values.put("fecha_actualizacion", fechaActualizacion);

            int filas = db.update(
                    TABLA,
                    values,
                    "id = ?",
                    new String[]{idAnimal}
            );

            ok = filas > 0;
            Log.d(TAG, "Animal marcado como eliminado lógicamente: " + idAnimal);

        } catch (Exception e) {
            Log.e(TAG, "Error en eliminación lógica del animal: " + idAnimal, e);
        } finally {
            db.close();
        }

        return ok;
    }

    // =========================================================
    // PENDIENTES DE SINCRONIZACIÓN
    // =========================================================

    public List<Animal> obtenerAnimalesNoSincronizados() {
        List<Animal> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "SELECT a.*, e.nombre AS nombre_explotacion " +
                    "FROM animales a " +
                    "LEFT JOIN explotaciones e ON a.id_explotacion_uuid = e.id " +
                    "WHERE a.sincronizado = 0 " +
                    "ORDER BY a.fecha_actualizacion ASC";

            cursor = db.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                lista.add(mapCursorToAnimal(cursor));
            }

            Log.d(TAG, "Animales pendientes de sincronizar: " + lista.size());

        } catch (Exception e) {
            Log.e(TAG, "Error obteniendo animales no sincronizados", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return lista;
    }

    public boolean marcarComoSincronizado(String idAnimal) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean ok = false;

        try {
            ContentValues values = new ContentValues();
            values.put("sincronizado", 1);

            int filas = db.update(
                    TABLA,
                    values,
                    "id = ?",
                    new String[]{idAnimal}
            );

            ok = filas > 0;
            Log.d(TAG, "Animal marcado como sincronizado: " + idAnimal);

        } catch (Exception e) {
            Log.e(TAG, "Error marcando animal como sincronizado: " + idAnimal, e);
        } finally {
            db.close();
        }

        return ok;
    }

    public boolean marcarComoSincronizados(List<String> idsAnimales) {
        if (idsAnimales == null || idsAnimales.isEmpty()) {
            return true;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean ok = false;

        db.beginTransaction();
        try {
            for (String idAnimal : idsAnimales) {
                ContentValues values = new ContentValues();
                values.put("sincronizado", 1);

                db.update(
                        TABLA,
                        values,
                        "id = ?",
                        new String[]{idAnimal}
                );
            }

            db.setTransactionSuccessful();
            ok = true;
            Log.d(TAG, "Animales marcados como sincronizados: " + idsAnimales.size());

        } catch (Exception e) {
            Log.e(TAG, "Error marcando animales como sincronizados", e);
        } finally {
            db.endTransaction();
            db.close();
        }

        return ok;
    }

    // =========================================================
    // PUSH A SUPABASE
    // =========================================================

    public boolean subirAnimalesNoSincronizados() {
        List<Animal> pendientes = obtenerAnimalesNoSincronizados();

        if (pendientes == null || pendientes.isEmpty()) {
            Log.d(TAG, "No hay animales pendientes de sincronizar");
            return true;
        }

        try {
            List<String> idsSincronizados = new ArrayList<>();

            for (Animal animal : pendientes) {
                JsonArray arrayUno = new JsonArray();
                arrayUno.add(animalToJson(animal));

                Log.d(TAG, "Subiendo animal individual: "
                        + animal.getCrotal()
                        + " | id: " + animal.getId()
                        + " | JSON: " + arrayUno.toString());

                Response<Void> response = api.upsertAnimales("id", arrayUno).execute();

                if (!response.isSuccessful()) {
                    String error = response.errorBody() != null
                            ? response.errorBody().string()
                            : "sin detalle";

                    Log.e(TAG, "Error subiendo animal individual: "
                            + animal.getCrotal()
                            + " | id: " + animal.getId()
                            + " | HTTP " + response.code()
                            + " | " + error);

                    return false;
                }

                idsSincronizados.add(animal.getId());

                Log.d(TAG, "Animal subido correctamente: "
                        + animal.getCrotal()
                        + " | id: " + animal.getId());
            }

            Log.d(TAG, "Animales subidos correctamente uno a uno: " + idsSincronizados.size());

            return marcarComoSincronizados(idsSincronizados);

        } catch (IOException e) {
            Log.e(TAG, "Error de red en push animales", e);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error general en push animales", e);
            return false;
        }
    }

    // =========================================================
    // DESCARGA INCREMENTAL DESDE SUPABASE
    // =========================================================

    public boolean descargarAnimalesModificadosDesdeFecha(String ultimaFechaSync) {
        String nuevaUltimaFecha = descargarAnimalesModificadosYDevolverUltimaFecha(ultimaFechaSync);
        return nuevaUltimaFecha != null;
    }

    public String descargarAnimalesModificadosYDevolverUltimaFecha(String ultimaFechaSync) {
        try {
            String filtroFecha = "gt." + ultimaFechaSync;

            Log.d(TAG, "Descargando animales modificados desde: " + ultimaFechaSync);

            Response<List<Animal>> response = api.getAnimalesDesdeFecha(
                    "*",
                    filtroFecha,
                    "fecha_actualizacion.asc"
            ).execute();

            if (!response.isSuccessful()) {
                String error = response.errorBody() != null
                        ? response.errorBody().string()
                        : "sin detalle";
                Log.e(TAG, "Error descargando animales incrementales. HTTP " + response.code() + " | " + error);
                return null;
            }

            List<Animal> animales = response.body();

            if (animales == null || animales.isEmpty()) {
                Log.d(TAG, "No hay animales nuevos/modificados para descargar");
                return ultimaFechaSync;
            }

            boolean ok = insertarOActualizarAnimalesDesdeServidor(animales);
            if (!ok) {
                return null;
            }

            String ultimaFechaRecibida = ultimaFechaSync;
            for (Animal animal : animales) {
                if (animal.getFechaActualizacion() != null &&
                        (ultimaFechaRecibida == null ||
                                animal.getFechaActualizacion().compareTo(ultimaFechaRecibida) > 0)) {
                    ultimaFechaRecibida = animal.getFechaActualizacion();
                }
            }

            Log.d(TAG, "Última fecha incremental animales: " + ultimaFechaRecibida);
            return ultimaFechaRecibida;

        } catch (IOException e) {
            Log.e(TAG, "Error de red en descarga incremental de animales", e);
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error general en descarga incremental de animales", e);
            return null;
        }
    }

    // =========================================================
    // MAPEOS
    // =========================================================

    private Animal mapCursorToAnimal(Cursor c) {
        Animal a = new Animal();

        a.setId(c.getString(c.getColumnIndexOrThrow("id")));
        a.setIdSharepoint(c.isNull(c.getColumnIndexOrThrow("id_sharepoint")) ? null : c.getLong(c.getColumnIndexOrThrow("id_sharepoint")));
        a.setCrotal(c.getString(c.getColumnIndexOrThrow("crotal")));
        a.setIdExplotacionUuid(c.getString(c.getColumnIndexOrThrow("id_explotacion_uuid")));
        a.setEstatus(c.isNull(c.getColumnIndexOrThrow("estatus")) ? null : c.getInt(c.getColumnIndexOrThrow("estatus")));
        a.setFechaNacimiento(c.getString(c.getColumnIndexOrThrow("fecha_nacimiento")));
        a.setRaza(c.getString(c.getColumnIndexOrThrow("raza")));
        a.setSexo(c.getString(c.getColumnIndexOrThrow("sexo")));
        a.setCrotalMadre(c.getString(c.getColumnIndexOrThrow("crotal_madre")));
        a.setCapa(c.getString(c.getColumnIndexOrThrow("capa")));
        a.setCercado(c.getString(c.getColumnIndexOrThrow("cercado")));
        a.setIdCercadoHistorico(c.isNull(c.getColumnIndexOrThrow("id_cercado_historico")) ? null : c.getLong(c.getColumnIndexOrThrow("id_cercado_historico")));
        a.setParidera(c.getString(c.getColumnIndexOrThrow("paridera")));
        a.setAltaGestionada(c.getString(c.getColumnIndexOrThrow("alta_gestionada")));
        a.setStatecode(c.getString(c.getColumnIndexOrThrow("statecode")));
        a.setFactor(c.getString(c.getColumnIndexOrThrow("factor")));
        a.setFechaBajaExplotacion(c.getString(c.getColumnIndexOrThrow("fecha_baja_explotacion")));
        a.setCausaAlta(c.getString(c.getColumnIndexOrThrow("causa_alta")));
        a.setExplotacionNacimiento(c.getString(c.getColumnIndexOrThrow("explotacion_nacimiento")));
        a.setCheckParidera(c.getString(c.getColumnIndexOrThrow("check_paridera")));
        a.setIschosen(c.getString(c.getColumnIndexOrThrow("ischosen")));
        a.setEstadoReproductivo(c.getString(c.getColumnIndexOrThrow("estado_reproductivo")));
        a.setCrotalIzquierdoPresente(c.getInt(c.getColumnIndexOrThrow("crotal_izquierdo_presente")) == 1);
        a.setCrotalDerechoPresente(c.getInt(c.getColumnIndexOrThrow("crotal_derecho_presente")) == 1);
        a.setFechaActualizacion(c.getString(c.getColumnIndexOrThrow("fecha_actualizacion")));
        a.setSincronizado(c.getInt(c.getColumnIndexOrThrow("sincronizado")));
        a.setEliminado(c.getInt(c.getColumnIndexOrThrow("eliminado")));
        a.setFechaEliminado(c.getString(c.getColumnIndexOrThrow("fecha_eliminado")));


        int idxNombreExplotacion = c.getColumnIndex("nombre_explotacion");
        if (idxNombreExplotacion != -1) {
            a.setNombreExplotacion(c.getString(idxNombreExplotacion));
        }

        return a;
    }

    private ContentValues animalToContentValuesLocal(Animal a) {
        ContentValues values = new ContentValues();

        values.put("id", a.getId());

        if (a.getIdSharepoint() != null) {
            values.put("id_sharepoint", a.getIdSharepoint());
        } else {
            values.putNull("id_sharepoint");
        }

        values.put("crotal", a.getCrotal());
        values.put("id_explotacion_uuid", a.getIdExplotacionUuid());

        if (a.getEstatus() != null) {
            values.put("estatus", a.getEstatus());
        } else {
            values.putNull("estatus");
        }

        values.put("fecha_nacimiento", a.getFechaNacimiento());
        values.put("raza", a.getRaza());
        values.put("sexo", a.getSexo());
        values.put("crotal_madre", a.getCrotalMadre());
        values.put("capa", a.getCapa());
        values.put("cercado", a.getCercado());

        if (a.getIdCercadoHistorico() != null) {
            values.put("id_cercado_historico", a.getIdCercadoHistorico());
        } else {
            values.putNull("id_cercado_historico");
        }

        values.put("paridera", a.getParidera());
        values.put("alta_gestionada", a.getAltaGestionada());
        values.put("statecode", a.getStatecode());
        values.put("factor", a.getFactor());
        if (a.getFechaBajaExplotacion() != null) {
            values.put("fecha_baja_explotacion", a.getFechaBajaExplotacion());
        } else {
            values.putNull("fecha_baja_explotacion");
        }
        values.put("causa_alta", a.getCausaAlta());
        values.put("explotacion_nacimiento", a.getExplotacionNacimiento());
        values.put("check_paridera", a.getCheckParidera());
        values.put("ischosen", a.getIschosen());
        values.put("estado_reproductivo",
                a.getEstadoReproductivo() != null ? a.getEstadoReproductivo() : "nada");
        values.put("crotal_izquierdo_presente",
                a.getCrotalIzquierdoPresente() != null && a.getCrotalIzquierdoPresente() ? 1 : 0);

        values.put("crotal_derecho_presente",
                a.getCrotalDerechoPresente() != null && a.getCrotalDerechoPresente() ? 1 : 0);

        values.put("fecha_actualizacion", a.getFechaActualizacion());
        values.put("sincronizado", a.getSincronizado() != null ? a.getSincronizado() : 0);
        values.put("eliminado", a.getEliminado() != null ? a.getEliminado() : 0);
        values.put("fecha_eliminado", a.getFechaEliminado());

        return values;
    }

    private ContentValues animalToContentValuesDesdeServidor(Animal a) {
        ContentValues values = animalToContentValuesLocal(a);
        values.put("sincronizado", 1);
        return values;
    }

    private JsonObject animalToJson(Animal a) {
        JsonObject obj = new JsonObject();

        putString(obj, "id", a.getId());
        putLong(obj, "id_sharepoint", a.getIdSharepoint());
        putString(obj, "crotal", a.getCrotal());
        putString(obj, "id_explotacion_uuid", a.getIdExplotacionUuid());
        putInteger(obj, "estatus", a.getEstatus());
        putString(obj, "fecha_nacimiento", a.getFechaNacimiento());
        putString(obj, "raza", a.getRaza());
        putString(obj, "sexo", a.getSexo());
        putString(obj, "crotal_madre", a.getCrotalMadre());
        putString(obj, "capa", a.getCapa());
        putString(obj, "cercado", a.getCercado());
        putLong(obj, "id_cercado_historico", a.getIdCercadoHistorico());
        putString(obj, "paridera", a.getParidera());
        putString(obj, "alta_gestionada", a.getAltaGestionada());
        putString(obj, "statecode", a.getStatecode());
        putString(obj, "factor", a.getFactor());
        if ("1".equals(a.getStatecode())) {
            obj.add("fecha_baja_explotacion", com.google.gson.JsonNull.INSTANCE);
        } else {
            putString(obj, "fecha_baja_explotacion", a.getFechaBajaExplotacion());
        }
        putString(obj, "causa_alta", a.getCausaAlta());
        putString(obj, "explotacion_nacimiento", a.getExplotacionNacimiento());
        putString(obj, "check_paridera", a.getCheckParidera());
        putString(obj, "ischosen", a.getIschosen());
        putString(obj, "estado_reproductivo",
                a.getEstadoReproductivo() != null ? a.getEstadoReproductivo() : "nada");
        obj.addProperty("crotal_izquierdo_presente",
                a.getCrotalIzquierdoPresente() != null && a.getCrotalIzquierdoPresente() ? 1 : 0);

        obj.addProperty("crotal_derecho_presente",
                a.getCrotalDerechoPresente() != null && a.getCrotalDerechoPresente() ? 1 : 0);

        putString(obj, "fecha_actualizacion", a.getFechaActualizacion());
        obj.addProperty("eliminado", a.getEliminado() != null ? a.getEliminado() : 0);
        putString(obj, "fecha_eliminado", a.getFechaEliminado());

        Log.d(TAG, "JSON animal a subir: " + obj.toString());
        return obj;
    }

    private void putString(JsonObject obj, String key, String value) {
        if (value != null) {
            obj.addProperty(key, value);
        } else {
            obj.add(key, com.google.gson.JsonNull.INSTANCE);
        }
    }

    private void putInteger(JsonObject obj, String key, Integer value) {
        if (value != null) {
            obj.addProperty(key, value);
        } else {
            obj.add(key, com.google.gson.JsonNull.INSTANCE);
        }
    }

    private void putLong(JsonObject obj, String key, Long value) {
        if (value != null) {
            obj.addProperty(key, value);
        } else {
            obj.add(key, com.google.gson.JsonNull.INSTANCE);
        }
    }

    public interface ListaAnimalesCallback {
        void onSuccess(List<Animal> animales);
        void onError(String error);
    }

    public interface AnimalCallback {
        void onSuccess(Animal animal);
        void onError(String error);
    }

    public interface BooleanCallback {
        void onSuccess(boolean ok);
        void onError(String error);
    }

    public void buscarPorIdAsync(String idAnimal, AnimalCallback callback) {
        new Thread(() -> {
            try {
                Animal animal = buscarPorId(idAnimal);
                callback.onSuccess(animal);
            } catch (Exception e) {
                Log.e(TAG, "Error en buscarPorIdAsync", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public void obtenerAnimalesPorExplotacionAsync(String idExplotacionUuid, ListaAnimalesCallback callback) {
        new Thread(() -> {
            try {
                List<Animal> lista = obtenerAnimalesPorExplotacion(idExplotacionUuid);
                callback.onSuccess(lista);
            } catch (Exception e) {
                Log.e(TAG, "Error en obtenerAnimalesPorExplotacionAsync", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public void buscarPorUltimosDigitosAsync(String ultimosDigitos, ListaAnimalesCallback callback) {
        new Thread(() -> {
            try {
                List<Animal> lista = buscarPorUltimosDigitos(ultimosDigitos);
                callback.onSuccess(lista);
            } catch (Exception e) {
                Log.e(TAG, "Error en buscarPorUltimosDigitosAsync", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public void insertarOActualizarAnimalLocalAsync(Animal animal, BooleanCallback callback) {
        new Thread(() -> {
            try {
                boolean ok = insertarOActualizarAnimalLocal(animal);
                callback.onSuccess(ok);
            } catch (Exception e) {
                Log.e(TAG, "Error en insertarOActualizarAnimalLocalAsync", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public void eliminarAnimalLogicoAsync(String idAnimal, String fechaEliminado, String fechaActualizacion, BooleanCallback callback) {
        new Thread(() -> {
            try {
                boolean ok = eliminarAnimalLogico(idAnimal, fechaEliminado, fechaActualizacion);
                callback.onSuccess(ok);
            } catch (Exception e) {
                Log.e(TAG, "Error en eliminarAnimalLogicoAsync", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public void subirAnimalesNoSincronizadosAsync(BooleanCallback callback) {
        new Thread(() -> {
            try {
                boolean ok = subirAnimalesNoSincronizados();
                callback.onSuccess(ok);
            } catch (Exception e) {
                Log.e(TAG, "Error en subirAnimalesNoSincronizadosAsync", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public void descargarAnimalesModificadosDesdeFechaAsync(String ultimaFechaSync, BooleanCallback callback) {
        new Thread(() -> {
            try {
                boolean ok = descargarAnimalesModificadosDesdeFecha(ultimaFechaSync);
                callback.onSuccess(ok);
            } catch (Exception e) {
                Log.e(TAG, "Error en descargarAnimalesModificadosDesdeFechaAsync", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public interface ResumenAnimalesCallback {
        void onSuccess(int vacas, int terneros, int toros, int novillas, int total);
        void onError(String error);
    }

    public int contarAnimalesPorEstatusYExplotacion(String idExplotacionUuid, int estatus) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        int total = 0;

        try {
            String sql = "SELECT COUNT(*) " +
                    "FROM animales " +
                    "WHERE id_explotacion_uuid = ? " +
                    "AND estatus = ? " +
                    "AND eliminado = 0 " +
                    "AND statecode = '1'";

            cursor = db.rawQuery(sql, new String[]{
                    idExplotacionUuid,
                    String.valueOf(estatus)
            });

            if (cursor.moveToFirst()) {
                total = cursor.getInt(0);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error contando animales por estatus", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return total;
    }

    public int contarTernerosPorExplotacion(String idExplotacionUuid) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        int total = 0;

        try {
            String sql = "SELECT COUNT(*) " +
                    "FROM animales " +
                    "WHERE id_explotacion_uuid = ? " +
                    "AND estatus IN (10001, 10002) " +
                    "AND eliminado = 0 " +
                    "AND statecode = '1'";

            cursor = db.rawQuery(sql, new String[]{idExplotacionUuid});

            if (cursor.moveToFirst()) {
                total = cursor.getInt(0);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error contando terneros", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return total;
    }

    public void obtenerResumenAnimalesExplotacionAsync(String idExplotacionUuid, ResumenAnimalesCallback callback) {
        new Thread(() -> {
            try {
                int vacas = contarAnimalesPorEstatusYExplotacion(idExplotacionUuid, 10003);
                int terneros = contarTernerosPorExplotacion(idExplotacionUuid);
                int toros = contarAnimalesPorEstatusYExplotacion(idExplotacionUuid, 10004);
                int novillas = contarAnimalesPorEstatusYExplotacion(idExplotacionUuid, 10005);
                int total = vacas + terneros + toros + novillas;

                callback.onSuccess(vacas, terneros, toros, novillas, total);

            } catch (Exception e) {
                Log.e(TAG, "Error en obtenerResumenAnimalesExplotacionAsync", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public List<Animal> buscarPorUltimosDigitosYExplotacion(String ultimosDigitos, String idExplotacionUuid) {
        List<Animal> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "SELECT a.*, e.nombre AS nombre_explotacion " +
                    "FROM animales a " +
                    "LEFT JOIN explotaciones e ON a.id_explotacion_uuid = e.id " +
                    "WHERE a.crotal LIKE ? " +
                    "AND a.id_explotacion_uuid = ? " +
                    "AND a.eliminado = 0 " +
                    "ORDER BY a.crotal ASC";

            cursor = db.rawQuery(sql, new String[]{"%" + ultimosDigitos, idExplotacionUuid});

            while (cursor.moveToNext()) {
                lista.add(mapCursorToAnimal(cursor));
            }

        } catch (Exception e) {
            Log.e(TAG, "Error buscando animales por últimos dígitos y explotación", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return lista;
    }

    public List<Animal> buscarPorUltimos4DigitosYExplotacion(String ultimos4, String idExplotacionUuid) {
        List<Animal> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "SELECT a.*, e.nombre AS nombre_explotacion " +
                    "FROM animales a " +
                    "LEFT JOIN explotaciones e ON a.id_explotacion_uuid = e.id " +
                    "WHERE substr(a.crotal, -4) = ? " +
                    "AND a.id_explotacion_uuid = ? " +
                    "AND a.eliminado = 0 " +
                    "ORDER BY a.crotal ASC";

            cursor = db.rawQuery(sql, new String[]{ultimos4, idExplotacionUuid});

            while (cursor.moveToNext()) {
                lista.add(mapCursorToAnimal(cursor));
            }

        } catch (Exception e) {
            Log.e(TAG, "Error buscando animales por últimos 4 dígitos y explotación", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return lista;
    }

    public void buscarPorUltimos4DigitosYExplotacionAsync(String ultimos4, String idExplotacionUuid, ListaAnimalesCallback callback) {
        new Thread(() -> {
            try {
                List<Animal> lista = buscarPorUltimos4DigitosYExplotacion(ultimos4, idExplotacionUuid);
                callback.onSuccess(lista);
            } catch (Exception e) {
                Log.e(TAG, "Error en buscarPorUltimos4DigitosYExplotacionAsync", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public String descargarTodosLosAnimalesYDevolverUltimaFecha() {
        final int PAGE_SIZE = 1000;
        int offset = 0;
        String ultimaFechaRecibida = null;

        while (true) {
            try {
                String range = offset + "-" + (offset + PAGE_SIZE - 1);

                Log.d(TAG, "Descargando animales. Range: " + range);

                Response<List<Animal>> response = api.getAnimales(
                        range,
                        "items",
                        "*",
                        "fecha_actualizacion.asc"
                ).execute();

                if (!response.isSuccessful()) {
                    String error = response.errorBody() != null
                            ? response.errorBody().string()
                            : "sin detalle";
                    Log.e(TAG, "Error descargando animales iniciales. HTTP " + response.code() + " | " + error);
                    return null;
                }

                List<Animal> animales = response.body();

                if (animales == null || animales.isEmpty()) {
                    Log.d(TAG, "No hay más animales que descargar");
                    break;
                }

                boolean ok = insertarOActualizarAnimalesDesdeServidor(animales);
                if (!ok) {
                    Log.e(TAG, "Error guardando animales descargados en SQLite");
                    return null;
                }

                for (Animal animal : animales) {
                    if (animal.getFechaActualizacion() != null &&
                            (ultimaFechaRecibida == null ||
                                    animal.getFechaActualizacion().compareTo(ultimaFechaRecibida) > 0)) {
                        ultimaFechaRecibida = animal.getFechaActualizacion();
                    }
                }

                Log.d(TAG, "Página descargada: " + animales.size());

                if (animales.size() < PAGE_SIZE) {
                    break;
                }

                offset += PAGE_SIZE;

            } catch (IOException e) {
                Log.e(TAG, "Error de red en descarga inicial de animales", e);
                return null;
            } catch (Exception e) {
                Log.e(TAG, "Error general en descarga inicial de animales", e);
                return null;
            }
        }

        return ultimaFechaRecibida;
    }

    public int contarVacasParidas(String idExplotacionUuid, String paridera) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        int total = 0;

        try {
            String sql = "SELECT COUNT(DISTINCT crotal_madre) " +
                    "FROM animales " +
                    "WHERE id_explotacion_uuid = ? " +
                    "AND paridera = ? " +
                    "AND crotal_madre IS NOT NULL " +
                    "AND TRIM(crotal_madre) != ''";

            cursor = db.rawQuery(sql, new String[]{
                    idExplotacionUuid,
                    paridera
            });

            if (cursor.moveToFirst()) {
                total = cursor.getInt(0);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error contando vacas paridas", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return total;
    }

    public interface ProgresoParideraCallback {
        void onSuccess(int vacasParidas);
        void onError(String error);
    }

    public void contarVacasParidasAsync(String idExplotacionUuid, String paridera, ProgresoParideraCallback callback) {
        new Thread(() -> {
            try {
                int resultado = contarVacasParidas(idExplotacionUuid, paridera);
                callback.onSuccess(resultado);
            } catch (Exception e) {
                Log.e(TAG, "Error en contarVacasParidasAsync", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public List<Animal> obtenerCriasPorCrotalMadre(String crotalMadre) {
        List<Animal> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "SELECT a.*, e.nombre AS nombre_explotacion " +
                    "FROM animales a " +
                    "LEFT JOIN explotaciones e ON a.id_explotacion_uuid = e.id " +
                    "WHERE a.crotal_madre = ? " +
                    "AND a.eliminado = 0 " +
                    "ORDER BY a.fecha_nacimiento DESC";

            cursor = db.rawQuery(sql, new String[]{crotalMadre});

            while (cursor.moveToNext()) {
                lista.add(mapCursorToAnimal(cursor));
            }

        } catch (Exception e) {
            Log.e(TAG, "Error obteniendo crías por crotal madre: " + crotalMadre, e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return lista;
    }

    public void obtenerCriasPorCrotalMadreAsync(String crotalMadre, ListaAnimalesCallback callback) {
        new Thread(() -> {
            try {
                List<Animal> lista = obtenerCriasPorCrotalMadre(crotalMadre);
                callback.onSuccess(lista);
            } catch (Exception e) {
                Log.e(TAG, "Error en obtenerCriasPorCrotalMadreAsync", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public Animal buscarPorCrotal(String crotal) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        Animal animal = null;

        try {
            String sql = "SELECT a.*, e.nombre AS nombre_explotacion " +
                    "FROM animales a " +
                    "LEFT JOIN explotaciones e ON a.id_explotacion_uuid = e.id " +
                    "WHERE a.crotal = ? " +
                    "AND a.eliminado = 0 " +
                    "LIMIT 1";

            cursor = db.rawQuery(sql, new String[]{crotal});

            if (cursor.moveToFirst()) {
                animal = mapCursorToAnimal(cursor);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error buscando animal por crotal: " + crotal, e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return animal;
    }

    public void buscarPorCrotalAsync(String crotal, AnimalCallback callback) {
        new Thread(() -> {
            try {
                Animal animal = buscarPorCrotal(crotal);
                callback.onSuccess(animal);
            } catch (Exception e) {
                Log.e(TAG, "Error en buscarPorCrotalAsync", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public boolean actualizarDatosBasicosAnimal(String idAnimal, String capa, String raza, int estatus) {
        return dbHelper.actualizarDatosBasicosAnimal(idAnimal, capa, raza, estatus);
    }

    public boolean actualizarCrotalesAnimal(String idAnimal,
                                            boolean crotalIzquierdoPresente,
                                            boolean crotalDerechoPresente) {

        return dbHelper.actualizarCrotalesAnimal(
                idAnimal,
                crotalIzquierdoPresente,
                crotalDerechoPresente
        );
    }

    public boolean darDeBajaAnimal(String idAnimal, int estatusBaja, String fechaBaja) {
        return dbHelper.darDeBajaAnimal(idAnimal, estatusBaja, fechaBaja);
    }

    public boolean reactivarAnimal(String idAnimal, int estatus) {
        return dbHelper.reactivarAnimal(idAnimal, estatus);
    }

    public List<Animal> buscarActivosPorUltimos4DigitosYExplotacion(String ultimos4, String idExplotacionUuid) {
        List<Animal> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "SELECT a.*, e.nombre AS nombre_explotacion " +
                    "FROM animales a " +
                    "LEFT JOIN explotaciones e ON a.id_explotacion_uuid = e.id " +
                    "WHERE substr(a.crotal, -4) = ? " +
                    "AND a.id_explotacion_uuid = ? " +
                    "AND a.eliminado = 0 " +
                    "AND a.statecode = '1' " +
                    "ORDER BY a.crotal ASC";

            cursor = db.rawQuery(sql, new String[]{ultimos4, idExplotacionUuid});

            while (cursor.moveToNext()) {
                lista.add(mapCursorToAnimal(cursor));
            }

        } catch (Exception e) {
            Log.e(TAG, "Error buscando animales activos por últimos 4 dígitos y explotación", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return lista;
    }

    public void buscarActivosPorUltimos4DigitosYExplotacionAsync(String ultimos4, String idExplotacionUuid, ListaAnimalesCallback callback) {
        new Thread(() -> {
            try {
                List<Animal> lista = buscarActivosPorUltimos4DigitosYExplotacion(ultimos4, idExplotacionUuid);
                callback.onSuccess(lista);
            } catch (Exception e) {
                Log.e(TAG, "Error en buscarActivosPorUltimos4DigitosYExplotacionAsync", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }
}