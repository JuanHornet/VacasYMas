package com.example.vacasymas.base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FechaUtils {

    public static String ahoraIso() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    public static String formatearFecha(String fecha) {
        if (fecha == null || fecha.isEmpty()) return "-";

        try {
            String[] partes = fecha.split("-");
            return partes[2] + "/" + partes[1] + "/" + partes[0];
        } catch (Exception e) {
            return fecha;
        }
    }

    public static String hoy() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
    }

    public static String hoyIso() {
        return hoy();
    }
}