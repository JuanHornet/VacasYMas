package com.example.vacasymas.sync;

import android.content.Context;
import android.util.Log;

import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.repo.DiagnosticoGestacionRepository;

public class SincronizadorDiagnosticosGestacion {

    private static final String TAG = "SyncDiagGestacion";

    private final Context context;
    private final DiagnosticoGestacionRepository repository;

    public SincronizadorDiagnosticosGestacion(Context context) {
        this.context = context.getApplicationContext();
        this.repository = new DiagnosticoGestacionRepository(new DBHelper(this.context));
    }

    public boolean sincronizarSubida() {
        Log.d(TAG, "Iniciando subida de diagnósticos de gestación");
        return repository.subirDiagnosticosNoSincronizadosSync();
    }

    public boolean sincronizarTodo() {
        Log.d(TAG, "=== Inicio sincronizarTodo diagnósticos gestación ===");

        boolean subidaOk = sincronizarSubida();

        // De momento fecha mínima para descargar todo el historial
        boolean bajadaOk = repository.descargarDiagnosticosDesdeFechaSync("1970-01-01T00:00:00Z");

        boolean resultado = subidaOk && bajadaOk;

        Log.d(TAG, "=== Fin sincronizarTodo diagnósticos gestación. Subida: "
                + subidaOk + " | Bajada: " + bajadaOk + " ===");

        return resultado;
    }
}
