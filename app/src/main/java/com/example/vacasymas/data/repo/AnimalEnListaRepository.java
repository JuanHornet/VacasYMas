package com.example.vacasymas.data.repo;

import android.util.Log;

import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.AnimalEnLista;
import com.example.vacasymas.network.ApiClient;
import com.example.vacasymas.network.services.ListaAnimalDetalleService;

import java.util.List;

import retrofit2.Response;

public class AnimalEnListaRepository {

    private static final String TAG = "AnimalEnListaRepo";

    private final DBHelper dbHelper;
    private final ListaAnimalDetalleService api;

    public AnimalEnListaRepository(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.api = ApiClient.get().create(ListaAnimalDetalleService.class);
    }

    public boolean subirDetallesNoSincronizados() {

        try {

            List<AnimalEnLista> pendientes =
                    dbHelper.obtenerDetallesListaNoSincronizados();

            if (pendientes == null || pendientes.isEmpty()) {

                Log.d(TAG, "No hay detalles pendientes");
                return true;
            }

            Response<Void> response = api.upsertDetalles(
                    "id",
                    pendientes,
                    "resolution=merge-duplicates,return=minimal"
            ).execute();

            if (!response.isSuccessful()) {

                Log.e(TAG,
                        "Error subiendo detalles: HTTP "
                                + response.code());

                return false;
            }

            for (AnimalEnLista item : pendientes) {

                dbHelper.marcarDetalleListaComoSincronizado(
                        item.getIdDetalle()
                );
            }

            Log.d(TAG,
                    "Detalles subidos: "
                            + pendientes.size());

            return true;

        } catch (Exception e) {

            Log.e(TAG,
                    "Error general subiendo detalles",
                    e);

            return false;
        }
    }

    public boolean descargarDetallesDesdeFecha(String ultimaFechaSync) {

        try {

            String filtroFecha = "gt." + ultimaFechaSync;

            Response<List<AnimalEnLista>> response =
                    api.getDetallesDesdeFecha(
                            "*",
                            filtroFecha,
                            "fecha_actualizacion.asc"
                    ).execute();

            if (!response.isSuccessful()) {

                Log.e(TAG,
                        "Error descargando detalles: HTTP "
                                + response.code());

                return false;
            }

            List<AnimalEnLista> detalles = response.body();

            if (detalles == null || detalles.isEmpty()) {
                return true;
            }

            for (AnimalEnLista item : detalles) {

                dbHelper.insertarOActualizarDetalleListaDesdeServidor(
                        item
                );
            }

            Log.d(TAG,
                    "Detalles descargados: "
                            + detalles.size());

            return true;

        } catch (Exception e) {

            Log.e(TAG,
                    "Error general descargando detalles",
                    e);

            return false;
        }
    }
}
