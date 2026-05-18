package com.example.vacasymas.sync;

import android.content.Context;
import android.content.SharedPreferences;

public class SyncPrefs {

    private static final String PREFS_SYNC = "sync_prefs";
    private static final String KEY_LAST_SYNC_ANIMALES = "last_sync_animales";
    private static final String KEY_LAST_SYNC_EXPLOTACIONES = "last_sync_explotaciones";
    private static final String KEY_LAST_SYNC_DIAGNOSTICOS_GESTACION = "last_sync_diagnosticos_gestacion";

    public static String getLastSyncAnimales(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_SYNC, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LAST_SYNC_ANIMALES, null);
    }

    public static void setLastSyncAnimales(Context context, String fechaIso) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_SYNC, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LAST_SYNC_ANIMALES, fechaIso).apply();
    }

    public static void setLastSyncExplotaciones(Context context, String fechaIso) {
        context.getSharedPreferences(PREFS_SYNC, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_LAST_SYNC_EXPLOTACIONES, fechaIso)
                .apply();
    }

    public static String getLastSyncExplotaciones(Context context) {
        return context.getSharedPreferences(PREFS_SYNC, Context.MODE_PRIVATE)
                .getString(KEY_LAST_SYNC_EXPLOTACIONES, null);
    }

    public static String getLastSyncDiagnosticosGestacion(Context context) {
        return context.getSharedPreferences(PREFS_SYNC, Context.MODE_PRIVATE)
                .getString(KEY_LAST_SYNC_DIAGNOSTICOS_GESTACION, null);
    }

    public static void setLastSyncDiagnosticosGestacion(Context context, String fechaIso) {
        context.getSharedPreferences(PREFS_SYNC, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_LAST_SYNC_DIAGNOSTICOS_GESTACION, fechaIso)
                .apply();
    }

    private static final String KEY_LAST_SYNC_EVENTOS_REPRODUCTIVOS = "last_sync_eventos_reproductivos";

    public static String getLastSyncEventosReproductivos(Context context) {
        return context.getSharedPreferences(PREFS_SYNC, Context.MODE_PRIVATE)
                .getString(KEY_LAST_SYNC_EVENTOS_REPRODUCTIVOS, null);
    }

    public static void setLastSyncEventosReproductivos(Context context, String fecha) {
        context.getSharedPreferences(PREFS_SYNC, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_LAST_SYNC_EVENTOS_REPRODUCTIVOS, fecha)
                .apply();
    }
}
