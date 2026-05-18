package com.example.vacasymas.sync;

import android.content.Context;
import android.util.Log;

import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.repo.EventoReproductivoRepository;

public class SincronizadorEventosReproductivos {

    private static final String TAG = "SyncEventosRepro";

    private final Context context;
    private final EventoReproductivoRepository repository;

    public SincronizadorEventosReproductivos(Context context) {
        this.context = context.getApplicationContext();
        this.repository = new EventoReproductivoRepository(new DBHelper(this.context));
    }

    public boolean sincronizarSubida() {
        Log.d(TAG, "Iniciando subida de eventos reproductivos");
        return repository.subirEventosNoSincronizadosSync();
    }


    public boolean sincronizarBajada() {
        String ultimaSync = SyncPrefs.getLastSyncEventosReproductivos(context);

        if (ultimaSync == null || ultimaSync.trim().isEmpty()) {
            ultimaSync = "1900-01-01T00:00:00";
        }

        Log.d(TAG, "Iniciando bajada de eventos reproductivos desde: " + ultimaSync);

        boolean ok = repository.descargarEventosDesdeFechaSync(ultimaSync);

        if (ok) {
            SyncPrefs.setLastSyncEventosReproductivos(
                    context,
                    com.example.vacasymas.base.FechaUtils.ahoraIso()
            );
        }

        return ok;
    }

    public boolean sincronizarTodo() {
        boolean subidaOk = sincronizarSubida();
        boolean bajadaOk = sincronizarBajada();

        boolean resultado = subidaOk && bajadaOk;

        Log.d(TAG, "Fin sync eventos reproductivos. Resultado: " + resultado);

        return resultado;
    }
}
