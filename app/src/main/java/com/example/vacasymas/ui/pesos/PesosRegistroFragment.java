package com.example.vacasymas.ui.pesos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.vacasymas.data.models.PesoAnimal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PesosRegistroFragment extends Fragment {

    private static final String ARG_ID_EXPLOTACION = "id_explotacion";

    private String idExplotacion;

    private EditText etCrotal, etPeso;
    private TextView tvAnimalEncontrado;
    private Button btnGuardarPeso;

    private DBHelper dbHelper;
    private Animal animalActual;
    private TextView tvPesadosHoy;


    private boolean buscandoAutomaticamente = false;

    public static PesosRegistroFragment newInstance(String idExplotacion) {
        PesosRegistroFragment fragment = new PesosRegistroFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID_EXPLOTACION, idExplotacion);
        fragment.setArguments(args);
        return fragment;
    }

    public PesosRegistroFragment() {
        super(R.layout.fragment_pesos_registro);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            idExplotacion = getArguments().getString(ARG_ID_EXPLOTACION);
        }

        dbHelper = new DBHelper(requireContext());

        etCrotal = view.findViewById(R.id.etCrotal);
        etPeso = view.findViewById(R.id.etPeso);
        tvAnimalEncontrado = view.findViewById(R.id.tvAnimalEncontrado);
        btnGuardarPeso = view.findViewById(R.id.btnGuardarPeso);
        tvPesadosHoy = view.findViewById(R.id.tvPesadosHoy);


        actualizarPesadosHoy();

        configurarBusquedaAutomatica();

        btnGuardarPeso.setOnClickListener(v -> guardarPesoAnimalSeleccionado());
    }

    private void configurarBusquedaAutomatica() {
        etCrotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String texto = editable.toString().trim();

                animalActual = null;
                tvAnimalEncontrado.setText("Animal: -");

                if (texto.length() == 4 && !buscandoAutomaticamente) {
                    buscarAnimalPorUltimos4(texto);
                }
            }
        });
    }

    private void buscarAnimalPorUltimos4(String ultimos4) {
        buscandoAutomaticamente = true;

        List<Animal> encontrados = dbHelper.buscarAnimalesPorUltimos4Crotal(
                ultimos4,
                idExplotacion
        );

        buscandoAutomaticamente = false;

        if (encontrados.isEmpty()) {
            animalActual = null;
            tvAnimalEncontrado.setText("Animal: no encontrado");
            Toast.makeText(requireContext(), "No se encontró ningún animal", Toast.LENGTH_SHORT).show();
            return;
        }

        if (encontrados.size() == 1) {
            seleccionarAnimal(encontrados.get(0));
            return;
        }

        mostrarDialogoSeleccionAnimal(encontrados);
    }

    private void mostrarDialogoSeleccionAnimal(List<Animal> animales) {
        List<String> opciones = new ArrayList<>();

        for (Animal a : animales) {
            opciones.add(
                    a.getCrotal() + " | " +
                            valorSeguro(a.getSexo()) + " | " +
                            valorSeguro(a.getEstatusDescripcion())
            );
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Selecciona el animal")
                .setItems(opciones.toArray(new String[0]), (dialog, which) ->
                        seleccionarAnimal(animales.get(which))
                )
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    animalActual = null;
                    tvAnimalEncontrado.setText("Animal: -");
                    etCrotal.setText("");
                    etCrotal.requestFocus();
                })
                .show();
    }

    private void seleccionarAnimal(Animal animal) {
        animalActual = animal;

        tvAnimalEncontrado.setText(
                "Crotal: " + animal.getCrotal() + " | " +
                        valorSeguro(animal.getSexo())
        );

        etPeso.requestFocus();
    }

    private void guardarPesoAnimalSeleccionado() {
        if (animalActual == null) {
            Toast.makeText(requireContext(), "Primero selecciona un animal", Toast.LENGTH_SHORT).show();
            etCrotal.requestFocus();
            return;
        }

        String pesoStr = etPeso.getText().toString().trim();

        if (pesoStr.isEmpty()) {
            Toast.makeText(requireContext(), "Introduce el peso", Toast.LENGTH_SHORT).show();
            etPeso.requestFocus();
            return;
        }

        int peso;

        try {
            peso = Integer.parseInt(pesoStr);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Peso no válido", Toast.LENGTH_SHORT).show();
            etPeso.requestFocus();
            return;
        }

        String fechaHoy = FechaUtils.hoy();

        // 🔴 AQUÍ LA CLAVE
        if (dbHelper.existePesoAnimalEnFecha(animalActual.getId(), fechaHoy)) {

            new AlertDialog.Builder(requireContext())
                    .setTitle("Peso ya registrado")
                    .setMessage("Este animal ya tiene un peso hoy.\n\n¿Quieres actualizarlo?")
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Actualizar", (dialog, which) -> {
                        actualizarPeso(animalActual, peso, fechaHoy);
                    })
                    .show();

            return;
        }

        // ✔ Si no existe → guardar normal
        guardarPeso(animalActual, peso);
    }

    private void guardarPeso(Animal animal, int peso) {
        PesoAnimal p = new PesoAnimal();

        p.setId(UUID.randomUUID().toString());
        p.setIdAnimal(animal.getId());
        p.setIdExplotacionUuid(animal.getIdExplotacionUuid());
        p.setCrotal(animal.getCrotal());
        p.setSexo(animal.getSexo());
        p.setFecha(FechaUtils.hoy());
        p.setPeso(peso);
        p.setObservaciones("Pesaje en báscula");
        p.setSincronizado(0);
        p.setEliminado(0);
        p.setFechaActualizacion(FechaUtils.ahoraIso());
        p.setFechaEliminado(null);

        boolean ok = dbHelper.insertarPesoAnimal(p);

        if (ok) {

            actualizarPesadosHoy();
            animalActual = null;
            etCrotal.setText("");
            etPeso.setText("");
            tvAnimalEncontrado.setText("Animal: -");
            etCrotal.requestFocus();

            Toast.makeText(requireContext(), "Peso guardado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Error al guardar peso", Toast.LENGTH_SHORT).show();
        }
    }


    private String valorSeguro(String valor) {
        return valor == null || valor.trim().isEmpty() ? "-" : valor;
    }

    private void actualizarPeso(Animal animal, int peso, String fecha) {

        boolean ok = dbHelper.actualizarPesoAnimal(
                animal.getId(),
                fecha,
                peso,
                FechaUtils.ahoraIso()
        );

        if (ok) {
            Toast.makeText(requireContext(), "Peso actualizado", Toast.LENGTH_SHORT).show();

            etCrotal.setText("");
            etPeso.setText("");
            tvAnimalEncontrado.setText("Animal: -");
            etCrotal.requestFocus();

        } else {
            Toast.makeText(requireContext(), "Error al actualizar peso", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarPesadosHoy() {

        int total = dbHelper.obtenerPesosPorExplotacionYFecha(
                idExplotacion,
                FechaUtils.hoy()
        ).size();

        tvPesadosHoy.setText("Pesados hoy: " + total);
    }
}