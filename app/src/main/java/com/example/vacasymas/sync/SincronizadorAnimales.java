package com.example.vacasymas.sync;

import android.content.Context;
import android.util.Log;

import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.repo.AnimalRepository;

public class SincronizadorAnimales {

    private static final String TAG = "SyncAnimales";

    private final Context context;
    private final AnimalRepository animalRepository;

    public SincronizadorAnimales(Context context) {
        this.context = context.getApplicationContext();
        this.animalRepository = new AnimalRepository(new DBHelper(this.context));
    }

    public SincronizadorAnimales(Context context, AnimalRepository animalRepository) {
        this.context = context.getApplicationContext();
        this.animalRepository = animalRepository;
    }

    public boolean sincronizarSubida() {
        Log.d(TAG, "Iniciando sincronización de animales (subida)");
        boolean ok = animalRepository.subirAnimalesNoSincronizados();

        if (ok) {
            Log.d(TAG, "Sincronización de subida completada correctamente");
        } else {
            Log.e(TAG, "Error en sincronización de subida");
        }

        return ok;
    }

    public boolean sincronizarBajada() {
        String ultimaSync = SyncPrefs.getLastSyncAnimales(context);

        boolean hayAnimalesLocales = animalRepository.hayAnimales();

        if (!hayAnimalesLocales || ultimaSync == null || ultimaSync.trim().isEmpty()) {
            Log.d(TAG, "SQLite sin animales o sin última sync. Haciendo descarga inicial completa de animales");

            String ultimaFechaInicial = animalRepository.descargarTodosLosAnimalesYDevolverUltimaFecha();

            if (ultimaFechaInicial != null) {
                SyncPrefs.setLastSyncAnimales(context, ultimaFechaInicial);
                Log.d(TAG, "Descarga inicial de animales completada correctamente");
                return true;
            } else {
                Log.e(TAG, "Error en descarga inicial de animales");
                return false;
            }
        }

        Log.d(TAG, "Iniciando sincronización de animales (bajada) desde: " + ultimaSync);

        String nuevaUltimaFecha = animalRepository.descargarAnimalesModificadosYDevolverUltimaFecha(ultimaSync);

        if (nuevaUltimaFecha != null) {
            SyncPrefs.setLastSyncAnimales(context, nuevaUltimaFecha);
            Log.d(TAG, "Sincronización de bajada completada correctamente");
            return true;
        } else {
            Log.e(TAG, "Error en sincronización de bajada");
            return false;
        }
    }

    public boolean sincronizarTodo() {
        Log.d(TAG, "=== Inicio sincronizarTodo animales ===");

        boolean subidaOk = sincronizarSubida();
        boolean bajadaOk = sincronizarBajada();

        boolean resultado = subidaOk && bajadaOk;

        Log.d(TAG, "=== Fin sincronizarTodo animales. Resultado: " + resultado + " ===");
        return resultado;
    }
}