package com.example.vacasymas.data.repo;

import android.util.Log;

import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.PesoAnimal;
import com.example.vacasymas.network.ApiClient;
import com.example.vacasymas.network.services.PesoAnimalService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Response;

public class PesoAnimalRepository {

    private static final String TAG = "PesoAnimalRepo";

    private final DBHelper dbHelper;
    private final PesoAnimalService api;

    public PesoAnimalRepository(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.api = ApiClient.get().create(PesoAnimalService.class);
    }

    public boolean subirPesosNoSincronizadosSync() {
        try {
            List<PesoAnimal> lista = dbHelper.obtenerPesosNoSincronizados();

            if (lista.isEmpty()) {
                Log.d(TAG, "No hay pesos para subir");
                return true;
            }

            JsonArray jsonArray = construirJsonPesos(lista);

            Response<Void> response = api.upsertPesos(jsonArray).execute();

            if (!response.isSuccessful()) {
                Log.e(TAG, "Error HTTP al subir pesos: " + response.code());
                return false;
            }

            for (PesoAnimal p : lista) {
                dbHelper.marcarPesoComoSincronizado(p.getId());
            }

            Log.d(TAG, "Pesos subidos correctamente: " + lista.size());
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error subiendo pesos", e);
            return false;
        }
    }

    private JsonArray construirJsonPesos(List<PesoAnimal> lista) {
        JsonArray jsonArray = new JsonArray();

        for (PesoAnimal p : lista) {
            JsonObject obj = new JsonObject();

            obj.addProperty("id", p.getId());
            obj.addProperty("id_animal", p.getIdAnimal());
            obj.addProperty("id_explotacion_uuid", p.getIdExplotacionUuid());
            obj.addProperty("crotal", p.getCrotal());
            obj.addProperty("sexo", p.getSexo());
            obj.addProperty("fecha", p.getFecha());
            obj.addProperty("peso", p.getPeso());

            if (p.getObservaciones() != null) {
                obj.addProperty("observaciones", p.getObservaciones());
            } else {
                obj.add("observaciones", null);
            }

            obj.addProperty("sincronizado", 1);
            obj.addProperty("eliminado", p.getEliminado() != null ? p.getEliminado() : 0);
            obj.addProperty("fecha_actualizacion", p.getFechaActualizacion());

            if (p.getFechaEliminado() != null) {
                obj.addProperty("fecha_eliminado", p.getFechaEliminado());
            } else {
                obj.add("fecha_eliminado", null);
            }

            jsonArray.add(obj);
        }

        return jsonArray;
    }

    public boolean descargarPesosDesdeFechaSync(String ultimaFechaSync) {
        try {
            String filtroFecha = "gt." + ultimaFechaSync;

            Response<List<PesoAnimal>> response = api.getPesosDesdeFecha(
                    "*",
                    filtroFecha,
                    "fecha_actualizacion.asc"
            ).execute();

            if (!response.isSuccessful()) {
                String error = response.errorBody() != null
                        ? response.errorBody().string()
                        : "sin detalle";

                Log.e(TAG, "Error HTTP descargando pesos: "
                        + response.code() + " | " + error);

                return false;
            }

            List<PesoAnimal> lista = response.body();

            if (lista == null || lista.isEmpty()) {
                Log.d(TAG, "No hay pesos nuevos/modificados para descargar");
                return true;
            }

            for (PesoAnimal p : lista) {
                dbHelper.insertarOActualizarPesoDesdeServidor(p);
            }

            Log.d(TAG, "Pesos descargados desde Supabase: " + lista.size());
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error descargando pesos", e);
            return false;
        }
    }
}
