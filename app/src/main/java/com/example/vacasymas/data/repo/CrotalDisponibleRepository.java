package com.example.vacasymas.data.repo;

import android.util.Log;

import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.CrotalDisponible;
import com.example.vacasymas.network.ApiClient;
import com.example.vacasymas.network.services.CrotalDisponibleService;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class CrotalDisponibleRepository {

    private static final String TAG = "CrotalDispRepo";

    private final DBHelper dbHelper;
    private final CrotalDisponibleService api;

    public CrotalDisponibleRepository(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.api = ApiClient.get().create(CrotalDisponibleService.class);
    }

    public boolean insertarRangoCrotales(String primerCrotal,
                                         String ultimoCrotal,
                                         String idExplotacionUuid,
                                         String fechaAsignacion,
                                         String observaciones,
                                         String fechaActualizacion) {
        return dbHelper.insertarRangoCrotales(
                primerCrotal,
                ultimoCrotal,
                idExplotacionUuid,
                fechaAsignacion,
                observaciones,
                fechaActualizacion
        );
    }

    public List<CrotalDisponible> obtenerCrotalesDisponibles(String idExplotacionUuid) {
        return dbHelper.obtenerCrotalesDisponibles(idExplotacionUuid);
    }

    public boolean marcarCrotalComoUsado(String crotal,
                                         String idAnimalUsado,
                                         String fechaUso,
                                         String fechaActualizacion) {
        return dbHelper.marcarCrotalComoUsado(
                crotal,
                idAnimalUsado,
                fechaUso,
                fechaActualizacion
        );
    }

    public int contarCrotalesPorEstado(String idExplotacionUuid, String estado) {
        return dbHelper.contarCrotalesPorEstado(idExplotacionUuid, estado);
    }

    public String obtenerSiguienteCrotalDisponible(String idExplotacionUuid) {
        return dbHelper.obtenerSiguienteCrotalDisponible(idExplotacionUuid);
    }

    public List<CrotalDisponible> obtenerCrotalesPorEstado(String idExplotacionUuid, String estado) {
        return dbHelper.obtenerCrotalesPorEstado(idExplotacionUuid, estado);
    }

    public boolean anularCrotal(String crotal, String observaciones, String fechaActualizacion) {
        return dbHelper.anularCrotal(crotal, observaciones, fechaActualizacion);
    }

    public boolean restaurarCrotalDisponible(String crotal, String fechaActualizacion) {
        return dbHelper.restaurarCrotalDisponible(crotal, fechaActualizacion);
    }

    public boolean subirCrotalesNoSincronizados() {
        List<CrotalDisponible> pendientes = dbHelper.obtenerCrotalesNoSincronizados();

        if (pendientes == null || pendientes.isEmpty()) {
            Log.d(TAG, "No hay crotales pendientes de sincronizar");
            return true;
        }

        try {
            Response<Void> response = api.upsertCrotales("id", pendientes).execute();

            if (!response.isSuccessful()) {
                String error = response.errorBody() != null
                        ? response.errorBody().string()
                        : "sin detalle";

                Log.e(TAG, "Error subiendo crotales. HTTP "
                        + response.code() + " | " + error);

                return false;
            }

            for (CrotalDisponible c : pendientes) {
                dbHelper.marcarCrotalSincronizado(c.getId());
            }

            Log.d(TAG, "Crotales subidos correctamente: " + pendientes.size());
            return true;

        } catch (IOException e) {
            Log.e(TAG, "Error de red subiendo crotales", e);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error general subiendo crotales", e);
            return false;
        }
    }

    public boolean descargarCrotalesDesdeSupabase() {
        try {
            String ultimaFecha = dbHelper.obtenerUltimaFechaActualizacion("crotales_disponibles");

            String filtroFecha = ultimaFecha != null
                    ? "gt." + ultimaFecha
                    : "not.is.null";

            Response<List<CrotalDisponible>> response =
                    api.getCrotalesDesdeFecha(
                            "*",
                            filtroFecha,
                            "fecha_actualizacion.asc"
                    ).execute();

            if (!response.isSuccessful()) {
                String error = response.errorBody() != null
                        ? response.errorBody().string()
                        : "sin detalle";

                Log.e(TAG, "Error descargando crotales. HTTP "
                        + response.code() + " | " + error);

                return false;
            }

            List<CrotalDisponible> lista = response.body();

            if (lista == null || lista.isEmpty()) {
                Log.d(TAG, "No hay crotales nuevos para descargar");
                return true;
            }

            for (CrotalDisponible c : lista) {
                dbHelper.insertarOActualizarCrotalDesdeSupabase(c);
            }

            Log.d(TAG, "Crotales descargados correctamente: " + lista.size());
            return true;

        } catch (IOException e) {
            Log.e(TAG, "Error de red descargando crotales", e);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error general descargando crotales", e);
            return false;
        }
    }
}