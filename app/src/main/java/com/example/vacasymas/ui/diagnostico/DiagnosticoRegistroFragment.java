package com.example.vacasymas.ui.diagnostico;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.vacasymas.R;
import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.Animal;

import java.util.ArrayList;

public class DiagnosticoRegistroFragment extends Fragment {

    private static final String ARG_ID_EXPLOTACION = "id_explotacion";

    private String idExplotacion;

    private EditText etCrotalDiagnostico, etObservacionesDiagnostico;
    private TextView tvVacaEncontrada, tvEstadoActual, tvDiagnosticadasHoy;
    private Spinner spinnerEstadoDiagnostico;
    private Button btnGuardarDiagnostico;

    private DBHelper dbHelper;
    private Animal vacaActual;

    private boolean buscandoAutomaticamente = false;

    private final String[] estadosMostrar = {
            "Selecciona diagnóstico",
            "Nada",
            "Vacía",
            "Cubierta",
            "Preñada"
    };

    private final String[] estadosGuardar = {
            "",
            "nada",
            "vacia",
            "cubierta",
            "preñada"
    };

    public static DiagnosticoRegistroFragment newInstance(String idExplotacion) {
        DiagnosticoRegistroFragment fragment = new DiagnosticoRegistroFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID_EXPLOTACION, idExplotacion);
        fragment.setArguments(args);
        return fragment;
    }

    public DiagnosticoRegistroFragment() {
        super(R.layout.fragment_diagnostico_registro);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            idExplotacion = getArguments().getString(ARG_ID_EXPLOTACION);
        }

        dbHelper = new DBHelper(requireContext());

        etCrotalDiagnostico = view.findViewById(R.id.etCrotalDiagnostico);
        etObservacionesDiagnostico = view.findViewById(R.id.etObservacionesDiagnostico);
        tvVacaEncontrada = view.findViewById(R.id.tvVacaEncontrada);
        tvEstadoActual = view.findViewById(R.id.tvEstadoActual);
        spinnerEstadoDiagnostico = view.findViewById(R.id.spinnerEstadoDiagnostico);
        btnGuardarDiagnostico = view.findViewById(R.id.btnGuardarDiagnostico);
        tvDiagnosticadasHoy = view.findViewById(R.id.tvDiagnosticadasHoy);


        configurarSpinner();
        actualizarDiagnosticadasHoy();
        configurarBusquedaAutomatica();

        btnGuardarDiagnostico.setOnClickListener(v -> guardarDiagnostico());
    }

    private void configurarSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                estadosMostrar
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstadoDiagnostico.setAdapter(adapter);
    }

    private void configurarBusquedaAutomatica() {
        etCrotalDiagnostico.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String texto = editable.toString().trim();

                vacaActual = null;
                tvVacaEncontrada.setText("Vaca: -");
                tvEstadoActual.setText("Estado actual: -");
                spinnerEstadoDiagnostico.setSelection(0);

                if (texto.length() == 4 && !buscandoAutomaticamente) {
                    buscarVacaPorUltimos4(texto);
                }
            }
        });
    }

    private void buscarVacaPorUltimos4(String ultimos4) {
        buscandoAutomaticamente = true;

        ArrayList<Animal> encontradas = dbHelper.buscarVacasPorUltimos4YExplotacion(
                ultimos4,
                idExplotacion
        );

        buscandoAutomaticamente = false;

        if (encontradas.isEmpty()) {
            vacaActual = null;
            tvVacaEncontrada.setText("Vaca: no encontrada");
            Toast.makeText(requireContext(), "No se encontró ninguna vaca", Toast.LENGTH_SHORT).show();
            return;
        }

        if (encontradas.size() == 1) {
            seleccionarVaca(encontradas.get(0));
            return;
        }

        mostrarDialogoSeleccionVaca(encontradas);
    }

    private void mostrarDialogoSeleccionVaca(ArrayList<Animal> vacas) {
        ArrayList<String> opciones = new ArrayList<>();

        for (Animal vaca : vacas) {
            opciones.add(
                    vaca.getCrotal() +
                            " | " +
                            valorSeguro(vaca.getEstadoReproductivoMostrar())
            );
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Selecciona la vaca")
                .setItems(opciones.toArray(new String[0]), (dialog, which) ->
                        seleccionarVaca(vacas.get(which))
                )
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    vacaActual = null;
                    tvVacaEncontrada.setText("Vaca: -");
                    tvEstadoActual.setText("Estado actual: -");
                    etCrotalDiagnostico.setText("");
                    etCrotalDiagnostico.requestFocus();
                })
                .show();
    }

    private void seleccionarVaca(Animal vaca) {
        vacaActual = vaca;

        tvVacaEncontrada.setText("Crotal: " + valorSeguro(vaca.getCrotal()));
        tvEstadoActual.setText("Estado actual: " + valorSeguro(vaca.getEstadoReproductivoMostrar()));

        seleccionarEstadoEnSpinner(vaca.getEstadoReproductivo());

        if (getActivity() instanceof DiagnosticoGestacionActivity) {
            ((DiagnosticoGestacionActivity) getActivity())
                    .actualizarVacaSeleccionadaParaRepro(
                            vaca.getId(),
                            vaca.getCrotal()
                    );
        }
    }

    private void guardarDiagnostico() {
        if (vacaActual == null) {
            Toast.makeText(requireContext(), "Primero selecciona una vaca", Toast.LENGTH_SHORT).show();
            etCrotalDiagnostico.requestFocus();
            return;
        }

        int pos = spinnerEstadoDiagnostico.getSelectedItemPosition();
        String resultado = estadosGuardar[pos];

        String fechaHoy = FechaUtils.hoy();

        String observaciones = etObservacionesDiagnostico.getText().toString().trim();

        boolean ok = dbHelper.insertarOActualizarDiagnosticoGestacionHoy(
                vacaActual.getId(),
                idExplotacion,
                fechaHoy,
                resultado,
                observaciones.isEmpty() ? null : observaciones
        );

        if (ok) {
            Toast.makeText(requireContext(), "Diagnóstico guardado", Toast.LENGTH_SHORT).show();

            actualizarDiagnosticadasHoy();

            vacaActual = null;
            etCrotalDiagnostico.setText("");
            etObservacionesDiagnostico.setText("");
            tvVacaEncontrada.setText("Vaca: -");
            tvEstadoActual.setText("Estado actual: -");
            spinnerEstadoDiagnostico.setSelection(0);
            etCrotalDiagnostico.requestFocus();

        } else {
            Toast.makeText(requireContext(), "Error al guardar diagnóstico", Toast.LENGTH_SHORT).show();
        }
    }

    private void seleccionarEstadoEnSpinner(String estado) {

        if (estado == null) {
            spinnerEstadoDiagnostico.setSelection(0);
            return;
        }

        switch (estado.toLowerCase()) {

            case "nada":
                spinnerEstadoDiagnostico.setSelection(1);
                break;

            case "vacia":
            case "vacía":
                spinnerEstadoDiagnostico.setSelection(2);
                break;

            case "cubierta":
                spinnerEstadoDiagnostico.setSelection(3);
                break;

            case "preñada":
            case "prenada":
                spinnerEstadoDiagnostico.setSelection(4);
                break;

            default:
                spinnerEstadoDiagnostico.setSelection(0);
                break;
        }
    }

    private void actualizarDiagnosticadasHoy() {
        int total = dbHelper.contarDiagnosticosGestacionHoy(
                idExplotacion,
                FechaUtils.hoy()
        );

        tvDiagnosticadasHoy.setText("Diagnosticadas hoy: " + total);
    }

    private String valorSeguro(String valor) {
        return valor == null || valor.trim().isEmpty() ? "-" : valor;
    }
}
