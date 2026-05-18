package com.example.vacasymas.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "vacasymas_session";

    private static final String ID_USUARIO_LOCAL = "3289f6b7-25af-4c6f-b839-18736a2793ea";
    private static final String EMAIL_USUARIO_LOCAL = "usuario@local.com";

    private static final String KEY_ID_EXPLOTACION = "id_explotacion_seleccionada";
    private static final String KEY_NOMBRE_EXPLOTACION = "nombre_explotacion_seleccionada";

    public static String getIdUsuarioLocal() {
        return ID_USUARIO_LOCAL;
    }

    public static String getEmailUsuarioLocal() {
        return EMAIL_USUARIO_LOCAL;
    }

    public static void guardarExplotacionSeleccionada(Context context, String idExplotacion, String nombreExplotacion) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_ID_EXPLOTACION, idExplotacion)
                .putString(KEY_NOMBRE_EXPLOTACION, nombreExplotacion)
                .apply();
    }

    public static String getIdExplotacionSeleccionada(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ID_EXPLOTACION, null);
    }

    public static String getNombreExplotacionSeleccionada(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_NOMBRE_EXPLOTACION, null);
    }

    public static void limpiarExplotacionSeleccionada(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .remove(KEY_ID_EXPLOTACION)
                .remove(KEY_NOMBRE_EXPLOTACION)
                .apply();
    }
}