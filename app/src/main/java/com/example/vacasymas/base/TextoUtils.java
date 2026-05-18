package com.example.vacasymas.base;

public class TextoUtils {

    public static String capitalizar(String texto) {
        if (texto == null || texto.trim().isEmpty() || texto.equals("-")) {
            return "-";
        }

        texto = texto.trim();
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }
}
