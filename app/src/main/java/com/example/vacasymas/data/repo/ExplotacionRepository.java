package com.example.vacasymas.data.repo;

import android.util.Log;

import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.Explotacion;
import com.example.vacasymas.network.ApiClient;
import com.example.vacasymas.network.services.ExplotacionService;
import com.example.vacasymas.session.SessionManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExplotacionRepository {

    private static final String TAG = "ExplotacionRepository";

    private final DBHelper dbHelper;
    private final ExplotacionService api;

    public ExplotacionRepository(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.api = ApiClient.get().create(ExplotacionService.class);
    }

    public interface SyncCallback {
        void onSuccess();
        void onError(String error);
    }

    // =========================================================
    // ASYNC -> Para pantallas / UI
    // =========================================================

    public void descargarExplotaciones(SyncCallback callback) {
        String idUsuario = SessionManager.getIdUsuarioLocal();

        Call<List<Explotacion>> call = api.getExplotacionesPorUsuario(
                "*",
                "eq." + idUsuario,
                "nombre.asc"
        );

        call.enqueue(new Callback<List<Explotacion>>() {
            @Override
            public void onResponse(Call<List<Explotacion>> call, Response<List<Explotacion>> response) {
                if (!response.isSuccessful()) {
                    String msg = "Error HTTP al descargar explotaciones: " + response.code();
                    Log.e(TAG, msg);
                    if (callback != null) callback.onError(msg);
                    return;
                }

                List<Explotacion> lista = response.body();

                if (lista == null) {
                    Log.d(TAG, "La respuesta de explotaciones vino vacía");
                    if (callback != null) callback.onSuccess();
                    return;
                }

                for (Explotacion e : lista) {
                    dbHelper.insertarOActualizarExplotacion(e);
                }

                Log.d(TAG, "Explotaciones descargadas: " + lista.size());
                if (callback != null) callback.onSuccess();
            }

            @Override
            public void onFailure(Call<List<Explotacion>> call, Throwable t) {
                String msg = "Fallo al descargar explotaciones: " + t.getMessage();
                Log.e(TAG, msg, t);
                if (callback != null) callback.onError(msg);
            }
        });
    }

    public void subirExplotacionesNoSincronizadas(SyncCallback callback) {
        List<Explotacion> lista = dbHelper.obtenerExplotacionesNoSincronizadas();

        if (lista.isEmpty()) {
            Log.d(TAG, "No hay explotaciones para subir");
            if (callback != null) callback.onSuccess();
            return;
        }

        JsonArray jsonArray = construirJsonExplotaciones(lista);

        Call<Void> call = api.upsertExplotaciones(jsonArray);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    String msg = "Error HTTP al subir explotaciones: " + response.code();
                    Log.e(TAG, msg);
                    if (callback != null) callback.onError(msg);
                    return;
                }

                for (Explotacion e : lista) {
                    dbHelper.marcarExplotacionComoSincronizada(e.getId());
                }

                Log.d(TAG, "Explotaciones subidas correctamente");
                if (callback != null) callback.onSuccess();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String msg = "Fallo al subir explotaciones: " + t.getMessage();
                Log.e(TAG, msg, t);
                if (callback != null) callback.onError(msg);
            }
        });
    }

    // =========================================================
    // SYNC -> Para WorkManager / Sincronizadores
    // =========================================================

    public boolean descargarExplotacionesSync() {
        try {
            String idUsuario = SessionManager.getIdUsuarioLocal();

            Response<List<Explotacion>> response = api.getExplotacionesPorUsuario(
                    "*",
                    "eq." + idUsuario,
                    "nombre.asc"
            ).execute();

            if (!response.isSuccessful()) {
                Log.e(TAG, "Error HTTP al descargar explotaciones: " + response.code());
                return false;
            }

            List<Explotacion> lista = response.body();

            if (lista == null) {
                Log.d(TAG, "La respuesta sync de explotaciones vino vacía");
                return true;
            }

            for (Explotacion e : lista) {
                dbHelper.insertarOActualizarExplotacion(e);
            }

            Log.d(TAG, "Explotaciones descargadas sync: " + lista.size());
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error al descargar explotaciones sync", e);
            return false;
        }
    }

    public boolean subirExplotacionesNoSincronizadasSync() {
        try {
            List<Explotacion> lista = dbHelper.obtenerExplotacionesNoSincronizadas();

            if (lista.isEmpty()) {
                Log.d(TAG, "No hay explotaciones para subir sync");
                return true;
            }

            JsonArray jsonArray = construirJsonExplotaciones(lista);

            Response<Void> response = api.upsertExplotaciones(jsonArray).execute();

            if (!response.isSuccessful()) {
                Log.e(TAG, "Error HTTP al subir explotaciones sync: " + response.code());
                return false;
            }

            for (Explotacion e : lista) {
                dbHelper.marcarExplotacionComoSincronizada(e.getId());
            }

            Log.d(TAG, "Explotaciones subidas sync correctamente");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error al subir explotaciones sync", e);
            return false;
        }
    }

    // =========================================================
    // AUXILIAR
    // =========================================================

    private JsonArray construirJsonExplotaciones(List<Explotacion> lista) {
        JsonArray jsonArray = new JsonArray();

        for (Explotacion e : lista) {
            JsonObject obj = new JsonObject();

            obj.addProperty("id", e.getId());

            if (e.getIdSharepoint() != null) {
                obj.addProperty("id_sharepoint", e.getIdSharepoint());
            } else {
                obj.add("id_sharepoint", null);
            }

            if (e.getIdUsuario() != null) {
                obj.addProperty("id_usuario", e.getIdUsuario());
            } else {
                obj.add("id_usuario", null);
            }

            obj.addProperty("nombre", e.getNombre());
            obj.addProperty("fecha_actualizacion", e.getFechaActualizacion());
            obj.addProperty("sincronizado", 1);

            if (e.getEliminado() != null) {
                obj.addProperty("eliminado", e.getEliminado());
            } else {
                obj.addProperty("eliminado", 0);
            }

            if (e.getFechaEliminado() != null) {
                obj.addProperty("fecha_eliminado", e.getFechaEliminado());
            } else {
                obj.add("fecha_eliminado", null);
            }

            jsonArray.add(obj);
        }

        return jsonArray;
    }
}