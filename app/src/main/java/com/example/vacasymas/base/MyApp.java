package com.example.vacasymas.base;

import android.app.Application;
import android.util.Log;

import com.example.vacasymas.sync.SyncManager.*;


public class MyApp extends Application {

    private static final String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Aplicación iniciada. Programando sync automática");

        SyncManager.programarSyncAutomatica(this);
    }
}