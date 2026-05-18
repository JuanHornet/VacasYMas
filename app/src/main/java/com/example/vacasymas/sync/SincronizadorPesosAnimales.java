package com.example.vacasymas.sync;

import android.content.Context;
import android.util.Log;

import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.repo.PesoAnimalRepository;

public class SincronizadorPesosAnimales {

    private static final String TAG = "SyncPesosAnimales";

    private final PesoAnimalRepository repository;

    public SincronizadorPesosAnimales(Context context) {
        Context appContext = context.getApplicationContext();
        this.repository = new PesoAnimalRepository(new DBHelper(appContext));
    }

    public boolean sincronizarSubida() {
        Log.d(TAG, "Iniciando subida de pesos");
        return repository.subirPesosNoSincronizadosSync();
    }

    public boolean sincronizarTodo() {
        Log.d(TAG, "=== Inicio sincronizarTodo pesos ===");

        boolean subidaOk = sincronizarSubida();
        boolean bajadaOk = repository.descargarPesosDesdeFechaSync("1970-01-01T00:00:00Z");

        Log.d(TAG, "=== Fin sincronizarTodo pesos. Subida: "
                + subidaOk + " | Bajada: " + bajadaOk + " ===");

        return subidaOk && bajadaOk;
    }
}
