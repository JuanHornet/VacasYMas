package com.example.vacasymas.base;

import java.util.Calendar;

public class AnimalUtils {

    public static String calcularEdad(String fechaNacimiento) {
        if (fechaNacimiento == null || fechaNacimiento.isEmpty()) return "-";

        try {
            String[] partes = fechaNacimiento.split("-");
            int año = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);
            int dia = Integer.parseInt(partes[2]);

            Calendar nacimiento = Calendar.getInstance();
            nacimiento.set(año, mes - 1, dia);

            Calendar hoy = Calendar.getInstance();

            int años = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR);
            int meses = hoy.get(Calendar.MONTH) - nacimiento.get(Calendar.MONTH);

            if (meses < 0) {
                años--;
                meses += 12;
            }

            if (años <= 0) {
                return meses + " meses";
            } else {
                return años + " años";
            }

        } catch (Exception e) {
            return "-";
        }
    }
}