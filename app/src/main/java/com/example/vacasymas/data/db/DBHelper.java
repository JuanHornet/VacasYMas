package com.example.vacasymas.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.data.models.Animal;
import com.example.vacasymas.data.models.AnimalEnLista;
import com.example.vacasymas.data.models.CrotalDisponible;
import com.example.vacasymas.data.models.DiagnosticoGestacion;
import com.example.vacasymas.data.models.EventoReproductivo;
import com.example.vacasymas.data.models.Explotacion;
import com.example.vacasymas.data.models.ListaAnimal;
import com.example.vacasymas.data.models.NotaAnimal;
import com.example.vacasymas.data.models.PesoAnimal;
import com.example.vacasymas.data.models.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "vacasymas.db";
    private static final int DATABASE_VERSION = 9;

    //diagnosticos gestacion
    public static final String ESTADO_REPRODUCTIVO_NADA = "nada";
    public static final String ESTADO_REPRODUCTIVO_VACIA = "vacia";
    public static final String ESTADO_REPRODUCTIVO_CUBIERTA = "cubierta";
    public static final String ESTADO_REPRODUCTIVO_PRENADA = "preñada";

    public static final String TABLA_LISTAS_ANIMALES = "listas_animales";
    public static final String TABLA_LISTA_ANIMALES_DETALLE = "lista_animales_detalle";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE explotaciones (" +
                "id TEXT PRIMARY KEY, " +
                "id_sharepoint INTEGER UNIQUE, " +
                "id_usuario TEXT, " +
                "nombre TEXT NOT NULL, " +
                "fecha_actualizacion TEXT NOT NULL, " +
                "sincronizado INTEGER NOT NULL DEFAULT 0, " +
                "eliminado INTEGER NOT NULL DEFAULT 0, " +
                "fecha_eliminado TEXT, " +
                "FOREIGN KEY(id_usuario) REFERENCES usuarios(id)" +
                ");");

        db.execSQL("CREATE TABLE animales (" +
                "id TEXT PRIMARY KEY, " +
                "id_sharepoint INTEGER, " +
                "crotal TEXT NOT NULL UNIQUE, " +
                "id_explotacion_uuid TEXT, " +
                "estatus INTEGER, " +
                "fecha_nacimiento TEXT, " +
                "raza TEXT, " +
                "sexo TEXT, " +
                "crotal_madre TEXT, " +
                "capa TEXT, " +
                "cercado TEXT, " +
                "id_cercado_historico INTEGER, " +
                "paridera TEXT, " +
                "alta_gestionada TEXT, " +
                "statecode TEXT, " +
                "factor TEXT, " +
                "fecha_baja_explotacion TEXT, " +
                "causa_alta TEXT, " +
                "explotacion_nacimiento TEXT, " +
                "check_paridera TEXT, " +
                "ischosen TEXT, " +
                "estado_reproductivo TEXT DEFAULT 'nada', " +
                "crotal_izquierdo_presente INTEGER NOT NULL DEFAULT 1, " +
                "crotal_derecho_presente INTEGER NOT NULL DEFAULT 1, " +
                "fecha_actualizacion TEXT NOT NULL, " +
                "sincronizado INTEGER NOT NULL DEFAULT 0, " +
                "eliminado INTEGER NOT NULL DEFAULT 0, " +
                "fecha_eliminado TEXT, " +
                "FOREIGN KEY(id_explotacion_uuid) REFERENCES explotaciones(id)" +
                ");");

        db.execSQL("CREATE INDEX idx_animales_crotal ON animales(crotal);");
        db.execSQL("CREATE INDEX idx_animales_id_explotacion_uuid ON animales(id_explotacion_uuid);");
        db.execSQL("CREATE INDEX idx_animales_estatus ON animales(estatus);");
        db.execSQL("CREATE INDEX idx_animales_fecha_actualizacion ON animales(fecha_actualizacion);");

        db.execSQL("CREATE TABLE usuarios (" +
                "id TEXT PRIMARY KEY, " +
                "email TEXT NOT NULL UNIQUE, " +
                "nombre TEXT, " +
                "password TEXT, " +
                "fecha_actualizacion TEXT NOT NULL, " +
                "sincronizado INTEGER NOT NULL DEFAULT 0, " +
                "eliminado INTEGER NOT NULL DEFAULT 0, " +
                "fecha_eliminado TEXT" +
                ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS diagnosticos_gestacion (" +
                "id TEXT PRIMARY KEY, " +
                "id_animal TEXT NOT NULL, " +
                "id_explotacion_uuid TEXT NOT NULL, " +
                "fecha TEXT NOT NULL, " +
                "resultado TEXT NOT NULL, " +
                "observaciones TEXT, " +
                "sincronizado INTEGER DEFAULT 0, " +
                "eliminado INTEGER DEFAULT 0, " +
                "fecha_actualizacion TEXT, " +
                "fecha_eliminado TEXT" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS eventos_reproductivos (" +
                "id TEXT PRIMARY KEY, " +
                "id_madre TEXT NOT NULL, " +
                "id_cria TEXT, " +
                "id_explotacion_uuid TEXT NOT NULL, " +
                "tipo_evento TEXT NOT NULL, " +
                "fecha_evento TEXT NOT NULL, " +
                "resultado_cria TEXT, " +
                "cria_identificada INTEGER NOT NULL DEFAULT 0, " +
                "sexo_estimado TEXT, " +
                "raza_estimada TEXT, " +
                "capa_estimada TEXT, " +
                "cercado TEXT, " +
                "observaciones TEXT, " +
                "sincronizado INTEGER DEFAULT 0, " +
                "eliminado INTEGER DEFAULT 0, " +
                "fecha_actualizacion TEXT, " +
                "fecha_eliminado TEXT, " +
                "FOREIGN KEY(id_madre) REFERENCES animales(id), " +
                "FOREIGN KEY(id_cria) REFERENCES animales(id), " +
                "FOREIGN KEY(id_explotacion_uuid) REFERENCES explotaciones(id)" +
                ");");

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_eventos_reproductivos_madre " +
                "ON eventos_reproductivos(id_madre);");

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_eventos_reproductivos_cria " +
                "ON eventos_reproductivos(id_cria);");

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_eventos_reproductivos_explotacion " +
                "ON eventos_reproductivos(id_explotacion_uuid);");

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_eventos_reproductivos_fecha " +
                "ON eventos_reproductivos(fecha_evento);");

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_eventos_reproductivos_pendientes " +
                "ON eventos_reproductivos(cria_identificada, resultado_cria);");

        db.execSQL("CREATE TABLE IF NOT EXISTS notas_animales (" +
                "id TEXT PRIMARY KEY, " +
                "id_animal TEXT NOT NULL, " +
                "fecha TEXT NOT NULL, " +
                "texto TEXT NOT NULL, " +
                "sincronizado INTEGER DEFAULT 0, " +
                "eliminado INTEGER DEFAULT 0, " +
                "fecha_actualizacion TEXT, " +
                "fecha_eliminado TEXT" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS pesos_animales (" +
                "id TEXT PRIMARY KEY, " +
                "id_animal TEXT NOT NULL, " +
                "id_explotacion_uuid TEXT NOT NULL, " +
                "crotal TEXT, " +
                "sexo TEXT, " +
                "fecha TEXT NOT NULL, " +
                "peso INTEGER NOT NULL, " +
                "observaciones TEXT, " +
                "sincronizado INTEGER DEFAULT 0, " +
                "eliminado INTEGER DEFAULT 0, " +
                "fecha_actualizacion TEXT, " +
                "fecha_eliminado TEXT, " +
                "UNIQUE(id_animal, fecha)" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS crotales_disponibles (" +
                "id TEXT PRIMARY KEY, " +
                "crotal TEXT NOT NULL UNIQUE, " +
                "id_explotacion_uuid TEXT NOT NULL, " +
                "estado TEXT NOT NULL DEFAULT 'DISPONIBLE', " +
                "fecha_asignacion TEXT, " +
                "fecha_uso TEXT, " +
                "id_animal_usado TEXT, " +
                "observaciones TEXT, " +
                "fecha_actualizacion TEXT NOT NULL, " +
                "sincronizado INTEGER NOT NULL DEFAULT 0, " +
                "eliminado INTEGER NOT NULL DEFAULT 0, " +
                "fecha_eliminado TEXT" +
                 ")");

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_crotales_explotacion " +
                "ON crotales_disponibles(id_explotacion_uuid)");

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_crotales_estado " +
                "ON crotales_disponibles(estado)");

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_crotales_fecha_actualizacion " +
                "ON crotales_disponibles(fecha_actualizacion)");

        db.execSQL("CREATE TABLE IF NOT EXISTS listas_animales (" +
                "id TEXT PRIMARY KEY, " +
                "id_explotacion_uuid TEXT NOT NULL, " +
                "nombre TEXT NOT NULL, " +
                "tipo TEXT, " +
                "observaciones TEXT, " +
                "fecha_creacion TEXT, " +
                "sincronizado INTEGER DEFAULT 0, " +
                "eliminado INTEGER DEFAULT 0, " +
                "fecha_actualizacion TEXT, " +
                "fecha_eliminado TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS lista_animales_detalle (" +
                "id TEXT PRIMARY KEY, " +
                "id_lista TEXT NOT NULL, " +
                "id_animal TEXT NOT NULL, " +
                "crotal TEXT, " +
                "sexo TEXT, " +
                "marcado INTEGER DEFAULT 0, " +
                "fecha_alta TEXT, " +
                "observaciones TEXT, " +
                "sincronizado INTEGER DEFAULT 0, " +
                "eliminado INTEGER DEFAULT 0, " +
                "fecha_actualizacion TEXT, " +
                "fecha_eliminado TEXT, " +
                "UNIQUE(id_lista, id_animal))");

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_listas_animales_explotacion " +
                "ON listas_animales(id_explotacion_uuid)");

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_listas_animales_eliminado " +
                "ON listas_animales(eliminado)");

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_lista_animales_detalle_lista " +
                "ON lista_animales_detalle(id_lista)");

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_lista_animales_detalle_animal " +
                "ON lista_animales_detalle(id_animal)");

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_lista_animales_detalle_eliminado " +
                "ON lista_animales_detalle(eliminado)");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS animales");
        db.execSQL("DROP TABLE IF EXISTS explotaciones");
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS diagnosticos_gestacion");
        db.execSQL("DROP TABLE IF EXISTS notas_animales");
        db.execSQL("DROP TABLE IF EXISTS pesos_animales");
        db.execSQL("DROP TABLE IF EXISTS eventos_reproductivos");
        db.execSQL("DROP TABLE IF EXISTS crotales_disponibles");
        db.execSQL("DROP TABLE IF EXISTS lista_animales_detalle");
        db.execSQL("DROP TABLE IF EXISTS listas_animales");
        if (oldVersion < DATABASE_VERSION) {
            db.execSQL("ALTER TABLE lista_animales_detalle ADD COLUMN marcado INTEGER DEFAULT 0");
        }
        onCreate(db);
    }


    public void insertarOActualizarExplotacion(Explotacion e) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", e.getId());
        values.put("id_sharepoint", e.getIdSharepoint());
        values.put("id_usuario", e.getIdUsuario());
        values.put("nombre", e.getNombre());
        values.put("fecha_actualizacion", e.getFechaActualizacion());
        values.put("sincronizado", e.getSincronizado() != null ? e.getSincronizado() : 1);
        values.put("eliminado", e.getEliminado() != null ? e.getEliminado() : 0);
        values.put("fecha_eliminado", e.getFechaEliminado());

        db.insertWithOnConflict("explotaciones", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void insertarOActualizarAnimal(Animal a) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", a.getId());
        values.put("id_sharepoint", a.getIdSharepoint());
        values.put("crotal", a.getCrotal());
        values.put("id_explotacion_uuid", a.getIdExplotacionUuid());
        values.put("estatus", a.getEstatus());
        values.put("fecha_nacimiento", a.getFechaNacimiento());
        values.put("raza", a.getRaza());
        values.put("sexo", a.getSexo());
        values.put("crotal_madre", a.getCrotalMadre());
        values.put("capa", a.getCapa());
        values.put("cercado", a.getCercado());
        values.put("id_cercado_historico", a.getIdCercadoHistorico());
        values.put("paridera", a.getParidera());
        values.put("alta_gestionada", a.getAltaGestionada());
        values.put("statecode", a.getStatecode());
        values.put("factor", a.getFactor());
        values.put("fecha_baja_explotacion", a.getFechaBajaExplotacion());
        values.put("causa_alta", a.getCausaAlta());
        values.put("explotacion_nacimiento", a.getExplotacionNacimiento());
        values.put("check_paridera", a.getCheckParidera());
        values.put("ischosen", a.getIschosen());
        values.put("estado_reproductivo",
                a.getEstadoReproductivo() != null ? a.getEstadoReproductivo() : "nada");
        values.put("crotal_izquierdo_presente", Boolean.TRUE.equals(a.getCrotalIzquierdoPresente()) ? 1 : 0);
        values.put("crotal_derecho_presente", Boolean.TRUE.equals(a.getCrotalDerechoPresente()) ? 1 : 0);
        values.put("fecha_actualizacion", a.getFechaActualizacion());
        values.put("sincronizado", a.getSincronizado() != null ? a.getSincronizado() : 1);
        values.put("eliminado", a.getEliminado() != null ? a.getEliminado() : 0);
        values.put("fecha_eliminado", a.getFechaEliminado());


        db.insertWithOnConflict("animales", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public int contarAnimales() {
        SQLiteDatabase db = this.getReadableDatabase();
        android.database.Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM animales", null);

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return total;
    }

    public String obtenerMaxFechaActualizacionAnimales() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String fecha = null;

        try {
            cursor = db.rawQuery("SELECT MAX(fecha_actualizacion) FROM animales", null);
            if (cursor.moveToFirst()) {
                fecha = cursor.getString(0);
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return fecha;
    }

    public List<Animal> obtenerAnimalesPorExplotacion(String idExplotacion) {
        List<com.example.vacasymas.data.models.Animal> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM animales " +
                        "WHERE id_explotacion_uuid = ? AND eliminado = 0 " +
                        "ORDER BY crotal ASC",
                new String[]{idExplotacion}
        );

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorToAnimal(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }

    public List<Animal> obtenerAnimalesNoSincronizados() {
        List<com.example.vacasymas.data.models.Animal> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM animales WHERE sincronizado = 0",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorToAnimal(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }

    public int marcarAnimalComoSincronizado(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sincronizado", 1);

        return db.update("animales", values, "id = ?", new String[]{id});
    }

    public int eliminarAnimalLogico(String idAnimal, String fechaEliminado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("eliminado", 1);
        values.put("sincronizado", 0);
        values.put("fecha_eliminado", fechaEliminado);

        return db.update("animales", values, "id = ?", new String[]{idAnimal});
    }

    private Animal cursorToAnimal(Cursor cursor) {
        Animal animal = new Animal();

        animal.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));

        if (!cursor.isNull(cursor.getColumnIndexOrThrow("id_sharepoint"))) {
            animal.setIdSharepoint(cursor.getLong(cursor.getColumnIndexOrThrow("id_sharepoint")));
        }

        animal.setCrotal(cursor.getString(cursor.getColumnIndexOrThrow("crotal")));
        animal.setIdExplotacionUuid(cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion_uuid")));

        if (!cursor.isNull(cursor.getColumnIndexOrThrow("estatus"))) {
            animal.setEstatus(cursor.getInt(cursor.getColumnIndexOrThrow("estatus")));
        }

        animal.setFechaNacimiento(cursor.getString(cursor.getColumnIndexOrThrow("fecha_nacimiento")));
        animal.setRaza(cursor.getString(cursor.getColumnIndexOrThrow("raza")));
        animal.setSexo(cursor.getString(cursor.getColumnIndexOrThrow("sexo")));
        animal.setCrotalMadre(cursor.getString(cursor.getColumnIndexOrThrow("crotal_madre")));
        animal.setCapa(cursor.getString(cursor.getColumnIndexOrThrow("capa")));
        animal.setCercado(cursor.getString(cursor.getColumnIndexOrThrow("cercado")));

        if (!cursor.isNull(cursor.getColumnIndexOrThrow("id_cercado_historico"))) {
            animal.setIdCercadoHistorico(cursor.getLong(cursor.getColumnIndexOrThrow("id_cercado_historico")));
        }

        animal.setParidera(cursor.getString(cursor.getColumnIndexOrThrow("paridera")));
        animal.setAltaGestionada(cursor.getString(cursor.getColumnIndexOrThrow("alta_gestionada")));
        animal.setStatecode(cursor.getString(cursor.getColumnIndexOrThrow("statecode")));
        animal.setFactor(cursor.getString(cursor.getColumnIndexOrThrow("factor")));
        animal.setFechaBajaExplotacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_baja_explotacion")));
        animal.setCausaAlta(cursor.getString(cursor.getColumnIndexOrThrow("causa_alta")));
        animal.setExplotacionNacimiento(cursor.getString(cursor.getColumnIndexOrThrow("explotacion_nacimiento")));
        animal.setCheckParidera(cursor.getString(cursor.getColumnIndexOrThrow("check_paridera")));
        animal.setIschosen(cursor.getString(cursor.getColumnIndexOrThrow("ischosen")));
        animal.setEstadoReproductivo(cursor.getString(cursor.getColumnIndexOrThrow("estado_reproductivo")));
        animal.setCrotalIzquierdoPresente(cursor.getInt(cursor.getColumnIndexOrThrow("crotal_izquierdo_presente")) == 1);
        animal.setCrotalDerechoPresente(cursor.getInt(cursor.getColumnIndexOrThrow("crotal_derecho_presente")) == 1);

        animal.setFechaActualizacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")));
        animal.setSincronizado(cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")));
        animal.setEliminado(cursor.getInt(cursor.getColumnIndexOrThrow("eliminado")));
        animal.setFechaEliminado(cursor.getString(cursor.getColumnIndexOrThrow("fecha_eliminado")));


        return animal;
    }

    public String obtenerMaxFechaActualizacionExplotaciones() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(fecha_actualizacion) FROM explotaciones", null);

        String fecha = null;
        if (cursor.moveToFirst()) {
            fecha = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return fecha;
    }

    public java.util.List<Explotacion> obtenerListaExplotacionesActivasPorUsuario(String idUsuario) {
        java.util.List<Explotacion> lista = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, id_sharepoint, id_usuario, nombre, fecha_actualizacion, sincronizado, eliminado, fecha_eliminado " +
                        "FROM explotaciones " +
                        "WHERE eliminado = 0 AND id_usuario = ? " +
                        "ORDER BY nombre ASC",
                new String[]{idUsuario}
        );

        try {
            while (cursor.moveToNext()) {
                com.example.vacasymas.data.models.Explotacion e = new com.example.vacasymas.data.models.Explotacion();

                e.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));

                int idxIdSharepoint = cursor.getColumnIndex("id_sharepoint");
                if (!cursor.isNull(idxIdSharepoint)) {
                    e.setIdSharepoint(cursor.getLong(idxIdSharepoint));
                }

                int idxIdUsuario = cursor.getColumnIndex("id_usuario");
                if (!cursor.isNull(idxIdUsuario)) {
                    e.setIdUsuario(cursor.getString(idxIdUsuario));
                }

                e.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                e.setFechaActualizacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")));
                e.setSincronizado(cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")));
                e.setEliminado(cursor.getInt(cursor.getColumnIndexOrThrow("eliminado")));

                int idxFechaEliminado = cursor.getColumnIndex("fecha_eliminado");
                if (!cursor.isNull(idxFechaEliminado)) {
                    e.setFechaEliminado(cursor.getString(idxFechaEliminado));
                }

                lista.add(e);
            }
        } finally {
            cursor.close();
            db.close();
        }

        return lista;
    }

    public long insertarExplotacionLocal(com.example.vacasymas.data.models.Explotacion e) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", e.getId());
        values.put("id_sharepoint", e.getIdSharepoint());
        values.put("id_usuario", e.getIdUsuario());
        values.put("nombre", e.getNombre());
        values.put("fecha_actualizacion", e.getFechaActualizacion());
        values.put("sincronizado", 0);
        values.put("eliminado", 0);
        values.put("fecha_eliminado", (String) null);

        long resultado = db.insert("explotaciones", null, values);
        db.close();
        return resultado;
    }

    public List<Explotacion> obtenerExplotacionesNoSincronizadas() {
        List<Explotacion> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, id_sharepoint, id_usuario, nombre, fecha_actualizacion, sincronizado, eliminado, fecha_eliminado " +
                        "FROM explotaciones WHERE sincronizado = 0",
                null
        );

        try {
            while (cursor.moveToNext()) {
                Explotacion e = new Explotacion();

                e.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));

                int idxIdSharepoint = cursor.getColumnIndex("id_sharepoint");
                if (!cursor.isNull(idxIdSharepoint)) {
                    e.setIdSharepoint(cursor.getLong(idxIdSharepoint));
                }

                e.setIdUsuario(cursor.getString(cursor.getColumnIndexOrThrow("id_usuario")));
                e.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                e.setFechaActualizacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")));
                e.setSincronizado(cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")));
                e.setEliminado(cursor.getInt(cursor.getColumnIndexOrThrow("eliminado")));

                int idxFechaEliminado = cursor.getColumnIndex("fecha_eliminado");
                if (!cursor.isNull(idxFechaEliminado)) {
                    e.setFechaEliminado(cursor.getString(idxFechaEliminado));
                }

                lista.add(e);
            }
        } finally {
            cursor.close();
            db.close();
        }

        return lista;
    }

    public void marcarExplotacionComoSincronizada(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("sincronizado", 1);

        db.update("explotaciones", values, "id = ?", new String[]{id});
        db.close();
    }

    public void insertarOActualizarUsuario(com.example.vacasymas.data.models.Usuario u) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", u.getId());
        values.put("email", u.getEmail());
        values.put("nombre", u.getNombre());
        values.put("password", u.getPassword());
        values.put("fecha_actualizacion", u.getFechaActualizacion());
        values.put("sincronizado", u.getSincronizado() != null ? u.getSincronizado() : 1);
        values.put("eliminado", u.getEliminado() != null ? u.getEliminado() : 0);
        values.put("fecha_eliminado", u.getFechaEliminado());

        db.insertWithOnConflict("usuarios", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id, email, nombre, password, fecha_actualizacion, sincronizado, eliminado, fecha_eliminado " +
                        "FROM usuarios WHERE email = ? AND eliminado = 0",
                new String[]{email}
        );

        com.example.vacasymas.data.models.Usuario usuario = null;

        try {
            if (cursor.moveToFirst()) {
                usuario = new com.example.vacasymas.data.models.Usuario();
                usuario.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                usuario.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                usuario.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));

                int idxPassword = cursor.getColumnIndex("password");
                if (!cursor.isNull(idxPassword)) {
                    usuario.setPassword(cursor.getString(idxPassword));
                }

                usuario.setFechaActualizacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")));
                usuario.setSincronizado(cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")));
                usuario.setEliminado(cursor.getInt(cursor.getColumnIndexOrThrow("eliminado")));

                int idxFechaEliminado = cursor.getColumnIndex("fecha_eliminado");
                if (!cursor.isNull(idxFechaEliminado)) {
                    usuario.setFechaEliminado(cursor.getString(idxFechaEliminado));
                }
            }
        } finally {
            cursor.close();
            db.close();
        }

        return usuario;
    }

    //*****************DIAGNOSTICOS GESTACION***************************************************
    //******************************************************************************************

    public boolean insertarOActualizarDiagnosticoGestacionHoy(
            String idAnimal,
            String idExplotacionUuid,
            String fecha,
            String resultado,
            String observaciones
    ) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        try {
            String fechaActualizacion = com.example.vacasymas.base.FechaUtils.ahoraIso();

            cursor = db.rawQuery(
                    "SELECT id FROM diagnosticos_gestacion " +
                            "WHERE id_animal = ? " +
                            "AND id_explotacion_uuid = ? " +
                            "AND fecha = ? " +
                            "AND eliminado = 0 " +
                            "LIMIT 1",
                    new String[]{idAnimal, idExplotacionUuid, fecha}
            );

            ContentValues values = new ContentValues();
            values.put("resultado", resultado);
            values.put("observaciones", observaciones);
            values.put("sincronizado", 0);
            values.put("fecha_actualizacion", fechaActualizacion);

            boolean ok;

            if (cursor.moveToFirst()) {
                String idDiagnostico = cursor.getString(0);

                int filas = db.update(
                        "diagnosticos_gestacion",
                        values,
                        "id = ?",
                        new String[]{idDiagnostico}
                );

                ok = filas > 0;

            } else {
                values.put("id", java.util.UUID.randomUUID().toString());
                values.put("id_animal", idAnimal);
                values.put("id_explotacion_uuid", idExplotacionUuid);
                values.put("fecha", fecha);
                values.put("eliminado", 0);

                long insert = db.insert("diagnosticos_gestacion", null, values);
                ok = insert != -1;
            }

            if (ok) {
                actualizarEstadoReproductivoAnimal(idAnimal, resultado);
            }

            return ok;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public boolean actualizarEstadoReproductivoAnimal(String idAnimal, String estadoReproductivo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("estado_reproductivo", estadoReproductivo);
        values.put("sincronizado", 0);
        values.put("fecha_actualizacion", com.example.vacasymas.base.FechaUtils.ahoraIso());

        int filas = db.update(
                "animales",
                values,
                "id = ?",
                new String[]{idAnimal}
        );

        return filas > 0;
    }

    public ArrayList<Animal> obtenerVacasPorExplotacion(String idExplotacionUuid) {
        ArrayList<Animal> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, crotal, id_explotacion_uuid, estatus, fecha_nacimiento, raza, sexo, " +
                        "crotal_madre, capa, cercado, paridera, estado_reproductivo, " +
                        "crotal_izquierdo_presente, crotal_derecho_presente, fecha_actualizacion, " +
                        "sincronizado, eliminado, fecha_eliminado " +
                        "FROM animales " +
                        "WHERE id_explotacion_uuid = ? " +
                        "AND estatus = 10003 " +
                        "AND eliminado = 0 " +
                        "ORDER BY crotal ASC",
                new String[]{idExplotacionUuid}
        );

        if (cursor.moveToFirst()) {
            do {
                Animal animal = new Animal();

                animal.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                animal.setCrotal(cursor.getString(cursor.getColumnIndexOrThrow("crotal")));
                animal.setIdExplotacionUuid(cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion_uuid")));
                animal.setEstatus(cursor.getInt(cursor.getColumnIndexOrThrow("estatus")));
                animal.setFechaNacimiento(cursor.getString(cursor.getColumnIndexOrThrow("fecha_nacimiento")));
                animal.setRaza(cursor.getString(cursor.getColumnIndexOrThrow("raza")));
                animal.setSexo(cursor.getString(cursor.getColumnIndexOrThrow("sexo")));
                animal.setCrotalMadre(cursor.getString(cursor.getColumnIndexOrThrow("crotal_madre")));
                animal.setCapa(cursor.getString(cursor.getColumnIndexOrThrow("capa")));
                animal.setCercado(cursor.getString(cursor.getColumnIndexOrThrow("cercado")));
                animal.setParidera(cursor.getString(cursor.getColumnIndexOrThrow("paridera")));
                animal.setEstadoReproductivo(cursor.getString(cursor.getColumnIndexOrThrow("estado_reproductivo")));

                animal.setCrotalIzquierdoPresente(cursor.getInt(cursor.getColumnIndexOrThrow("crotal_izquierdo_presente")) == 1);
                animal.setCrotalDerechoPresente(cursor.getInt(cursor.getColumnIndexOrThrow("crotal_derecho_presente")) == 1);

                animal.setFechaActualizacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")));
                animal.setSincronizado(cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")));
                animal.setEliminado(cursor.getInt(cursor.getColumnIndexOrThrow("eliminado")));
                animal.setFechaEliminado(cursor.getString(cursor.getColumnIndexOrThrow("fecha_eliminado")));

                lista.add(animal);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }

    public ArrayList<Animal> buscarVacasPorUltimos4YExplotacion(String ultimos4, String idExplotacionUuid) {
        ArrayList<Animal> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM animales " +
                        "WHERE substr(crotal, -4) = ? " +
                        "AND id_explotacion_uuid = ? " +
                        "AND estatus = 10003 " +
                        "AND eliminado = 0 " +
                        "ORDER BY crotal ASC",
                new String[]{ultimos4, idExplotacionUuid}
        );

        if (cursor.moveToFirst()) {
            do {
                Animal animal = new Animal();

                animal.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                animal.setCrotal(cursor.getString(cursor.getColumnIndexOrThrow("crotal")));
                animal.setIdExplotacionUuid(cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion_uuid")));
                animal.setEstatus(cursor.getInt(cursor.getColumnIndexOrThrow("estatus")));
                animal.setFechaNacimiento(cursor.getString(cursor.getColumnIndexOrThrow("fecha_nacimiento")));
                animal.setRaza(cursor.getString(cursor.getColumnIndexOrThrow("raza")));
                animal.setSexo(cursor.getString(cursor.getColumnIndexOrThrow("sexo")));
                animal.setEstadoReproductivo(cursor.getString(cursor.getColumnIndexOrThrow("estado_reproductivo")));

                lista.add(animal);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }

    public int contarDiagnosticosGestacionHoy(String idExplotacionUuid, String fecha) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM diagnosticos_gestacion " +
                            "WHERE id_explotacion_uuid = ? " +
                            "AND fecha = ? " +
                            "AND eliminado = 0",
                    new String[]{idExplotacionUuid, fecha}
            );

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }

        } finally {
            if (cursor != null) cursor.close();
        }

        return 0;
    }

    public ArrayList<Animal> obtenerVacasDiagnosticadasHoy(String idExplotacionUuid, String fecha) {
        ArrayList<Animal> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT a.id, a.crotal, a.id_explotacion_uuid, a.estatus, " +
                            "d.resultado AS estado_diagnostico " +
                            "FROM diagnosticos_gestacion d " +
                            "INNER JOIN animales a ON a.id = d.id_animal " +
                            "WHERE d.id_explotacion_uuid = ? " +
                            "AND d.fecha = ? " +
                            "AND d.eliminado = 0 " +
                            "ORDER BY d.fecha_actualizacion DESC",
                    new String[]{idExplotacionUuid, fecha}
            );

            if (cursor.moveToFirst()) {
                do {
                    Animal animal = new Animal();
                    animal.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                    animal.setCrotal(cursor.getString(cursor.getColumnIndexOrThrow("crotal")));
                    animal.setIdExplotacionUuid(cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion_uuid")));
                    animal.setEstatus(cursor.getInt(cursor.getColumnIndexOrThrow("estatus")));
                    animal.setEstadoReproductivo(cursor.getString(cursor.getColumnIndexOrThrow("estado_diagnostico")));

                    lista.add(animal);

                } while (cursor.moveToNext());
            }

        } finally {
            if (cursor != null) cursor.close();
        }

        return lista;
    }

    public ArrayList<DiagnosticoGestacion> obtenerDiagnosticosGestacionNoSincronizados() {
        ArrayList<DiagnosticoGestacion> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM diagnosticos_gestacion WHERE sincronizado = 0 ORDER BY fecha_actualizacion ASC",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                DiagnosticoGestacion d = new DiagnosticoGestacion();

                d.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                d.setIdAnimal(cursor.getString(cursor.getColumnIndexOrThrow("id_animal")));
                d.setIdExplotacionUuid(cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion_uuid")));
                d.setFecha(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
                d.setResultado(cursor.getString(cursor.getColumnIndexOrThrow("resultado")));
                d.setObservaciones(cursor.getString(cursor.getColumnIndexOrThrow("observaciones")));
                d.setSincronizado(cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")));
                d.setEliminado(cursor.getInt(cursor.getColumnIndexOrThrow("eliminado")));
                d.setFechaActualizacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")));
                d.setFechaEliminado(cursor.getString(cursor.getColumnIndexOrThrow("fecha_eliminado")));

                lista.add(d);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }

    public boolean marcarDiagnosticoGestacionComoSincronizado(String idDiagnostico) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("sincronizado", 1);

        int filas = db.update(
                "diagnosticos_gestacion",
                values,
                "id = ?",
                new String[]{idDiagnostico}
        );

        return filas > 0;
    }

    public ArrayList<DiagnosticoGestacion> obtenerDiagnosticosGestacionPorAnimal(String idAnimal) {
        ArrayList<DiagnosticoGestacion> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT * FROM diagnosticos_gestacion " +
                            "WHERE id_animal = ? " +
                            "AND eliminado = 0 " +
                            "ORDER BY fecha DESC",
                    new String[]{idAnimal}
            );

            if (cursor.moveToFirst()) {
                do {
                    DiagnosticoGestacion d = new DiagnosticoGestacion();

                    d.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                    d.setIdAnimal(cursor.getString(cursor.getColumnIndexOrThrow("id_animal")));
                    d.setIdExplotacionUuid(cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion_uuid")));
                    d.setFecha(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
                    d.setResultado(cursor.getString(cursor.getColumnIndexOrThrow("resultado")));
                    d.setObservaciones(cursor.getString(cursor.getColumnIndexOrThrow("observaciones")));
                    d.setSincronizado(cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")));
                    d.setEliminado(cursor.getInt(cursor.getColumnIndexOrThrow("eliminado")));
                    d.setFechaActualizacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")));
                    d.setFechaEliminado(cursor.getString(cursor.getColumnIndexOrThrow("fecha_eliminado")));

                    lista.add(d);

                } while (cursor.moveToNext());
            }

        } finally {
            if (cursor != null) cursor.close();
        }

        return lista;
    }

    public boolean insertarOActualizarDiagnosticoGestacionDesdeServidor(DiagnosticoGestacion d) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", d.getId());
        values.put("id_animal", d.getIdAnimal());
        values.put("id_explotacion_uuid", d.getIdExplotacionUuid());
        values.put("fecha", d.getFecha());
        values.put("resultado", d.getResultado());
        values.put("observaciones", d.getObservaciones());
        values.put("sincronizado", 1);
        values.put("eliminado", d.getEliminado() != null ? d.getEliminado() : 0);
        values.put("fecha_actualizacion", d.getFechaActualizacion());
        values.put("fecha_eliminado", d.getFechaEliminado());

        long result = db.insertWithOnConflict(
                "diagnosticos_gestacion",
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );

        return result != -1;
    }

    public boolean eliminarDiagnosticoGestacionLogico(String idDiagnostico) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("eliminado", 1);
        values.put("sincronizado", 0);
        values.put("fecha_eliminado", FechaUtils.ahoraIso());
        values.put("fecha_actualizacion", FechaUtils.ahoraIso());

        int filas = db.update(
                "diagnosticos_gestacion",
                values,
                "id = ?",
                new String[]{idDiagnostico}
        );

        return filas > 0;
    }

    public ArrayList<String> obtenerFechasDiagnosticosGestacion(String idExplotacionUuid) {
        ArrayList<String> fechas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT fecha FROM diagnosticos_gestacion " +
                        "WHERE id_explotacion_uuid = ? " +
                        "AND eliminado = 0 " +
                        "ORDER BY fecha DESC",
                new String[]{idExplotacionUuid}
        );

        if (cursor.moveToFirst()) {
            do {
                fechas.add(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return fechas;
    }

     // ******************FIN DIAGNOSTICOS GESTACION******************************
    //****************************************************************************

    public ArrayList<NotaAnimal> obtenerNotasPorAnimal(String idAnimal) {
        ArrayList<NotaAnimal> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT * FROM notas_animales " +
                            "WHERE id_animal = ? " +
                            "AND eliminado = 0 " +
                            "ORDER BY fecha DESC, fecha_actualizacion DESC",
                    new String[]{idAnimal}
            );

            if (cursor.moveToFirst()) {
                do {
                    NotaAnimal n = new NotaAnimal();

                    n.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                    n.setIdAnimal(cursor.getString(cursor.getColumnIndexOrThrow("id_animal")));
                    n.setFecha(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
                    n.setTexto(cursor.getString(cursor.getColumnIndexOrThrow("texto")));
                    n.setSincronizado(cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")));
                    n.setEliminado(cursor.getInt(cursor.getColumnIndexOrThrow("eliminado")));
                    n.setFechaActualizacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")));
                    n.setFechaEliminado(cursor.getString(cursor.getColumnIndexOrThrow("fecha_eliminado")));

                    lista.add(n);

                } while (cursor.moveToNext());
            }

        } finally {
            if (cursor != null) cursor.close();
        }

        return lista;
    }

    public boolean insertarNotaAnimal(String idAnimal, String texto) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", UUID.randomUUID().toString());
        values.put("id_animal", idAnimal);
        values.put("fecha", FechaUtils.hoy());
        values.put("texto", texto);
        values.put("sincronizado", 0);
        values.put("eliminado", 0);
        values.put("fecha_actualizacion", FechaUtils.ahoraIso());
        values.putNull("fecha_eliminado");

        long result = db.insert("notas_animales", null, values);
        return result != -1;
    }

    public boolean eliminarNotaAnimalLogico(String idNota) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("eliminado", 1);
        values.put("sincronizado", 0);
        values.put("fecha_eliminado", FechaUtils.ahoraIso());
        values.put("fecha_actualizacion", FechaUtils.ahoraIso());

        int filas = db.update(
                "notas_animales",
                values,
                "id = ?",
                new String[]{idNota}
        );

        return filas > 0;
    }

    public ArrayList<NotaAnimal> obtenerNotasAnimalesNoSincronizadas() {
        ArrayList<NotaAnimal> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM notas_animales WHERE sincronizado = 0 ORDER BY fecha_actualizacion ASC",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                NotaAnimal n = new NotaAnimal();

                n.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                n.setIdAnimal(cursor.getString(cursor.getColumnIndexOrThrow("id_animal")));
                n.setFecha(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
                n.setTexto(cursor.getString(cursor.getColumnIndexOrThrow("texto")));
                n.setSincronizado(cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")));
                n.setEliminado(cursor.getInt(cursor.getColumnIndexOrThrow("eliminado")));
                n.setFechaActualizacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")));
                n.setFechaEliminado(cursor.getString(cursor.getColumnIndexOrThrow("fecha_eliminado")));

                lista.add(n);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }

    public boolean insertarOActualizarNotaAnimalDesdeServidor(NotaAnimal n) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", n.getId());
        values.put("id_animal", n.getIdAnimal());
        values.put("fecha", n.getFecha());
        values.put("texto", n.getTexto());
        values.put("sincronizado", 1);
        values.put("eliminado", n.getEliminado() != null ? n.getEliminado() : 0);
        values.put("fecha_actualizacion", n.getFechaActualizacion());
        values.put("fecha_eliminado", n.getFechaEliminado());

        long result = db.insertWithOnConflict(
                "notas_animales",
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );

        return result != -1;
    }

    public boolean marcarNotaAnimalComoSincronizada(String idNota) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("sincronizado", 1);

        int filas = db.update(
                "notas_animales",
                values,
                "id = ?",
                new String[]{idNota}
        );

        return filas > 0;
    }

    public boolean actualizarDatosBasicosAnimal(String idAnimal, String capa, String raza, int estatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("capa", capa);
        values.put("raza", raza);
        values.put("estatus", estatus);
        values.put("sincronizado", 0);
        values.put("fecha_actualizacion", FechaUtils.ahoraIso());

        int filas = db.update(
                "animales",
                values,
                "id = ?",
                new String[]{idAnimal}
        );

        return filas > 0;
    }

    public boolean actualizarCrotalesAnimal(String idAnimal,
                                            boolean crotalIzquierdoPresente,
                                            boolean crotalDerechoPresente) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("crotal_izquierdo_presente", crotalIzquierdoPresente ? 1 : 0);
        values.put("crotal_derecho_presente", crotalDerechoPresente ? 1 : 0);
        values.put("sincronizado", 0);
        values.put("fecha_actualizacion", FechaUtils.ahoraIso());

        int filas = db.update(
                "animales",
                values,
                "id = ?",
                new String[]{idAnimal}
        );

        return filas > 0;
    }

    public boolean darDeBajaAnimal(String idAnimal, int estatusBaja, String fechaBaja) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("statecode", "0");
        values.put("estatus", estatusBaja);
        values.put("fecha_baja_explotacion", fechaBaja);
        values.put("sincronizado", 0);
        values.put("fecha_actualizacion", FechaUtils.ahoraIso());

        int filas = db.update(
                "animales",
                values,
                "id = ?",
                new String[]{idAnimal}
        );

        return filas > 0;
    }

    public boolean reactivarAnimal(String idAnimal, int estatus) {

        SQLiteDatabase db = this.getWritableDatabase();

        String sql =
                "UPDATE animales " +
                        "SET statecode = ?, " +
                        "estatus = ?, " +
                        "fecha_baja_explotacion = NULL, " +
                        "sincronizado = ?, " +
                        "fecha_actualizacion = ? " +
                        "WHERE id = ?";

        android.database.sqlite.SQLiteStatement stmt = db.compileStatement(sql);

        stmt.bindString(1, "1");
        stmt.bindLong(2, estatus);
        stmt.bindLong(3, 0);
        stmt.bindString(4, FechaUtils.ahoraIso());
        stmt.bindString(5, idAnimal);

        int filas = stmt.executeUpdateDelete();

        android.util.Log.d("DBHelper", "Reactivar animal filas actualizadas: " + filas);

        android.util.Log.d("DBHelper", "Reactivar idAnimal: " + idAnimal);
        android.util.Log.d("DBHelper", "Reactivar estatus: " + estatus);
        android.util.Log.d("DBHelper", "Reactivar filas actualizadas: " + filas);

        return filas > 0;
    }

    //******PESOS******************************************************************************
    //*****************************************************************************************

    public boolean insertarPesoAnimal(PesoAnimal peso) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", peso.getId());
        values.put("id_animal", peso.getIdAnimal());
        values.put("id_explotacion_uuid", peso.getIdExplotacionUuid());
        values.put("crotal", peso.getCrotal());
        values.put("sexo", peso.getSexo());
        values.put("fecha", peso.getFecha());
        values.put("peso", peso.getPeso());
        values.put("observaciones", peso.getObservaciones());
        values.put("sincronizado", 0);
        values.put("eliminado", 0);
        values.put("fecha_actualizacion", peso.getFechaActualizacion());
        values.putNull("fecha_eliminado");

        long result = db.insertWithOnConflict(
                "pesos_animales",
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );

        return result != -1;
    }
    public List<PesoAnimal> obtenerPesosPorAnimal(String idAnimal) {
        List<PesoAnimal> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM pesos_animales " +
                        "WHERE id_animal = ? AND eliminado = 0 " +
                        "ORDER BY fecha DESC",
                new String[]{idAnimal}
        );

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorAPesoAnimal(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }

    public List<PesoAnimal> obtenerPesosNoSincronizados() {
        List<PesoAnimal> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM pesos_animales WHERE sincronizado = 0",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorAPesoAnimal(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }

    public void marcarPesoComoSincronizado(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("sincronizado", 1);

        db.update(
                "pesos_animales",
                values,
                "id = ?",
                new String[]{id}
        );
    }

    public void insertarOActualizarPesoDesdeServidor(PesoAnimal peso) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", peso.getId());
        values.put("id_animal", peso.getIdAnimal());
        values.put("id_explotacion_uuid", peso.getIdExplotacionUuid());
        values.put("crotal", peso.getCrotal());
        values.put("sexo", peso.getSexo());
        values.put("fecha", peso.getFecha());
        values.put("peso", peso.getPeso());
        values.put("observaciones", peso.getObservaciones());
        values.put("sincronizado", 1);
        values.put("eliminado", peso.getEliminado() != null ? peso.getEliminado() : 0);
        values.put("fecha_actualizacion", peso.getFechaActualizacion());
        values.put("fecha_eliminado", peso.getFechaEliminado());

        db.insertWithOnConflict(
                "pesos_animales",
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    private PesoAnimal cursorAPesoAnimal(Cursor cursor) {
        PesoAnimal p = new PesoAnimal();

        p.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
        p.setIdAnimal(cursor.getString(cursor.getColumnIndexOrThrow("id_animal")));
        p.setIdExplotacionUuid(cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion_uuid")));
        p.setCrotal(cursor.getString(cursor.getColumnIndexOrThrow("crotal")));
        p.setSexo(cursor.getString(cursor.getColumnIndexOrThrow("sexo")));
        p.setFecha(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
        p.setPeso(cursor.getInt(cursor.getColumnIndexOrThrow("peso")));
        p.setObservaciones(cursor.getString(cursor.getColumnIndexOrThrow("observaciones")));
        p.setSincronizado(cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")));
        p.setEliminado(cursor.getInt(cursor.getColumnIndexOrThrow("eliminado")));
        p.setFechaActualizacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")));
        p.setFechaEliminado(cursor.getString(cursor.getColumnIndexOrThrow("fecha_eliminado")));

        return p;
    }

    public List<Animal> buscarAnimalesPorUltimos4Crotal(String ultimos4, String idExplotacion) {
        List<Animal> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM animales " +
                        "WHERE substr(crotal, -4) = ? " +
                        "AND id_explotacion_uuid = ? " +
                        "AND eliminado = 0 " +
                        "AND (statecode IS NULL OR statecode != '0') " +
                        "ORDER BY crotal ASC",
                new String[]{ultimos4, idExplotacion}
        );

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorToAnimal(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }

    public boolean actualizarPesoAnimal(String idAnimal, String fecha, int peso, String fechaActualizacion) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("peso", peso);
        values.put("fecha_actualizacion", fechaActualizacion);
        values.put("sincronizado", 0);

        int filas = db.update(
                "pesos_animales",
                values,
                "id_animal = ? AND fecha = ?",
                new String[]{idAnimal, fecha}
        );

        return filas > 0;
    }

    public boolean existePesoAnimalEnFecha(String idAnimal, String fecha) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM pesos_animales WHERE id_animal = ? AND fecha = ? AND eliminado = 0 LIMIT 1",
                new String[]{idAnimal, fecha}
        );

        boolean existe = cursor.moveToFirst();
        cursor.close();

        return existe;
    }

    public List<PesoAnimal> obtenerPesosPorExplotacionYFecha(String idExplotacion, String fecha) {
        List<PesoAnimal> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM pesos_animales " +
                        "WHERE id_explotacion_uuid = ? " +
                        "AND fecha = ? " +
                        "AND eliminado = 0 " +
                        "ORDER BY crotal ASC",
                new String[]{idExplotacion, fecha}
        );

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorAPesoAnimal(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }

    public boolean eliminarPesoAnimal(String idPeso) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("eliminado", 1);
        values.put("sincronizado", 0);
        values.put("fecha_eliminado", FechaUtils.ahoraIso());
        values.put("fecha_actualizacion", FechaUtils.ahoraIso());

        int filas = db.update(
                "pesos_animales",
                values,
                "id = ?",
                new String[]{idPeso}
        );

        return filas > 0;
    }

    public List<String> obtenerFechasPesajesPorExplotacion(String idExplotacion) {
        List<String> fechas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT fecha FROM pesos_animales " +
                        "WHERE id_explotacion_uuid = ? AND eliminado = 0 " +
                        "ORDER BY fecha DESC",
                new String[]{idExplotacion}
        );

        if (cursor.moveToFirst()) {
            do {
                fechas.add(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return fechas;
    }
    //************EVENTOS REPRODUCTIVOS***************
    //***********************************************

    public boolean insertarOActualizarEventoReproductivo(EventoReproductivo e) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean ok = false;

        try {
            ContentValues values = new ContentValues();

            values.put("id", e.getId());
            values.put("id_madre", e.getIdMadre());
            values.put("id_cria", e.getIdCria());
            values.put("id_explotacion_uuid", e.getIdExplotacionUuid());
            values.put("tipo_evento", e.getTipoEvento());
            values.put("fecha_evento", e.getFechaEvento());
            values.put("resultado_cria", e.getResultadoCria());
            values.put("cria_identificada", e.getCriaIdentificada() != null ? e.getCriaIdentificada() : 0);
            values.put("sexo_estimado", e.getSexoEstimado());
            values.put("raza_estimada", e.getRazaEstimada());
            values.put("capa_estimada", e.getCapaEstimada());
            values.put("cercado", e.getCercado());
            values.put("observaciones", e.getObservaciones());
            values.put("sincronizado", e.getSincronizado() != null ? e.getSincronizado() : 0);
            values.put("eliminado", e.getEliminado() != null ? e.getEliminado() : 0);
            values.put("fecha_actualizacion", e.getFechaActualizacion());
            values.put("fecha_eliminado", e.getFechaEliminado());

            int filas = db.update(
                    "eventos_reproductivos",
                    values,
                    "id = ?",
                    new String[]{e.getId()}
            );

            if (filas == 0) {
                long insert = db.insert("eventos_reproductivos", null, values);
                ok = insert != -1;
            } else {
                ok = true;
            }

        } catch (Exception ex) {
            android.util.Log.e("DBHelper", "Error insertando/actualizando evento reproductivo", ex);
        } finally {
            db.close();
        }

        return ok;
    }
    public boolean registrarPartoPendienteCrotal(EventoReproductivo evento) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean ok = false;

        db.beginTransaction();

        try {
            ContentValues valuesEvento = new ContentValues();

            valuesEvento.put("id", evento.getId());
            valuesEvento.put("id_madre", evento.getIdMadre());
            valuesEvento.put("id_cria", (String) null);
            valuesEvento.put("id_explotacion_uuid", evento.getIdExplotacionUuid());
            valuesEvento.put("tipo_evento", EventoReproductivo.TIPO_PARTO);
            valuesEvento.put("fecha_evento", evento.getFechaEvento());
            valuesEvento.put("resultado_cria", EventoReproductivo.VIVA_PENDIENTE_IDENTIFICAR);
            valuesEvento.put("cria_identificada", 0);
            valuesEvento.put("sexo_estimado", evento.getSexoEstimado());
            valuesEvento.put("raza_estimada", evento.getRazaEstimada());
            valuesEvento.put("capa_estimada", evento.getCapaEstimada());
            valuesEvento.put("cercado", evento.getCercado());
            valuesEvento.put("observaciones", evento.getObservaciones());
            valuesEvento.put("sincronizado", 0);
            valuesEvento.put("eliminado", 0);
            valuesEvento.put("fecha_actualizacion", evento.getFechaActualizacion());
            valuesEvento.put("fecha_eliminado", (String) null);

            long insertEvento = db.insert("eventos_reproductivos", null, valuesEvento);

            ContentValues valuesMadre = new ContentValues();
            valuesMadre.put("estado_reproductivo", "vacia");
            valuesMadre.put("sincronizado", 0);
            valuesMadre.put("fecha_actualizacion", evento.getFechaActualizacion());

            int filasMadre = db.update(
                    "animales",
                    valuesMadre,
                    "id = ?",
                    new String[]{evento.getIdMadre()}
            );

            if (insertEvento != -1 && filasMadre > 0) {
                db.setTransactionSuccessful();
                ok = true;
            }

        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error registrando parto pendiente de crotal", e);
        } finally {
            db.endTransaction();
            db.close();
        }

        return ok;
    }

    public List<EventoReproductivo> obtenerCriasPendientesIdentificar(String idExplotacionUuid) {
        List<EventoReproductivo> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "SELECT er.*, a.crotal AS crotal_madre_mostrar " +
                    "FROM eventos_reproductivos er " +
                    "LEFT JOIN animales a ON er.id_madre = a.id " +
                    "WHERE er.id_explotacion_uuid = ? " +
                    "AND er.eliminado = 0 " +
                    "AND er.tipo_evento = ? " +
                    "AND er.resultado_cria = ? " +
                    "AND er.cria_identificada = 0 " +
                    "ORDER BY er.fecha_evento ASC";

            cursor = db.rawQuery(sql, new String[]{
                    idExplotacionUuid,
                    EventoReproductivo.TIPO_PARTO,
                    EventoReproductivo.VIVA_PENDIENTE_IDENTIFICAR
            });

            while (cursor.moveToNext()) {
                EventoReproductivo e = cursorToEventoReproductivo(cursor);

                int idxCrotalMadre = cursor.getColumnIndex("crotal_madre_mostrar");
                if (idxCrotalMadre != -1) {
                    e.setCrotalMadre(cursor.getString(idxCrotalMadre));
                }

                lista.add(e);
            }

        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error obteniendo crías pendientes", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return lista;
    }

    private EventoReproductivo cursorToEventoReproductivo(Cursor cursor) {
        EventoReproductivo e = new EventoReproductivo();

        e.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
        e.setIdMadre(cursor.getString(cursor.getColumnIndexOrThrow("id_madre")));
        e.setIdCria(cursor.getString(cursor.getColumnIndexOrThrow("id_cria")));
        e.setIdExplotacionUuid(cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion_uuid")));
        e.setTipoEvento(cursor.getString(cursor.getColumnIndexOrThrow("tipo_evento")));
        e.setFechaEvento(cursor.getString(cursor.getColumnIndexOrThrow("fecha_evento")));
        e.setResultadoCria(cursor.getString(cursor.getColumnIndexOrThrow("resultado_cria")));
        e.setCriaIdentificada(cursor.getInt(cursor.getColumnIndexOrThrow("cria_identificada")));
        e.setSexoEstimado(cursor.getString(cursor.getColumnIndexOrThrow("sexo_estimado")));
        e.setRazaEstimada(cursor.getString(cursor.getColumnIndexOrThrow("raza_estimada")));
        e.setCapaEstimada(cursor.getString(cursor.getColumnIndexOrThrow("capa_estimada")));
        e.setCercado(cursor.getString(cursor.getColumnIndexOrThrow("cercado")));
        e.setObservaciones(cursor.getString(cursor.getColumnIndexOrThrow("observaciones")));
        e.setSincronizado(cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")));
        e.setEliminado(cursor.getInt(cursor.getColumnIndexOrThrow("eliminado")));
        e.setFechaActualizacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")));
        e.setFechaEliminado(cursor.getString(cursor.getColumnIndexOrThrow("fecha_eliminado")));

        return e;
    }

    public List<EventoReproductivo> obtenerEventosReproductivosNoSincronizados() {
        List<EventoReproductivo> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {

            String sql =
                    "SELECT er.* " +
                            "FROM eventos_reproductivos er " +
                            "LEFT JOIN animales a ON er.id_cria = a.id " +
                            "WHERE er.sincronizado = 0 " +
                            "AND (er.id_cria IS NULL OR a.id IS NOT NULL) " +
                            "ORDER BY er.fecha_actualizacion ASC";

            cursor = db.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                lista.add(cursorToEventoReproductivo(cursor));
            }

        } catch (Exception e) {
            android.util.Log.e("DBHelper",
                    "Error obteniendo eventos reproductivos no sincronizados", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return lista;
    }

    public boolean marcarEventoReproductivoComoSincronizado(String idEvento) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean ok = false;

        try {
            ContentValues values = new ContentValues();
            values.put("sincronizado", 1);

            int filas = db.update(
                    "eventos_reproductivos",
                    values,
                    "id = ?",
                    new String[]{idEvento}
            );

            ok = filas > 0;

        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error marcando evento reproductivo sincronizado", e);
        } finally {
            db.close();
        }

        return ok;
    }


    public boolean insertarOActualizarEventoReproductivoDesdeServidor(EventoReproductivo e) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean ok = false;

        try {
            ContentValues values = new ContentValues();

            values.put("id", e.getId());
            values.put("id_madre", e.getIdMadre());

            putStringOrNull(values, "id_cria", e.getIdCria());

            values.put("id_explotacion_uuid", e.getIdExplotacionUuid());
            values.put("tipo_evento", e.getTipoEvento());
            values.put("fecha_evento", e.getFechaEvento());

            putStringOrNull(values, "resultado_cria", e.getResultadoCria());

            values.put("cria_identificada", e.getCriaIdentificada() != null ? e.getCriaIdentificada() : 0);

            putStringOrNull(values, "sexo_estimado", e.getSexoEstimado());
            putStringOrNull(values, "raza_estimada", e.getRazaEstimada());
            putStringOrNull(values, "capa_estimada", e.getCapaEstimada());
            putStringOrNull(values, "cercado", e.getCercado());
            putStringOrNull(values, "observaciones", e.getObservaciones());

            values.put("sincronizado", 1);
            values.put("eliminado", e.getEliminado() != null ? e.getEliminado() : 0);

            putStringOrNull(values, "fecha_actualizacion", e.getFechaActualizacion());
            putStringOrNull(values, "fecha_eliminado", e.getFechaEliminado());

            int filas = db.update(
                    "eventos_reproductivos",
                    values,
                    "id = ?",
                    new String[]{e.getId()}
            );

            if (filas == 0) {
                long insert = db.insert("eventos_reproductivos", null, values);
                ok = insert != -1;
            } else {
                ok = true;
            }

        } catch (Exception ex) {
            android.util.Log.e("DBHelper", "Error guardando evento reproductivo desde servidor. " +
                    "id=" + e.getId() +
                    " idMadre=" + e.getIdMadre() +
                    " idCria=" + e.getIdCria() +
                    " idExplotacion=" + e.getIdExplotacionUuid(), ex);
        } finally {
            db.close();
        }

        return ok;
    }

    public boolean identificarCria(String idEvento, Animal cria, String fechaActualizacion) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean ok = false;

        db.beginTransaction();

        try {
            ContentValues valuesCria = new ContentValues();

            valuesCria.put("id", cria.getId());
            valuesCria.put("id_sharepoint", cria.getIdSharepoint());
            valuesCria.put("crotal", cria.getCrotal());
            valuesCria.put("id_explotacion_uuid", cria.getIdExplotacionUuid());
            valuesCria.put("estatus", cria.getEstatus());
            valuesCria.put("fecha_nacimiento", cria.getFechaNacimiento());
            valuesCria.put("raza", cria.getRaza());
            valuesCria.put("sexo", cria.getSexo());
            valuesCria.put("crotal_madre", cria.getCrotalMadre());
            valuesCria.put("capa", cria.getCapa());
            valuesCria.put("cercado", cria.getCercado());
            valuesCria.put("id_cercado_historico", cria.getIdCercadoHistorico());
            valuesCria.put("paridera", cria.getParidera());
            valuesCria.put("alta_gestionada", cria.getAltaGestionada());
            valuesCria.put("statecode", cria.getStatecode());
            valuesCria.put("factor", cria.getFactor());
            valuesCria.put("fecha_baja_explotacion", cria.getFechaBajaExplotacion());
            valuesCria.put("causa_alta", cria.getCausaAlta());
            valuesCria.put("explotacion_nacimiento", cria.getExplotacionNacimiento());
            valuesCria.put("check_paridera", cria.getCheckParidera());
            valuesCria.put("ischosen", cria.getIschosen());
            valuesCria.put("estado_reproductivo", cria.getEstadoReproductivo() != null ? cria.getEstadoReproductivo() : "nada");
            valuesCria.put("crotal_izquierdo_presente", cria.getCrotalIzquierdoPresente() != null && cria.getCrotalIzquierdoPresente() ? 1 : 0);
            valuesCria.put("crotal_derecho_presente", cria.getCrotalDerechoPresente() != null && cria.getCrotalDerechoPresente() ? 1 : 0);
            valuesCria.put("fecha_actualizacion", fechaActualizacion);
            valuesCria.put("sincronizado", 0);
            valuesCria.put("eliminado", 0);
            valuesCria.put("fecha_eliminado", (String) null);

            long insertCria = db.insert("animales", null, valuesCria);
            int filasCrotal = 0;

            if (insertCria != -1) {

                ContentValues valuesCrotal = new ContentValues();
                valuesCrotal.put("estado", "USADO");
                valuesCrotal.put("id_animal_usado", cria.getId());
                valuesCrotal.put("fecha_uso", cria.getFechaNacimiento());
                valuesCrotal.put("fecha_actualizacion", fechaActualizacion);
                valuesCrotal.put("sincronizado", 0);

                filasCrotal = db.update(
                        "crotales_disponibles",
                        valuesCrotal,
                        "crotal = ?",
                        new String[]{cria.getCrotal()}
                );
            }

            ContentValues valuesEvento = new ContentValues();
            valuesEvento.put("id_cria", cria.getId());
            valuesEvento.put("cria_identificada", 1);
            valuesEvento.put("resultado_cria", EventoReproductivo.VIVA_IDENTIFICADA);
            valuesEvento.put("sincronizado", 0);
            valuesEvento.put("fecha_actualizacion", fechaActualizacion);

            int filasEvento = db.update(
                    "eventos_reproductivos",
                    valuesEvento,
                    "id = ?",
                    new String[]{idEvento}
            );

            if (insertCria != -1 && filasEvento > 0 && filasCrotal > 0) {
                db.setTransactionSuccessful();
                ok = true;
            }

        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error identificando cría", e);
        } finally {
            db.endTransaction();
            db.close();
        }

        return ok;
    }


    //*******************EVENTOS REPRODUCTIVOS******************

    public boolean registrarEventoReproductivoSinCria(EventoReproductivo evento) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean ok = false;

        db.beginTransaction();

        try {
            ContentValues valuesEvento = new ContentValues();

            valuesEvento.put("id", evento.getId());
            valuesEvento.put("id_madre", evento.getIdMadre());
            valuesEvento.putNull("id_cria");
            valuesEvento.put("id_explotacion_uuid", evento.getIdExplotacionUuid());
            valuesEvento.put("tipo_evento", evento.getTipoEvento());
            valuesEvento.put("fecha_evento", evento.getFechaEvento());
            valuesEvento.put("resultado_cria", evento.getResultadoCria());
            valuesEvento.put("cria_identificada", 0);
            valuesEvento.put("sexo_estimado", evento.getSexoEstimado());
            valuesEvento.put("raza_estimada", evento.getRazaEstimada());
            valuesEvento.put("capa_estimada", evento.getCapaEstimada());
            valuesEvento.put("cercado", evento.getCercado());
            valuesEvento.put("observaciones", evento.getObservaciones());
            valuesEvento.put("sincronizado", 0);
            valuesEvento.put("eliminado", 0);
            valuesEvento.put("fecha_actualizacion", evento.getFechaActualizacion());
            valuesEvento.putNull("fecha_eliminado");

            long insertEvento = db.insert("eventos_reproductivos", null, valuesEvento);

            ContentValues valuesMadre = new ContentValues();
            valuesMadre.put("estado_reproductivo", "vacia");
            valuesMadre.put("sincronizado", 0);
            valuesMadre.put("fecha_actualizacion", evento.getFechaActualizacion());

            int filasMadre = db.update(
                    "animales",
                    valuesMadre,
                    "id = ?",
                    new String[]{evento.getIdMadre()}
            );

            if (insertEvento != -1 && filasMadre > 0) {
                db.setTransactionSuccessful();
                ok = true;
            }

        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error registrando evento reproductivo sin cría", e);
        } finally {
            db.endTransaction();
            db.close();
        }

        return ok;
    }

    public int contarCriasPendientesIdentificar(String idExplotacionUuid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int total = 0;

        try {
            String sql = "SELECT COUNT(*) FROM eventos_reproductivos " +
                    "WHERE id_explotacion_uuid = ? " +
                    "AND eliminado = 0 " +
                    "AND tipo_evento = ? " +
                    "AND resultado_cria = ? " +
                    "AND cria_identificada = 0";

            cursor = db.rawQuery(sql, new String[]{
                    idExplotacionUuid,
                    EventoReproductivo.TIPO_PARTO,
                    EventoReproductivo.VIVA_PENDIENTE_IDENTIFICAR
            });

            if (cursor.moveToFirst()) {
                total = cursor.getInt(0);
            }

        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error contando crías pendientes", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return total;
    }

    public boolean marcarCriaMuertaAntesIdentificar(String idEvento, String observaciones, String fechaActualizacion) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean ok = false;

        try {
            ContentValues values = new ContentValues();
            values.put("resultado_cria", EventoReproductivo.MUERE_ANTES_IDENTIFICAR);
            values.put("cria_identificada", 0);
            values.putNull("id_cria");
            values.put("observaciones", observaciones);
            values.put("sincronizado", 0);
            values.put("fecha_actualizacion", fechaActualizacion);

            int filas = db.update(
                    "eventos_reproductivos",
                    values,
                    "id = ?",
                    new String[]{idEvento}
            );

            ok = filas > 0;

        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error marcando cría muerta antes de identificar", e);
        } finally {
            db.close();
        }

        return ok;
    }

    //***************CROTALES NUEVOS*************************************


    public boolean insertarRangoCrotales(String primerCrotal,
                                         String ultimoCrotal,
                                         String idExplotacionUuid,
                                         String fechaAsignacion,
                                         String observaciones,
                                         String fechaActualizacion) {

        SQLiteDatabase db = this.getWritableDatabase();
        boolean ok = false;

        db.beginTransaction();

        try {
            String prefijoInicio = primerCrotal.replaceAll("\\d+$", "");
            String numeroInicioStr = primerCrotal.substring(prefijoInicio.length());

            String prefijoFin = ultimoCrotal.replaceAll("\\d+$", "");
            String numeroFinStr = ultimoCrotal.substring(prefijoFin.length());

            if (!prefijoInicio.equals(prefijoFin)) {
                throw new Exception("Los crotales no tienen el mismo prefijo");
            }

            long numeroInicio = Long.parseLong(numeroInicioStr);
            long numeroFin = Long.parseLong(numeroFinStr);

            if (numeroFin < numeroInicio) {
                throw new Exception("El último crotal es menor que el primero");
            }

            int longitudNumero = numeroInicioStr.length();

            for (long i = numeroInicio; i <= numeroFin; i++) {
                String numeroFormateado = String.format("%0" + longitudNumero + "d", i);
                String crotal = prefijoInicio + numeroFormateado;

                ContentValues values = new ContentValues();
                values.put("id", java.util.UUID.randomUUID().toString());
                values.put("crotal", crotal);
                values.put("id_explotacion_uuid", idExplotacionUuid);
                values.put("estado", "DISPONIBLE");
                values.put("fecha_asignacion", fechaAsignacion);
                values.putNull("fecha_uso");
                values.putNull("id_animal_usado");
                values.put("observaciones", observaciones);
                values.put("fecha_actualizacion", fechaActualizacion);
                values.put("sincronizado", 0);
                values.put("eliminado", 0);
                values.putNull("fecha_eliminado");

                db.insertWithOnConflict(
                        "crotales_disponibles",
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_IGNORE
                );
            }

            db.setTransactionSuccessful();
            ok = true;

        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error insertando rango de crotales", e);
        } finally {
            db.endTransaction();
            db.close();
        }

        return ok;
    }

    public List<CrotalDisponible> obtenerCrotalesDisponibles(String idExplotacionUuid) {
        List<CrotalDisponible> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql =
                    "SELECT * FROM crotales_disponibles " +
                            "WHERE id_explotacion_uuid = ? " +
                            "AND estado = 'DISPONIBLE' " +
                            "AND eliminado = 0 " +
                            "ORDER BY crotal ASC";

            cursor = db.rawQuery(sql, new String[]{idExplotacionUuid});

            while (cursor.moveToNext()) {
                lista.add(cursorToCrotalDisponible(cursor));
            }

        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error obteniendo crotales disponibles", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return lista;
    }

    public boolean marcarCrotalComoUsado(String crotal,
                                         String idAnimalUsado,
                                         String fechaUso,
                                         String fechaActualizacion) {

        SQLiteDatabase db = this.getWritableDatabase();
        boolean ok = false;

        try {
            ContentValues values = new ContentValues();
            values.put("estado", "USADO");
            values.put("id_animal_usado", idAnimalUsado);
            values.put("fecha_uso", fechaUso);
            values.put("fecha_actualizacion", fechaActualizacion);
            values.put("sincronizado", 0);

            int filas = db.update(
                    "crotales_disponibles",
                    values,
                    "crotal = ? AND estado = 'DISPONIBLE'",
                    new String[]{crotal}
            );

            ok = filas > 0;

        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error marcando crotal como usado", e);
        } finally {
            db.close();
        }

        return ok;
    }

    private CrotalDisponible cursorToCrotalDisponible(Cursor c) {
        CrotalDisponible crotal = new CrotalDisponible();

        crotal.setId(c.getString(c.getColumnIndexOrThrow("id")));
        crotal.setCrotal(c.getString(c.getColumnIndexOrThrow("crotal")));
        crotal.setIdExplotacionUuid(c.getString(c.getColumnIndexOrThrow("id_explotacion_uuid")));
        crotal.setEstado(c.getString(c.getColumnIndexOrThrow("estado")));
        crotal.setFechaAsignacion(c.getString(c.getColumnIndexOrThrow("fecha_asignacion")));
        crotal.setFechaUso(c.getString(c.getColumnIndexOrThrow("fecha_uso")));
        crotal.setIdAnimalUsado(c.getString(c.getColumnIndexOrThrow("id_animal_usado")));
        crotal.setObservaciones(c.getString(c.getColumnIndexOrThrow("observaciones")));
        crotal.setFechaActualizacion(c.getString(c.getColumnIndexOrThrow("fecha_actualizacion")));
        crotal.setSincronizado(c.getInt(c.getColumnIndexOrThrow("sincronizado")));
        crotal.setEliminado(c.getInt(c.getColumnIndexOrThrow("eliminado")));
        crotal.setFechaEliminado(c.getString(c.getColumnIndexOrThrow("fecha_eliminado")));

        return crotal;
    }

    public List<CrotalDisponible> obtenerCrotalesNoSincronizados() {
        List<CrotalDisponible> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql =
                    "SELECT * FROM crotales_disponibles " +
                            "WHERE sincronizado = 0 " +
                            "ORDER BY fecha_actualizacion ASC";

            cursor = db.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                lista.add(cursorToCrotalDisponible(cursor));
            }

        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error obteniendo crotales no sincronizados", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return lista;
    }

    public boolean marcarCrotalSincronizado(String idCrotal) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean ok = false;

        try {
            ContentValues values = new ContentValues();
            values.put("sincronizado", 1);

            int filas = db.update(
                    "crotales_disponibles",
                    values,
                    "id = ?",
                    new String[]{idCrotal}
            );

            ok = filas > 0;

        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error marcando crotal sincronizado", e);
        } finally {
            db.close();
        }

        return ok;
    }

    public int contarCrotalesPorEstado(String idExplotacionUuid, String estado) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int total = 0;

        try {
            String sql =
                    "SELECT COUNT(*) FROM crotales_disponibles " +
                            "WHERE id_explotacion_uuid = ? " +
                            "AND estado = ? " +
                            "AND eliminado = 0";

            cursor = db.rawQuery(sql, new String[]{idExplotacionUuid, estado});

            if (cursor.moveToFirst()) {
                total = cursor.getInt(0);
            }

        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error contando crotales por estado", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return total;
    }

    public String obtenerSiguienteCrotalDisponible(String idExplotacionUuid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String crotal = null;

        try {
            String sql =
                    "SELECT crotal FROM crotales_disponibles " +
                            "WHERE id_explotacion_uuid = ? " +
                            "AND estado = 'DISPONIBLE' " +
                            "AND eliminado = 0 " +
                            "ORDER BY crotal ASC " +
                            "LIMIT 1";

            cursor = db.rawQuery(sql, new String[]{idExplotacionUuid});

            if (cursor.moveToFirst()) {
                crotal = cursor.getString(0);
            }

        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error obteniendo siguiente crotal", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return crotal;
    }

    public List<CrotalDisponible> obtenerCrotalesPorEstado(String idExplotacionUuid, String estado) {
        List<CrotalDisponible> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql =
                    "SELECT * FROM crotales_disponibles " +
                            "WHERE id_explotacion_uuid = ? " +
                            "AND estado = ? " +
                            "AND eliminado = 0 " +
                            "ORDER BY crotal ASC";

            cursor = db.rawQuery(sql, new String[]{idExplotacionUuid, estado});

            while (cursor.moveToNext()) {
                lista.add(cursorToCrotalDisponible(cursor));
            }

        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error obteniendo crotales por estado", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return lista;
    }

    public boolean anularCrotal(String crotal,
                                String observaciones,
                                String fechaActualizacion) {

        SQLiteDatabase db = this.getWritableDatabase();
        boolean ok = false;

        try {
            ContentValues values = new ContentValues();
            values.put("estado", "ANULADO");
            values.put("observaciones", observaciones);
            values.put("fecha_actualizacion", fechaActualizacion);
            values.put("sincronizado", 0);

            int filas = db.update(
                    "crotales_disponibles",
                    values,
                    "crotal = ? AND estado = 'DISPONIBLE'",
                    new String[]{crotal}
            );

            ok = filas > 0;

        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error anulando crotal", e);
        } finally {
            db.close();
        }

        return ok;
    }

    public boolean restaurarCrotalDisponible(String crotal,
                                             String fechaActualizacion) {

        SQLiteDatabase db = this.getWritableDatabase();
        boolean ok = false;

        try {
            ContentValues values = new ContentValues();
            values.put("estado", "DISPONIBLE");
            values.putNull("fecha_uso");
            values.putNull("id_animal_usado");
            values.put("fecha_actualizacion", fechaActualizacion);
            values.put("sincronizado", 0);

            int filas = db.update(
                    "crotales_disponibles",
                    values,
                    "crotal = ? AND estado = 'ANULADO'",
                    new String[]{crotal}
            );

            ok = filas > 0;

        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error restaurando crotal", e);
        } finally {
            db.close();
        }

        return ok;
    }

    public String obtenerUltimaFechaActualizacion(String tabla) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String fecha = null;

        try {
            cursor = db.rawQuery(
                    "SELECT MAX(fecha_actualizacion) FROM " + tabla,
                    null
            );

            if (cursor.moveToFirst()) {
                fecha = cursor.getString(0);
            }

        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error obteniendo última fecha de " + tabla, e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return fecha;
    }

    public boolean insertarOActualizarCrotalDesdeSupabase(CrotalDisponible c) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean ok = false;

        try {
            ContentValues values = new ContentValues();

            values.put("id", c.getId());
            values.put("crotal", c.getCrotal());
            values.put("id_explotacion_uuid", c.getIdExplotacionUuid());
            values.put("estado", c.getEstado());
            values.put("fecha_asignacion", c.getFechaAsignacion());
            values.put("fecha_uso", c.getFechaUso());
            values.put("id_animal_usado", c.getIdAnimalUsado());
            values.put("observaciones", c.getObservaciones());
            values.put("fecha_actualizacion", c.getFechaActualizacion());
            values.put("sincronizado", 1);
            values.put("eliminado", c.getEliminado() != null ? c.getEliminado() : 0);
            values.put("fecha_eliminado", c.getFechaEliminado());

            long resultado = db.insertWithOnConflict(
                    "crotales_disponibles",
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
            );

            ok = resultado != -1;

        } catch (Exception e) {
            android.util.Log.e("DBHelper", "Error insertando/actualizando crotal desde Supabase", e);
        } finally {
            db.close();
        }

        return ok;
    }

    private void putStringOrNull(ContentValues values, String campo, String valor) {
        if (valor != null && !valor.trim().isEmpty()) {
            values.put(campo, valor);
        } else {
            values.putNull(campo);
        }
    }

    //******************LISTAS*************************************

    public boolean guardarListaAnimal(ListaAnimal lista) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", lista.getId());
        values.put("id_explotacion_uuid", lista.getIdExplotacionUuid());
        values.put("nombre", lista.getNombre());
        values.put("tipo", lista.getTipo());
        values.put("observaciones", lista.getObservaciones());
        values.put("fecha_creacion", lista.getFechaCreacion());
        values.put("sincronizado", 0);
        values.put("eliminado", 0);
        values.put("fecha_actualizacion", lista.getFechaActualizacion());
        values.put("fecha_eliminado", lista.getFechaEliminado());

        long res = db.insertWithOnConflict(
                "listas_animales",
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );

        return res != -1;
    }

    public List<ListaAnimal> obtenerListasAnimales(String idExplotacionUuid) {
        List<ListaAnimal> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM listas_animales " +
                        "WHERE id_explotacion_uuid = ? AND eliminado = 0 " +
                        "ORDER BY fecha_creacion DESC",
                new String[]{idExplotacionUuid}
        );

        if (cursor.moveToFirst()) {
            do {
                ListaAnimal item = new ListaAnimal();
                item.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                item.setIdExplotacionUuid(cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion_uuid")));
                item.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                item.setTipo(cursor.getString(cursor.getColumnIndexOrThrow("tipo")));
                item.setObservaciones(cursor.getString(cursor.getColumnIndexOrThrow("observaciones")));
                item.setFechaCreacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_creacion")));
                item.setSincronizado(cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")));
                item.setEliminado(cursor.getInt(cursor.getColumnIndexOrThrow("eliminado")));
                item.setFechaActualizacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")));
                item.setFechaEliminado(cursor.getString(cursor.getColumnIndexOrThrow("fecha_eliminado")));

                lista.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }

    public boolean añadirAnimalALista(String idLista, Animal animal, String fechaActualizacion) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", UUID.randomUUID().toString());
        values.put("id_lista", idLista);
        values.put("id_animal", animal.getId());
        values.put("crotal", animal.getCrotal());
        values.put("sexo", animal.getSexo());
        values.put("marcado", 0);
        values.put("fecha_alta", fechaActualizacion);
        values.put("observaciones", "");
        values.put("sincronizado", 0);
        values.put("eliminado", 0);
        values.put("fecha_actualizacion", fechaActualizacion);
        values.put("fecha_eliminado", (String) null);


        long res = db.insertWithOnConflict(
                "lista_animales_detalle",
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        );

        return res != -1;
    }

    public List<AnimalEnLista> obtenerAnimalesDeLista(String idLista) {
        List<AnimalEnLista> animales = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM lista_animales_detalle " +
                        "WHERE id_lista = ? AND eliminado = 0 " +
                        "ORDER BY fecha_alta DESC",
                new String[]{idLista}
        );

        if (cursor.moveToFirst()) {
            do {
                AnimalEnLista item = new AnimalEnLista();
                item.setIdDetalle(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                item.setIdLista(cursor.getString(cursor.getColumnIndexOrThrow("id_lista")));
                item.setIdAnimal(cursor.getString(cursor.getColumnIndexOrThrow("id_animal")));
                item.setCrotal(cursor.getString(cursor.getColumnIndexOrThrow("crotal")));
                item.setSexo(cursor.getString(cursor.getColumnIndexOrThrow("sexo")));
                item.setMarcado(cursor.getInt(cursor.getColumnIndexOrThrow("marcado")));
                item.setFechaAlta(cursor.getString(cursor.getColumnIndexOrThrow("fecha_alta")));
                item.setObservaciones(cursor.getString(cursor.getColumnIndexOrThrow("observaciones")));

                animales.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return animales;
    }


    public boolean eliminarAnimalDeLista(String idDetalle, String fechaActualizacion) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("eliminado", 1);
        values.put("sincronizado", 0);
        values.put("fecha_eliminado", fechaActualizacion);
        values.put("fecha_actualizacion", fechaActualizacion);

        int filas = db.update(
                "lista_animales_detalle",
                values,
                "id = ?",
                new String[]{idDetalle}
        );

        return filas > 0;
    }

    public Animal buscarAnimalPorCrotalParcial(String texto, String idExplotacionUuid) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM animales " +
                        "WHERE id_explotacion_uuid = ? " +
                        "AND eliminado = 0 " +
                        "AND statecode = '1' " +
                        "AND crotal LIKE ? " +
                        "LIMIT 1",
                new String[]{idExplotacionUuid, "%" + texto}
        );

        Animal animal = null;

        if (cursor.moveToFirst()) {
            animal = cursorToAnimal(cursor);
        }

        cursor.close();
        return animal;
    }

    public List<AnimalEnLista> obtenerAnimalesDeListaPorSexo(String idLista,
                                                             String sexo) {

        List<AnimalEnLista> lista = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM lista_animales_detalle " +
                        "WHERE id_lista = ? " +
                        "AND sexo = ? " +
                        "AND eliminado = 0 " +
                        "ORDER BY fecha_alta DESC",
                new String[]{idLista, sexo}
        );

        while (cursor.moveToNext()) {

            AnimalEnLista item = new AnimalEnLista();

            item.setIdDetalle(
                    cursor.getString(cursor.getColumnIndexOrThrow("id"))
            );

            item.setCrotal(
                    cursor.getString(cursor.getColumnIndexOrThrow("crotal"))
            );

            item.setSexo(
                    cursor.getString(cursor.getColumnIndexOrThrow("sexo"))
            );
            item.setMarcado(cursor.getInt(cursor.getColumnIndexOrThrow("marcado")));

            lista.add(item);
        }

        cursor.close();
        db.close();

        return lista;
    }

    public boolean actualizarMarcadoAnimalLista(String idDetalle, int marcado, String fechaActualizacion) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("marcado", marcado);
        values.put("sincronizado", 0);
        values.put("fecha_actualizacion", fechaActualizacion);

        int filas = db.update(
                "lista_animales_detalle",
                values,
                "id = ?",
                new String[]{idDetalle}
        );

        return filas > 0;
    }

    public boolean actualizarNombreListaAnimal(String idLista, String nuevoNombre, String fechaActualizacion) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", nuevoNombre);
        values.put("sincronizado", 0);
        values.put("fecha_actualizacion", fechaActualizacion);

        int filas = db.update(
                "listas_animales",
                values,
                "id = ?",
                new String[]{idLista}
        );

        return filas > 0;
    }

    public boolean eliminarListaAnimal(String idLista, String fechaActualizacion) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        try {
            ContentValues valuesLista = new ContentValues();
            valuesLista.put("eliminado", 1);
            valuesLista.put("sincronizado", 0);
            valuesLista.put("fecha_eliminado", fechaActualizacion);
            valuesLista.put("fecha_actualizacion", fechaActualizacion);

            db.update(
                    "listas_animales",
                    valuesLista,
                    "id = ?",
                    new String[]{idLista}
            );

            ContentValues valuesDetalle = new ContentValues();
            valuesDetalle.put("eliminado", 1);
            valuesDetalle.put("sincronizado", 0);
            valuesDetalle.put("fecha_eliminado", fechaActualizacion);
            valuesDetalle.put("fecha_actualizacion", fechaActualizacion);

            db.update(
                    "lista_animales_detalle",
                    valuesDetalle,
                    "id_lista = ?",
                    new String[]{idLista}
            );

            db.setTransactionSuccessful();
            return true;

        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public List<ListaAnimal> obtenerListasAnimalesNoSincronizadas() {

        List<ListaAnimal> lista = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM listas_animales " +
                        "WHERE sincronizado = 0",
                null
        );

        while (cursor.moveToNext()) {

            ListaAnimal item = new ListaAnimal();

            item.setId(
                    cursor.getString(cursor.getColumnIndexOrThrow("id"))
            );

            item.setIdExplotacionUuid(
                    cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion_uuid"))
            );

            item.setNombre(
                    cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            );

            item.setTipo(
                    cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
            );

            item.setObservaciones(
                    cursor.getString(cursor.getColumnIndexOrThrow("observaciones"))
            );

            item.setFechaCreacion(
                    cursor.getString(cursor.getColumnIndexOrThrow("fecha_creacion"))
            );

            item.setSincronizado(
                    cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado"))
            );

            item.setEliminado(
                    cursor.getInt(cursor.getColumnIndexOrThrow("eliminado"))
            );

            item.setFechaActualizacion(
                    cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion"))
            );

            item.setFechaEliminado(
                    cursor.getString(cursor.getColumnIndexOrThrow("fecha_eliminado"))
            );

            lista.add(item);
        }

        cursor.close();
        db.close();

        return lista;
    }

    public boolean marcarListaAnimalComoSincronizada(String idLista) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("sincronizado", 1);

        int filas = db.update(
                "listas_animales",
                values,
                "id = ?",
                new String[]{idLista}
        );

        return filas > 0;
    }

    public boolean insertarOActualizarListaAnimalDesdeServidor(ListaAnimal lista) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {

            ContentValues values = new ContentValues();

            values.put("id", lista.getId());
            values.put("id_explotacion_uuid", lista.getIdExplotacionUuid());
            values.put("nombre", lista.getNombre());
            values.put("tipo", lista.getTipo());
            values.put("observaciones", lista.getObservaciones());
            values.put("fecha_creacion", lista.getFechaCreacion());
            values.put("sincronizado", 1);
            values.put("eliminado", lista.getEliminado());
            values.put("fecha_actualizacion", lista.getFechaActualizacion());
            values.put("fecha_eliminado", lista.getFechaEliminado());

            long resultado = db.insertWithOnConflict(
                    "listas_animales",
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
            );

            return resultado != -1;

        } catch (Exception e) {

            Log.e("DBHelper",
                    "Error insertando/actualizando lista desde servidor",
                    e);

            return false;

        } finally {

            db.close();
        }
    }

    public boolean marcarDetalleListaComoSincronizado(String idDetalle) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("sincronizado", 1);

        int filas = db.update(
                "lista_animales_detalle",
                values,
                "id = ?",
                new String[]{idDetalle}
        );

        db.close();

        return filas > 0;
    }

    public boolean insertarOActualizarDetalleListaDesdeServidor(AnimalEnLista item) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();

            values.put("id", item.getIdDetalle());
            values.put("id_lista", item.getIdLista());
            values.put("id_animal", item.getIdAnimal());
            values.put("crotal", item.getCrotal());
            values.put("sexo", item.getSexo());
            values.put("marcado", item.getMarcado());
            values.put("fecha_alta", item.getFechaAlta());
            values.put("observaciones", item.getObservaciones());
            values.put("sincronizado", 1);
            values.put("eliminado", item.getEliminado());
            values.put("fecha_actualizacion", item.getFechaActualizacion());
            values.put("fecha_eliminado", item.getFechaEliminado());

            long resultado = db.insertWithOnConflict(
                    "lista_animales_detalle",
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
            );

            return resultado != -1;

        } catch (Exception e) {
            Log.e("DBHelper", "Error insertando/actualizando detalle lista desde servidor", e);
            return false;
        } finally {
            db.close();
        }
    }
}