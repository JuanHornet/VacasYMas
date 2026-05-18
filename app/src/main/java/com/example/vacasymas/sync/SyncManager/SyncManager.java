package com.example.vacasymas.sync.SyncManager;

import android.content.Context;
import android.util.Log;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class SyncManager {

    private static final String TAG = "SyncManager";
    private static final String UNIQUE_WORK_NAME = "sync_animales";

    // =========================
    // SINCRONIZACIÓN AUTOMÁTICA
    // =========================
    public static void programarSyncAutomatica(Context context) {

        Log.d(TAG, "Programando sincronización automática");

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // solo con internet
                .build();

        PeriodicWorkRequest workRequest =
                new PeriodicWorkRequest.Builder(com.example.vacasymas.sync.SyncWorker.SyncWorker.class, 15, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .setBackoffCriteria(
                                BackoffPolicy.EXPONENTIAL,
                                10,
                                TimeUnit.SECONDS
                        )
                        .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                workRequest
        );
    }

    // =========================
    // SINCRONIZACIÓN MANUAL
    // =========================
    public static void lanzarSyncManual(Context context) {

        Log.d(TAG, "Lanzando sincronización manual");

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest workRequest =
                new OneTimeWorkRequest.Builder(com.example.vacasymas.sync.SyncWorker.SyncWorker.class)
                        .setConstraints(constraints)
                        .setBackoffCriteria(
                                BackoffPolicy.EXPONENTIAL,
                                10,
                                TimeUnit.SECONDS
                        )
                        .build();

        WorkManager.getInstance(context).enqueue(workRequest);
    }
}