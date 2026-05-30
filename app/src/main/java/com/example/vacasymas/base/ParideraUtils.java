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

    public static String obtenerParideraAnterior() {
        String actual = obtenerParideraActual();

        try {
            String[] partes = actual.split("/");
            int inicio = Integer.parseInt(partes[0]);
            int fin = Integer.parseInt(partes[1]);

            return (inicio - 1) + "/" + (fin - 1);

        } catch (Exception e) {
            return null;
        }
    }

    public static String obtenerFechaInicioParidera(String paridera) {
        if (paridera == null || paridera.trim().isEmpty()) return null;

        try {
            String[] partes = paridera.split("/");
            return partes[0] + "-08-01";
        } catch (Exception e) {
            return null;
        }
    }

    public static String obtenerFechaFinParidera(String paridera) {
        if (paridera == null || paridera.trim().isEmpty()) return null;

        try {
            String[] partes = paridera.split("/");
            return partes[1] + "-07-31";
        } catch (Exception e) {
            return null;
        }
    }

    public static String obtenerFechaHaceDosAnos() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -2);

        int anio = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH) + 1;
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        return String.format("%04d-%02d-%02d", anio, mes, dia);
    }

    public static java.util.List<String> obtenerUltimasCincoParideras() {

        java.util.List<String> parideras = new java.util.ArrayList<>();

        String actual = obtenerParideraActual();

        try {
            String[] partes = actual.split("/");

            int inicio = Integer.parseInt(partes[0]);
            int fin = Integer.parseInt(partes[1]);

            for (int i = 0; i < 5; i++) {
                parideras.add((inicio - i) + "/" + (fin - i));
            }

        } catch (Exception e) {
            parideras.add(actual);
        }

        return parideras;
    }
}