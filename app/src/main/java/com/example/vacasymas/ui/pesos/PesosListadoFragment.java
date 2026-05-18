package com.example.vacasymas.ui.pesos;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.PesoAnimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PesosListadoFragment extends Fragment {

    private static final String ARG_ID_EXPLOTACION = "id_explotacion";

    private String idExplotacion;
    private DBHelper dbHelper;

    private TextView tvTotalPesados;
    private TextView tvMachosPesados;
    private TextView tvHembrasPesadas;

    private TextView tvMediaTotal;
    private TextView tvMediaMachos;
    private TextView tvMediaHembras;

    private TextView tvTotal200;
    private TextView tvMachos200;
    private TextView tvHembras200;
    private TextView tvResumenSeleccion;
    private RecyclerView rvMachos, rvHembras;

    private PesoListadoAdapter adapterMachos;
    private PesoListadoAdapter adapterHembras;

    private Spinner spFechasPesaje;
    private String fechaSeleccionada;
    private boolean cargandoSpinner = false;

    public static PesosListadoFragment newInstance(String idExplotacion) {
        PesosListadoFragment fragment = new PesosListadoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID_EXPLOTACION, idExplotacion);
        fragment.setArguments(args);
        return fragment;
    }

    public PesosListadoFragment() {
        super(R.layout.fragment_pesos_listado);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            idExplotacion = getArguments().getString(ARG_ID_EXPLOTACION);
        }

        dbHelper = new DBHelper(requireContext());

        tvTotalPesados = view.findViewById(R.id.tvTotalPesados);
        tvMachosPesados = view.findViewById(R.id.tvMachosPesados);
        tvHembrasPesadas = view.findViewById(R.id.tvHembrasPesadas);

        tvMediaTotal = view.findViewById(R.id.tvMediaTotal);
        tvMediaMachos = view.findViewById(R.id.tvMediaMachos);
        tvMediaHembras = view.findViewById(R.id.tvMediaHembras);

        tvTotal200 = view.findViewById(R.id.tvTotal200);
        tvMachos200 = view.findViewById(R.id.tvMachos200);
        tvHembras200 = view.findViewById(R.id.tvHembras200);
        tvResumenSeleccion = view.findViewById(R.id.tvResumenSeleccion);
        rvMachos = view.findViewById(R.id.rvMachos);
        rvHembras = view.findViewById(R.id.rvHembras);

        spFechasPesaje = view.findViewById(R.id.spFechasPesaje);

        configurarAdapters();
        cargarFechasPesaje();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarFechasPesaje();
    }

    private void configurarAdapters() {
        adapterMachos = new PesoListadoAdapter(new PesoListadoAdapter.PesoListener() {
            @Override
            public void onEliminar(PesoAnimal peso) {
                confirmarEliminar(peso);
            }

            @Override
            public void onSeleccionCambiada() {
                actualizarResumenSeleccion();
            }
        });

        adapterHembras = new PesoListadoAdapter(new PesoListadoAdapter.PesoListener() {
            @Override
            public void onEliminar(PesoAnimal peso) {
                confirmarEliminar(peso);
            }

            @Override
            public void onSeleccionCambiada() {
                actualizarResumenSeleccion();
            }
        });

        rvMachos.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHembras.setLayoutManager(new LinearLayoutManager(requireContext()));

        rvMachos.setAdapter(adapterMachos);
        rvHembras.setAdapter(adapterHembras);
    }

    private void cargarListado() {
        if (idExplotacion == null || idExplotacion.trim().isEmpty()) {
            return;
        }

        List<PesoAnimal> todos = dbHelper.obtenerPesosPorExplotacionYFecha(
                idExplotacion,
                fechaSeleccionada
        );

        List<PesoAnimal> machos = new ArrayList<>();
        List<PesoAnimal> hembras = new ArrayList<>();

        for (PesoAnimal p : todos) {
            String sexo = p.getSexo() != null ? p.getSexo().trim().toLowerCase() : "";

            if (sexo.equals("macho") || sexo.equals("m")) {
                machos.add(p);
            } else if (sexo.equals("hembra") || sexo.equals("h")) {
                hembras.add(p);
            }
        }

        adapterMachos.actualizarLista(machos);
        adapterHembras.actualizarLista(hembras);


        actualizarResumenSexo(machos, hembras);
        actualizarResumenSeleccion();
    }

    private void actualizarResumenSexo(List<PesoAnimal> machos,
                                       List<PesoAnimal> hembras) {

        int total = machos.size() + hembras.size();

        int sumaMachos = 0;
        int sumaHembras = 0;

        int mayores200Machos = 0;
        int mayores200Hembras = 0;

        for (PesoAnimal p : machos) {

            if (p.getPeso() != null) {
                sumaMachos += p.getPeso();

                if (p.getPeso() >= 200) {
                    mayores200Machos++;
                }
            }
        }

        for (PesoAnimal p : hembras) {

            if (p.getPeso() != null) {
                sumaHembras += p.getPeso();

                if (p.getPeso() >= 200) {
                    mayores200Hembras++;
                }
            }
        }

        int sumaTotal = sumaMachos + sumaHembras;

        double mediaTotal = total > 0
                ? (double) sumaTotal / total
                : 0;

        double mediaMachos = machos.size() > 0
                ? (double) sumaMachos / machos.size()
                : 0;

        double mediaHembras = hembras.size() > 0
                ? (double) sumaHembras / hembras.size()
                : 0;

        tvTotalPesados.setText("Pesados: " + total);
        tvMachosPesados.setText("♂ " + machos.size());
        tvHembrasPesadas.setText("♀ " + hembras.size());

        tvMediaTotal.setText(String.format(
                Locale.getDefault(),
                "Media: %.0f kg",
                mediaTotal
        ));

        tvMediaMachos.setText(String.format(
                Locale.getDefault(),
                "♂ %.0f",
                mediaMachos
        ));

        tvMediaHembras.setText(String.format(
                Locale.getDefault(),
                "♀ %.0f",
                mediaHembras
        ));

        tvTotal200.setText(
                "≥200 kg: " + (mayores200Machos + mayores200Hembras)
        );

        tvMachos200.setText("♂ " + mayores200Machos);
        tvHembras200.setText("♀ " + mayores200Hembras);
    }

    private int contarMasDe200(List<PesoAnimal> lista) {
        int total = 0;

        for (PesoAnimal p : lista) {
            if (p.getPeso() != null && p.getPeso() > 200) {
                total++;
            }
        }

        return total;
    }

    private void actualizarResumenSeleccion() {
        List<PesoAnimal> seleccionados = new ArrayList<>();

        if (adapterMachos != null) {
            seleccionados.addAll(adapterMachos.obtenerSeleccionados());
        }

        if (adapterHembras != null) {
            seleccionados.addAll(adapterHembras.obtenerSeleccionados());
        }

        if (seleccionados.isEmpty()) {
            tvResumenSeleccion.setText("Seleccionados: 0 | Media: - kg");
            return;
        }

        int suma = 0;

        for (PesoAnimal p : seleccionados) {
            if (p.getPeso() != null) {
                suma += p.getPeso();
            }
        }

        double media = (double) suma / seleccionados.size();

        tvResumenSeleccion.setText(String.format(
                Locale.getDefault(),
                "Seleccionados: %d | Media: %.1f kg",
                seleccionados.size(),
                media
        ));
    }

    private void confirmarEliminar(PesoAnimal peso) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar peso")
                .setMessage("¿Eliminar el peso de " + peso.getCrotal() + "?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarPeso(peso))
                .show();
    }

    private void eliminarPeso(PesoAnimal peso) {
        boolean ok = dbHelper.eliminarPesoAnimal(peso.getId());

        if (ok) {
            Toast.makeText(requireContext(), "Peso eliminado", Toast.LENGTH_SHORT).show();
            cargarListado();
        } else {
            Toast.makeText(requireContext(), "Error al eliminar peso", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarFechasPesaje() {
        if (idExplotacion == null || idExplotacion.trim().isEmpty()) {
            return;
        }

        List<String> fechasIso = dbHelper.obtenerFechasPesajesPorExplotacion(idExplotacion);

        if (fechasIso.isEmpty()) {
            fechaSeleccionada = FechaUtils.hoy();

            adapterMachos.actualizarLista(new ArrayList<>());
            adapterHembras.actualizarLista(new ArrayList<>());

            tvTotalPesados.setText("Pesados: 0");
            tvMachosPesados.setText("♂ 0");
            tvHembrasPesadas.setText("♀ 0");

            tvMediaTotal.setText("Media: - kg");
            tvMediaMachos.setText("♂ -");
            tvMediaHembras.setText("♀ -");

            tvTotal200.setText("≥200 kg: 0");
            tvMachos200.setText("♂ 0");
            tvHembras200.setText("♀ 0");

            tvResumenSeleccion.setText("Seleccionados: 0 | Media: - kg");
            return;
        }

        List<String> fechasVisibles = new ArrayList<>();

        for (String fecha : fechasIso) {
            fechasVisibles.add(FechaUtils.formatearFecha(fecha));
        }

        cargandoSpinner = true;

        ArrayAdapter<String> adapterFechas = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                fechasVisibles
        );

        adapterFechas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFechasPesaje.setAdapter(adapterFechas);

        int posicionInicial = 0;
        String hoy = FechaUtils.hoy();

        for (int i = 0; i < fechasIso.size(); i++) {
            if (fechasIso.get(i).equals(hoy)) {
                posicionInicial = i;
                break;
            }
        }

        spFechasPesaje.setSelection(posicionInicial);
        fechaSeleccionada = fechasIso.get(posicionInicial);

        cargandoSpinner = false;

        spFechasPesaje.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (cargandoSpinner) return;

                fechaSeleccionada = fechasIso.get(position);
                cargarListado();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        cargarListado();
    }
}