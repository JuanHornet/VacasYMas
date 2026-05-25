package com.example.vacasymas.sync;

import android.content.Context;
import android.util.Log;

import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.repo.ListaAnimalRepository;

public class SincronizadorListasAnimales {

    private static final String TAG = "SyncListasAnimales";

    private final Context context;
    private final DBHelper dbHelper;
    private final ListaAnimalRepository repository;

    public SincronizadorListasAnimales(Context context) {
        this.context = context;
        this.dbHelper = new DBHelper(context);
        this.repository = new ListaAnimalRepository(dbHelper);
    }

    public boolean sincronizar() {

        try {

            Log.d(TAG, "=== Inicio sync listas animales ===");

            boolean subidaOk =
                    repository.subirListasNoSincronizadas();

            if (!subidaOk) {

                Log.e(TAG,
                        "Error subiendo listas");

                return false;
            }

            String ultimaFecha =
                    dbHelper.obtenerUltimaFechaSyncListasAnimales();

            if (ultimaFecha == null ||
                    ultimaFecha.trim().isEmpty()) {

                ultimaFecha = "2000-01-01T00:00:00Z";
            }

            boolean descargaOk =
                    repository.descargarListasDesdeFecha(
                            ultimaFecha
                    );

            if (!descargaOk) {

                Log.e(TAG,
                        "Error descargando listas");

                return false;
            }

            dbHelper.guardarUltimaFechaSyncListasAnimales(
                    com.example.vacasymas.base.FechaUtils.ahoraIso()
            );

            Log.d(TAG,
                    "=== Fin sync listas animales ===");

            return true;

        } catch (Exception e) {

            Log.e(TAG,
                    "Error sincronizando listas animales",
                    e);

            return false;
        }
    }
}
