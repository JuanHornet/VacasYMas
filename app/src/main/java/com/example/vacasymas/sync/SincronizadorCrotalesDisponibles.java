package com.example.vacasymas.sync;

import android.content.Context;
import android.util.Log;

import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.repo.CrotalDisponibleRepository;

public class SincronizadorCrotalesDisponibles {

    private static final String TAG = "SyncCrotalesDisp";

    private final DBHelper dbHelper;
    private final CrotalDisponibleRepository repository;

    public SincronizadorCrotalesDisponibles(Context context) {
        this.dbHelper = new DBHelper(context.getApplicationContext());
        this.repository = new CrotalDisponibleRepository(dbHelper);
    }

    public boolean sincronizarTodo() {
        Log.d(TAG, "=== Inicio sync crotales disponibles ===");

        boolean subidaOk = repository.subirCrotalesNoSincronizados();

        if (!subidaOk) {
            Log.e(TAG, "Error subiendo crotales disponibles");
            return false;
        }

        boolean descargaOk = repository.descargarCrotalesDesdeSupabase();

        Log.d(TAG, "=== Fin sync crotales disponibles. OK: " + descargaOk + " ===");

        return descargaOk;
    }
}