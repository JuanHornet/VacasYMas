package com.example.vacasymas.data.repo;

import android.util.Log;

import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.Animal;
import com.example.vacasymas.data.models.EventoReproductivo;
import com.example.vacasymas.network.ApiClient;
import com.example.vacasymas.network.services.EventoReproductivoService;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Response;

public class EventoReproductivoRepository {

    private static final String TAG = "EventoReproRepo";

    private final DBHelper dbHelper;
    private final EventoReproductivoService api;

    public EventoReproductivoRepository(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.api = ApiClient.get().create(EventoReproductivoService.class);
    }

    public boolean registrarPartoPendienteCrotal(EventoReproductivo evento) {
        return dbHelper.registrarPartoPendienteCrotal(evento);
    }

    public boolean identificarCria(String idEvento, Animal cria, String fechaActualizacion) {
        return dbHelper.identificarCria(idEvento, cria, fechaActualizacion);
    }

    public List<EventoReproductivo> obtenerCriasPendientesIdentificar(String idExplotacionUuid) {
        return dbHelper.obtenerCriasPendientesIdentificar(idExplotacionUuid);
    }

    public boolean subirEventosNoSincronizadosSync() {
        try {
            List<EventoReproductivo> lista = dbHelper.obtenerEventosReproductivosNoSincronizados();

            if (lista == null || lista.isEmpty()) {
                Log.d(TAG, "No hay eventos reproductivos pendientes de subir");
                return true;
            }

            for (EventoReproductivo evento : lista) {

                JsonArray jsonArray = new JsonArray();
                jsonArray.add(construirJsonEvento(evento));

                Response<Void> response = api.upsertEventos(
                        "resolution=merge-duplicates,return=minimal",
                        jsonArray
                ).execute();

                if (!response.isSuccessful()) {

                    String error = response.errorBody() != null
                            ? response.errorBody().string()
                            : "sin detalle";

                    Log.e(TAG,
                            "Error subiendo evento reproductivo id="
                                    + evento.getId()
                                    + " | HTTP " + response.code()
                                    + " | " + error);

                    return false;
                }

                dbHelper.marcarEventoReproductivoComoSincronizado(evento.getId());
            }

            Log.d(TAG, "Eventos reproductivos subidos correctamente: " + lista.size());
            return true;


        } catch (Exception e) {
            Log.e(TAG, "Error subiendo eventos reproductivos", e);
            return false;
        }
    }

    public boolean descargarEventosDesdeFechaSync(String ultimaFechaSync) {
        try {
            String filtroFecha = "gt." + ultimaFechaSync;

            Response<List<EventoReproductivo>> response = api.getEventosDesdeFecha(
                    "*",
                    filtroFecha,
                    "fecha_actualizacion.asc"
            ).execute();

            if (!response.isSuccessful()) {
                String error = response.errorBody() != null
                        ? response.errorBody().string()
                        : "sin detalle";

                Log.e(TAG, "Error HTTP descargando eventos reproductivos: "
                        + response.code() + " | " + error);

                return false;
            }

            List<EventoReproductivo> lista = response.body();

            if (lista == null || lista.isEmpty()) {
                Log.d(TAG, "No hay eventos reproductivos nuevos/modificados");
                return true;
            }

            int insertados = 0;
            int errores = 0;

            for (EventoReproductivo e : lista) {

                boolean okInsert =
                        dbHelper.insertarOActualizarEventoReproductivoDesdeServidor(e);

                if (okInsert) {
                    insertados++;
                } else {
                    errores++;

                    Log.e(TAG,
                            "Error insertando evento reproductivo en SQLite. " +
                                    "id=" + e.getId() +
                                    " | madre=" + e.getIdMadre() +
                                    " | cria=" + e.getIdCria() +
                                    " | explotacion=" + e.getIdExplotacionUuid());
                }
            }

            Log.d(TAG,
                    "Eventos reproductivos descargados: " + lista.size() +
                            " | insertados=" + insertados +
                            " | errores=" + errores);

            return errores == 0;


        } catch (Exception e) {
            Log.e(TAG, "Error descargando eventos reproductivos", e);
            return false;
        }
    }

    private JsonArray construirJsonEventos(List<EventoReproductivo> lista) {
        JsonArray array = new JsonArray();

        for (EventoReproductivo e : lista) {
            JsonObject obj = new JsonObject();

            putString(obj, "id", e.getId());
            putString(obj, "id_madre", e.getIdMadre());
            putString(obj, "id_cria", e.getIdCria());
            putString(obj, "id_explotacion_uuid", e.getIdExplotacionUuid());
            putString(obj, "tipo_evento", e.getTipoEvento());
            putString(obj, "fecha_evento", e.getFechaEvento());
            putString(obj, "resultado_cria", e.getResultadoCria());

            obj.addProperty("cria_identificada",
                    e.getCriaIdentificada() != null ? e.getCriaIdentificada() : 0);

            putString(obj, "sexo_estimado", e.getSexoEstimado());
            putString(obj, "raza_estimada", e.getRazaEstimada());
            putString(obj, "capa_estimada", e.getCapaEstimada());
            putString(obj, "cercado", e.getCercado());
            putString(obj, "observaciones", e.getObservaciones());

            obj.addProperty("sincronizado", 1);
            obj.addProperty("eliminado", e.getEliminado() != null ? e.getEliminado() : 0);

            putString(obj, "fecha_actualizacion", e.getFechaActualizacion());
            putString(obj, "fecha_eliminado", e.getFechaEliminado());
            putString(obj, "origen",
                    e.getOrigen() != null ? e.getOrigen() : "APP");

            Log.d(TAG, "Evento subida: " + obj.toString());

            array.add(obj);
        }

        return array;
    }

    private void putString(JsonObject obj, String key, String value) {
        if (value != null) {
            obj.addProperty(key, value);
        } else {
            obj.add(key, JsonNull.INSTANCE);
        }
    }

    public boolean marcarCriaMuertaAntesIdentificar(String idEvento, String observaciones, String fechaActualizacion) {
        return dbHelper.marcarCriaMuertaAntesIdentificar(idEvento, observaciones, fechaActualizacion);
    }

    public int contarCriasPendientesIdentificar(String idExplotacionUuid) {
        return dbHelper.contarCriasPendientesIdentificar(idExplotacionUuid);
    }

    private JsonObject construirJsonEvento(EventoReproductivo e) {
        JsonObject obj = new JsonObject();

        putString(obj, "id", e.getId());
        putString(obj, "id_madre", e.getIdMadre());
        putString(obj, "id_cria", e.getIdCria());
        putString(obj, "id_explotacion_uuid", e.getIdExplotacionUuid());
        putString(obj, "tipo_evento", e.getTipoEvento());
        putString(obj, "fecha_evento", e.getFechaEvento());
        putString(obj, "resultado_cria", e.getResultadoCria());

        obj.addProperty("cria_identificada",
                e.getCriaIdentificada() != null ? e.getCriaIdentificada() : 0);

        putString(obj, "sexo_estimado", e.getSexoEstimado());
        putString(obj, "raza_estimada", e.getRazaEstimada());
        putString(obj, "capa_estimada", e.getCapaEstimada());
        putString(obj, "cercado", e.getCercado());
        putString(obj, "observaciones", e.getObservaciones());

        obj.addProperty("sincronizado", 1);
        obj.addProperty("eliminado", e.getEliminado() != null ? e.getEliminado() : 0);

        putString(obj, "fecha_actualizacion", e.getFechaActualizacion());
        putString(obj, "fecha_eliminado", e.getFechaEliminado());

        putString(obj, "origen",
                e.getOrigen() != null ? e.getOrigen() : "APP");

        Log.d(TAG, "Evento subida individual: " + obj);

        return obj;
    }
}
