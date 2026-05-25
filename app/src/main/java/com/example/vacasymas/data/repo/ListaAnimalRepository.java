package com.example.vacasymas.data.repo;

import android.util.Log;

import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.ListaAnimal;
import com.example.vacasymas.network.ApiClient;
import com.example.vacasymas.network.services.ListaAnimalService;

import java.util.List;

import retrofit2.Response;

public class ListaAnimalRepository {

    private static final String TAG = "ListaAnimalRepo";

    private final DBHelper dbHelper;
    private final ListaAnimalService api;

    public ListaAnimalRepository(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.api = ApiClient.get().create(ListaAnimalService.class);
    }

    public boolean subirListasNoSincronizadas() {
        try {
            List<ListaAnimal> pendientes = dbHelper.obtenerListasAnimalesNoSincronizadas();

            if (pendientes == null || pendientes.isEmpty()) {
                Log.d(TAG, "No hay listas pendientes");
                return true;
            }

            Response<Void> response = api.upsertListas(
                    "id",
                    pendientes,
                    "resolution=merge-duplicates,return=minimal"
            ).execute();

            if (!response.isSuccessful()) {
                Log.e(TAG, "Error subiendo listas: HTTP " + response.code());
                return false;
            }

            for (ListaAnimal lista : pendientes) {
                dbHelper.marcarListaAnimalComoSincronizada(lista.getId());
            }

            Log.d(TAG, "Listas subidas: " + pendientes.size());
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error general subiendo listas", e);
            return false;
        }
    }

    public boolean descargarListasDesdeFecha(String ultimaFechaSync) {
        try {
            String filtroFecha = "gt." + ultimaFechaSync;

            Response<List<ListaAnimal>> response = api.getListasDesdeFecha(
                    "*",
                    filtroFecha,
                    "fecha_actualizacion.asc"
            ).execute();

            if (!response.isSuccessful()) {
                Log.e(TAG, "Error descargando listas: HTTP " + response.code());
                return false;
            }

            List<ListaAnimal> listas = response.body();

            if (listas == null || listas.isEmpty()) {
                return true;
            }

            for (ListaAnimal lista : listas) {
                dbHelper.insertarOActualizarListaAnimalDesdeServidor(lista);
            }

            Log.d(TAG, "Listas descargadas: " + listas.size());
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error general descargando listas", e);
            return false;
        }
    }
}