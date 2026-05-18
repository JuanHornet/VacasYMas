package com.example.vacasymas.data.repo;

import android.util.Log;

import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.NotaAnimal;
import com.example.vacasymas.network.ApiClient;
import com.example.vacasymas.network.services.NotaAnimalService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Response;

public class NotaAnimalRepository {

    private static final String TAG = "NotaAnimalRepo";

    private final DBHelper dbHelper;
    private final NotaAnimalService api;

    public NotaAnimalRepository(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.api = ApiClient.get().create(NotaAnimalService.class);
    }

    public boolean subirNotasNoSincronizadasSync() {
        try {
            List<NotaAnimal> lista = dbHelper.obtenerNotasAnimalesNoSincronizadas();

            if (lista.isEmpty()) {
                Log.d(TAG, "No hay notas de animales para subir");
                return true;
            }

            JsonArray jsonArray = construirJsonNotas(lista);

            Log.d(TAG, "JSON notas animales: " + jsonArray.toString());

            Response<Void> response = api.upsertNotasAnimales(jsonArray).execute();

            if (!response.isSuccessful()) {
                String error = response.errorBody() != null
                        ? response.errorBody().string()
                        : "sin detalle";

                Log.e(TAG, "Error HTTP al subir notas animales: "
                        + response.code() + " | " + error);
                return false;
            }

            for (NotaAnimal n : lista) {
                dbHelper.marcarNotaAnimalComoSincronizada(n.getId());
            }

            Log.d(TAG, "Notas animales subidas correctamente: " + lista.size());
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error subiendo notas animales", e);
            return false;
        }
    }

    public boolean descargarNotasDesdeFechaSync(String ultimaFechaSync) {
        try {
            String filtroFecha = "gt." + ultimaFechaSync;

            Response<List<NotaAnimal>> response = api.getNotasDesdeFecha(
                    "*",
                    filtroFecha,
                    "fecha_actualizacion.asc"
            ).execute();

            if (!response.isSuccessful()) {
                Log.e(TAG, "Error HTTP descargando notas animales: " + response.code());
                return false;
            }

            List<NotaAnimal> lista = response.body();

            if (lista == null || lista.isEmpty()) {
                Log.d(TAG, "No hay notas animales nuevas");
                return true;
            }

            for (NotaAnimal n : lista) {
                dbHelper.insertarOActualizarNotaAnimalDesdeServidor(n);
            }

            Log.d(TAG, "Notas animales descargadas: " + lista.size());
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error descargando notas animales", e);
            return false;
        }
    }

    private JsonArray construirJsonNotas(List<NotaAnimal> lista) {
        JsonArray jsonArray = new JsonArray();

        for (NotaAnimal n : lista) {
            JsonObject obj = new JsonObject();

            obj.addProperty("id", n.getId());
            obj.addProperty("id_animal", n.getIdAnimal());
            obj.addProperty("fecha", n.getFecha());
            obj.addProperty("texto", n.getTexto());
            obj.addProperty("sincronizado", 1);
            obj.addProperty("eliminado", n.getEliminado() != null ? n.getEliminado() : 0);
            putStringOrNull(obj, "fecha_actualizacion", n.getFechaActualizacion());
            putStringOrNull(obj, "fecha_eliminado", n.getFechaEliminado());

            jsonArray.add(obj);
        }

        return jsonArray;
    }

    private void putStringOrNull(JsonObject obj, String key, String value) {
        if (value == null || value.trim().isEmpty()) {
            obj.add(key, com.google.gson.JsonNull.INSTANCE);
        } else {
            obj.addProperty(key, value);
        }
    }
}
