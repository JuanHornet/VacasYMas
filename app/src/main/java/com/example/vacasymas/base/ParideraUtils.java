package com.example.vacasymas.base;

import java.util.Calendar;

public class ParideraUtils {

    public static String obtenerParideraActual() {
        Calendar calendar = Calendar.getInstance();

        int anio = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH) + 1;

        if (mes >= 8) {
            return anio + "/" + (anio + 1);
        } else {
            return (anio - 1) + "/" + anio;
        }
    }

    public static String calcularParidera(String fecha) {

        if (fecha == null || fecha.trim().isEmpty()) {
            return null;
        }

        try {
            String[] partes = fecha.split("-");

            int anio = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);

            if (mes >= 8) {
                return anio + "/" + (anio + 1);
            } else {
                return (anio - 1) + "/" + anio;
            }

        } catch (Exception e) {
            return null;
        }
    }
}