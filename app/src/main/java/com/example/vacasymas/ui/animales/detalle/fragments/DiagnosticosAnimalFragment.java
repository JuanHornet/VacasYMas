package com.example.vacasymas.ui.animales.detalle.fragments;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.base.TextoUtils;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.DiagnosticoGestacion;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class DiagnosticosAnimalFragment extends Fragment {

    private static final String ARG_ID_ANIMAL = "id_animal";

    private String idAnimal;
    private DBHelper dbHelper;
    private LinearLayout layoutContenido;
    private static final String ARG_ID_EXPLOTACION = "id_explotacion";
    private String idExplotacion;

    public static DiagnosticosAnimalFragment newInstance(String idAnimal, String idExplotacion) {
        DiagnosticosAnimalFragment fragment = new DiagnosticosAnimalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID_ANIMAL, idAnimal);
        args.putString(ARG_ID_EXPLOTACION, idExplotacion);
        fragment.setArguments(args);
        return fragment;
    }

    public DiagnosticosAnimalFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater,
                             @Nullable android.view.ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            idAnimal = getArguments().getString(ARG_ID_ANIMAL);
            idExplotacion = getArguments().getString(ARG_ID_EXPLOTACION);
        }

        dbHelper = new DBHelper(requireContext());

        android.widget.ScrollView scrollView = new android.widget.ScrollView(requireContext());
        scrollView.setFillViewport(false);

        scrollView.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        layoutContenido = new LinearLayout(requireContext());
        layoutContenido.setOrientation(LinearLayout.VERTICAL);
        layoutContenido.setPadding(dp(16), dp(12), dp(16), dp(24));

        scrollView.addView(layoutContenido, new android.widget.ScrollView.LayoutParams(
                android.widget.ScrollView.LayoutParams.MATCH_PARENT,
                android.widget.ScrollView.LayoutParams.WRAP_CONTENT
        ));

        cargarDiagnosticos();

        return scrollView;
    }

    private void cargarDiagnosticos() {
        layoutContenido.removeAllViews();

        ArrayList<DiagnosticoGestacion> lista =
                dbHelper.obtenerDiagnosticosGestacionPorAnimal(idAnimal);

        TextView btnAnadir = crearBoton("+ Añadir diagnóstico");
        btnAnadir.setOnClickListener(v -> mostrarDialogoAnadirDiagnostico());
        layoutContenido.addView(btnAnadir);

        if (lista == null || lista.isEmpty()) {
            TextView mensaje = crearTextoSecundario("No hay diagnósticos registrados.");
            mensaje.setPadding(0, dp(12), 0, 0);
            layoutContenido.addView(mensaje);
            return;
        }

        TextView titulo = crearTitulo("Diagnósticos: " + lista.size());
        titulo.setPadding(0, dp(12), 0, 0);
        layoutContenido.addView(titulo);

        for (DiagnosticoGestacion d : lista) {
            layoutContenido.addView(crearCardDiagnostico(d));
        }
    }

    private MaterialCardView crearCardDiagnostico(DiagnosticoGestacion d) {
        MaterialCardView card = new MaterialCardView(requireContext());

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, dp(10), 0, 0);
        card.setLayoutParams(cardParams);

        card.setRadius(dp(14));
        card.setCardElevation(dp(2));
        card.setStrokeWidth(dp(1));
        card.setStrokeColor(0xFFE0E0E0);
        card.setUseCompatPadding(true);

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(12), dp(10), dp(12), dp(10));

        TextView tvLinea1 = crearTextoPrincipal(
                obtenerIconoResultado(d.getResultado()) +
                        " " +
                        TextoUtils.capitalizar(valorSeguro(d.getResultado())) +
                        " · " +
                        FechaUtils.formatearFecha(d.getFecha())
        );

        root.addView(tvLinea1);

        if (d.getObservaciones() != null && !d.getObservaciones().trim().isEmpty()) {
            TextView tvObs = crearTextoSecundario(d.getObservaciones());
            root.addView(tvObs);
        }

        card.setOnLongClickListener(v -> {
            confirmarEliminarDiagnostico(d);
            return true;
        });

        card.addView(root);
        return card;
    }

    private String obtenerIconoResultado(String resultado) {
        if (resultado == null) return "⚪";

        switch (resultado.toLowerCase()) {
            case "preñada":
            case "prenada":
                return "🟢";
            case "vacía":
            case "vacia":
                return "🔴";
            case "cubierta":
                return "🟡";
            case "nada":
                return "⚪";
            default:
                return "⚪";
        }
    }

    private void mostrarMensaje(String mensaje) {
        layoutContenido.removeAllViews();
        layoutContenido.addView(crearTextoSecundario(mensaje));
    }

    private TextView crearTitulo(String texto) {
        TextView tv = new TextView(requireContext());
        tv.setText(texto);
        tv.setTextSize(15);
        tv.setTextColor(0xFF000000);
        tv.setTypeface(null, Typeface.BOLD);
        return tv;
    }

    private TextView crearTextoPrincipal(String texto) {
        TextView tv = new TextView(requireContext());
        tv.setText(texto);
        tv.setTextSize(14);
        tv.setTextColor(0xFF000000);
        tv.setTypeface(null, Typeface.BOLD);
        return tv;
    }

    private TextView crearTextoSecundario(String texto) {
        TextView tv = new TextView(requireContext());
        tv.setText(texto);
        tv.setTextSize(13);
        tv.setTextColor(0xFF666666);
        tv.setPadding(0, dp(4), 0, 0);
        return tv;
    }

    private String valorSeguro(String valor) {
        return valor == null || valor.trim().isEmpty() ? "-" : valor;
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
    }

    private void mostrarDialogoAnadirDiagnostico() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(8), dp(20), 0);

        Spinner spinnerResultado = new Spinner(requireContext());

        String[] estadosMostrar = {"Nada", "Vacía", "Cubierta", "Preñada"};
        String[] estadosGuardar = {"nada", "vacia", "cubierta", "preñada"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                estadosMostrar
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerResultado.setAdapter(adapter);

        EditText etObservaciones = new EditText(requireContext());
        etObservaciones.setHint("Observaciones");
        etObservaciones.setMinLines(2);

        layout.addView(spinnerResultado);
        layout.addView(etObservaciones);

        new AlertDialog.Builder(requireContext())
                .setTitle("Añadir diagnóstico")
                .setView(layout)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    int pos = spinnerResultado.getSelectedItemPosition();
                    String resultado = estadosGuardar[pos];
                    String observaciones = etObservaciones.getText().toString().trim();

                    guardarDiagnostico(resultado, observaciones);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void guardarDiagnostico(String resultado, String observaciones) {
        if (idAnimal == null || idAnimal.trim().isEmpty()) {
            Toast.makeText(requireContext(), "No se ha recibido el animal", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idExplotacion == null || idExplotacion.trim().isEmpty()) {
            Toast.makeText(requireContext(), "No se ha recibido la explotación", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean ok = dbHelper.insertarOActualizarDiagnosticoGestacionHoy(
                idAnimal,
                idExplotacion,
                FechaUtils.hoy(),
                resultado,
                observaciones.isEmpty() ? null : observaciones
        );

        if (ok) {
            Toast.makeText(requireContext(), "Diagnóstico guardado", Toast.LENGTH_SHORT).show();
            cargarDiagnosticos();
        } else {
            Toast.makeText(requireContext(), "Error al guardar diagnóstico", Toast.LENGTH_SHORT).show();
        }
    }

    private TextView crearBoton(String texto) {
        TextView tv = new TextView(requireContext());
        tv.setText(texto);
        tv.setTextSize(14);
        tv.setTextColor(0xFF6750A4);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setPadding(dp(12), dp(10), dp(12), dp(10));
        tv.setGravity(android.view.Gravity.CENTER);
        return tv;
    }

    private void confirmarEliminarDiagnostico(DiagnosticoGestacion diagnostico) {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Eliminar diagnóstico")
                .setMessage("¿Quieres eliminar este diagnóstico?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    boolean ok = dbHelper.eliminarDiagnosticoGestacionLogico(diagnostico.getId());

                    if (ok) {
                        android.widget.Toast.makeText(
                                requireContext(),
                                "Diagnóstico eliminado",
                                android.widget.Toast.LENGTH_SHORT
                        ).show();

                        cargarDiagnosticos();
                    } else {
                        android.widget.Toast.makeText(
                                requireContext(),
                                "Error al eliminar diagnóstico",
                                android.widget.Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}