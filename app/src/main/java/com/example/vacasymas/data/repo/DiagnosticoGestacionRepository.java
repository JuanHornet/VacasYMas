package com.example.vacasymas.data.repo;

import android.util.Log;

import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.DiagnosticoGestacion;
import com.example.vacasymas.network.ApiClient;
import com.example.vacasymas.network.services.DiagnosticoGestacionService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Response;

public class DiagnosticoGestacionRepository {

    private static final String TAG = "DiagGestacionRepo";

    private final DBHelper dbHelper;
    private final DiagnosticoGestacionService api;

    public DiagnosticoGestacionRepository(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.api = ApiClient.get().create(DiagnosticoGestacionService.class);
    }

    public boolean subirDiagnosticosNoSincronizadosSync() {
        try {
            List<DiagnosticoGestacion> lista = dbHelper.obtenerDiagnosticosGestacionNoSincronizados();

            if (lista.isEmpty()) {
                Log.d(TAG, "No hay diagnósticos de gestación para subir");
                return true;
            }

            JsonArray jsonArray = construirJsonDiagnosticos(lista);

            Response<Void> response = api.upsertDiagnosticos(jsonArray).execute();

            if (!response.isSuccessful()) {
                Log.e(TAG, "Error HTTP al subir diagnósticos: " + response.code());
                return false;
            }

            for (DiagnosticoGestacion d : lista) {
                dbHelper.marcarDiagnosticoGestacionComoSincronizado(d.getId());
            }

            Log.d(TAG, "Diagnósticos de gestación subidos correctamente: " + lista.size());
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error subiendo diagnósticos de gestación", e);
            return false;
        }
    }

    private JsonArray construirJsonDiagnosticos(List<DiagnosticoGestacion> lista) {
        JsonArray jsonArray = new JsonArray();

        for (DiagnosticoGestacion d : lista) {
            JsonObject obj = new JsonObject();

            obj.addProperty("id", d.getId());
            obj.addProperty("id_animal", d.getIdAnimal());
            obj.addProperty("id_explotacion_uuid", d.getIdExplotacionUuid());
            obj.addProperty("fecha", d.getFecha());
            obj.addProperty("resultado", d.getResultado());

            if (d.getObservaciones() != null) {
                obj.addProperty("observaciones", d.getObservaciones());
            } else {
                obj.add("observaciones", null);
            }

            obj.addProperty("sincronizado", 1);
            obj.addProperty("eliminado", d.getEliminado() != null ? d.getEliminado() : 0);
            obj.addProperty("fecha_actualizacion", d.getFechaActualizacion());

            if (d.getFechaEliminado() != null) {
                obj.addProperty("fecha_eliminado", d.getFechaEliminado());
            } else {
                obj.add("fecha_eliminado", null);
            }

            jsonArray.add(obj);
        }

        return jsonArray;
    }

    public boolean descargarDiagnosticosDesdeFechaSync(String ultimaFechaSync) {
        try {
            String filtroFecha = "gt." + ultimaFechaSync;

            Response<List<DiagnosticoGestacion>> response = api.getDiagnosticosDesdeFecha(
                    "*",
                    filtroFecha,
                    "fecha_actualizacion.asc"
            ).execute();

            if (!response.isSuccessful()) {
                String error = response.errorBody() != null
                        ? response.errorBody().string()
                        : "sin detalle";

                Log.e(TAG, "Error HTTP descargando diagnósticos: "
                        + response.code() + " | " + error);

                return false;
            }

            List<DiagnosticoGestacion> lista = response.body();

            if (lista == null || lista.isEmpty()) {
                Log.d(TAG, "No hay diagnósticos nuevos/modificados para descargar");
                return true;
            }

            for (DiagnosticoGestacion d : lista) {
                dbHelper.insertarOActualizarDiagnosticoGestacionDesdeServidor(d);
            }

            Log.d(TAG, "Diagnósticos descargados desde Supabase: " + lista.size());
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error descargando diagnósticos de gestación", e);
            return false;
        }
    }
}
