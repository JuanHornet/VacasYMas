package com.example.vacasymas.sync.SyncWorker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.vacasymas.sync.SincronizadorGeneral;

public class SyncWorker extends Worker {

    private static final String TAG = "SyncWorker";

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "=== Inicio SyncWorker ===");

        try {
            Context context = getApplicationContext();

            SincronizadorGeneral sincronizadorGeneral = new SincronizadorGeneral(context);
            boolean ok = sincronizadorGeneral.sincronizarTodo();

            if (ok) {
                Log.d(TAG, "=== SyncWorker finalizado OK ===");
                return Result.success();
            } else {
                Log.e(TAG, "=== SyncWorker con error recuperable. Se reintentará ===");
                return Result.retry();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error no controlado en SyncWorker", e);
            return Result.retry();
        }
    }
}