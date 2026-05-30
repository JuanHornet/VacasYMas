package com.example.vacasymas.sync;

import android.content.Context;
import android.util.Log;

public class SincronizadorGeneral {

    private static final String TAG = "SyncGeneral";

    private final Context context;

    public SincronizadorGeneral(Context context) {
        this.context = context.getApplicationContext();
    }

    public boolean sincronizarTodo() {
        Log.d(TAG, "=== Inicio sincronización general ===");

        boolean explotacionesOk = new SincronizadorExplotaciones(context).sincronizarTodo();

        boolean animalesOk = new SincronizadorAnimales(context).sincronizarTodo();

        boolean crotalesOk = new SincronizadorCrotalesDisponibles(context).sincronizarTodo();

        boolean listasOk = new SincronizadorListasAnimales(context).sincronizar();

        boolean detalleListasOk = new SincronizadorListaAnimalesDetalle(context).sincronizar();


        if (!animalesOk) {
            Log.e(TAG, "Se cancela sync de eventos porque animales no sincronizó correctamente");
            return false;
        }

        boolean diagnosticosGestacionOk =
                new SincronizadorDiagnosticosGestacion(context).sincronizarTodo();

        boolean notasAnimalesOk =
                new SincronizadorNotasAnimales(context).sincronizarTodo();

        boolean pesosOk =
                new SincronizadorPesosAnimales(context).sincronizarTodo();

        boolean eventosReproductivosOk =
                new SincronizadorEventosReproductivos(context).sincronizarTodo();

        boolean resultado =
                explotacionesOk
                        && crotalesOk
                        && animalesOk
                        && diagnosticosGestacionOk
                        && notasAnimalesOk
                        && pesosOk
                        && eventosReproductivosOk
                        && listasOk
                        && detalleListasOk;

        Log.d(TAG,
                "=== Fin sincronización general. " +
                        "Explotaciones OK: " + explotacionesOk +
                        " | Animales OK: " + animalesOk +
                        " | Diagnósticos gestación OK: " + diagnosticosGestacionOk +
                        " | Notas animales OK: " + notasAnimalesOk +
                        " | Pesos OK: " + pesosOk +
                        " | Eventos reproductivos OK: " + eventosReproductivosOk +
                        " | Crotales OK: " + crotalesOk +
                        " | Listas OK: " + listasOk +
                        " | Detalle listas OK: " + detalleListasOk +
                        " ===");

        return resultado;
    }
}