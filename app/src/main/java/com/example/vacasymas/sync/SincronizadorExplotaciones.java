package com.example.vacasymas.sync;

import android.content.Context;
import android.util.Log;

import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.repo.ExplotacionRepository;

public class SincronizadorExplotaciones {

    private static final String TAG = "SyncExplotaciones";

    private final Context context;
    private final ExplotacionRepository explotacionRepository;

    public SincronizadorExplotaciones(Context context) {
        this.context = context.getApplicationContext();
        this.explotacionRepository = new ExplotacionRepository(new DBHelper(this.context));
    }

    public SincronizadorExplotaciones(Context context, ExplotacionRepository explotacionRepository) {
        this.context = context.getApplicationContext();
        this.explotacionRepository = explotacionRepository;
    }

    public boolean sincronizarSubida() {
        Log.d(TAG, "Iniciando sincronización de explotaciones (subida)");
        return explotacionRepository.subirExplotacionesNoSincronizadasSync();
    }

    public boolean sincronizarBajada() {
        Log.d(TAG, "Iniciando sincronización de explotaciones (bajada)");
        return explotacionRepository.descargarExplotacionesSync();
    }

    public boolean sincronizarTodo() {
        Log.d(TAG, "=== Inicio sincronizarTodo explotaciones ===");

        boolean subidaOk = sincronizarSubida();
        boolean bajadaOk = false;

        if (subidaOk) {
            bajadaOk = sincronizarBajada();
        } else {
            Log.e(TAG, "No se ejecuta la bajada porque la subida ha fallado");
        }

        boolean resultado = subidaOk && bajadaOk;

        Log.d(TAG, "=== Fin sincronizarTodo explotaciones. Resultado: " + resultado + " ===");
        return resultado;
    }
}