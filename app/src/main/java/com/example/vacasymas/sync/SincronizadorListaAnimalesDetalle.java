package com.example.vacasymas.sync;

import android.content.Context;
import android.util.Log;

import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.repo.AnimalEnListaRepository;

public class SincronizadorListaAnimalesDetalle {

    private static final String TAG = "SyncListaDetalle";

    private final Context context;
    private final DBHelper dbHelper;
    private final AnimalEnListaRepository repository;

    public SincronizadorListaAnimalesDetalle(Context context) {

        this.context = context;

        this.dbHelper = new DBHelper(context);

        this.repository =
                new AnimalEnListaRepository(dbHelper);
    }

    public boolean sincronizar() {

        try {

            Log.d(TAG,
                    "=== Inicio sync detalle listas ===");

            boolean subidaOk =
                    repository.subirDetallesNoSincronizados();

            if (!subidaOk) {

                Log.e(TAG,
                        "Error subiendo detalles");

                return false;
            }

            String ultimaFecha =
                    dbHelper.obtenerUltimaFechaSyncListaAnimalesDetalle();

            if (ultimaFecha == null ||
                    ultimaFecha.trim().isEmpty()) {

                ultimaFecha = "2000-01-01T00:00:00Z";
            }

            boolean descargaOk =
                    repository.descargarDetallesDesdeFecha(
                            ultimaFecha
                    );

            if (!descargaOk) {

                Log.e(TAG,
                        "Error descargando detalles");

                return false;
            }

            dbHelper.guardarUltimaFechaSyncListaAnimalesDetalle(
                    com.example.vacasymas.base.FechaUtils.ahoraIso()
            );

            Log.d(TAG,
                    "=== Fin sync detalle listas ===");

            return true;

        } catch (Exception e) {

            Log.e(TAG,
                    "Error sincronizando detalle listas",
                    e);

            return false;
        }
    }
}
