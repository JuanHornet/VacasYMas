package com.example.vacasymas.sync;

import android.content.Context;
import android.util.Log;

import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.repo.NotaAnimalRepository;

public class SincronizadorNotasAnimales {

    private static final String TAG = "SyncNotasAnimales";

    private final NotaAnimalRepository repository;

    public SincronizadorNotasAnimales(Context context) {
        Context appContext = context.getApplicationContext();
        this.repository = new NotaAnimalRepository(new DBHelper(appContext));
    }

    public boolean sincronizarTodo() {
        Log.d(TAG, "=== Inicio sincronizarTodo notas animales ===");

        boolean subidaOk = repository.subirNotasNoSincronizadasSync();

        boolean bajadaOk = repository.descargarNotasDesdeFechaSync(
                "1970-01-01T00:00:00Z"
        );

        boolean resultado = subidaOk && bajadaOk;

        Log.d(TAG, "=== Fin sincronizarTodo notas animales. Subida: "
                + subidaOk
                + " | Bajada: "
                + bajadaOk
                + " | Resultado: "
                + resultado
                + " ===");

        return resultado;
    }
}