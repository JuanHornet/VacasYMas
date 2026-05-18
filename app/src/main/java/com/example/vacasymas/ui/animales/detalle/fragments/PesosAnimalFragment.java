package com.example.vacasymas.ui.animales.detalle.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.example.vacasymas.ui.animales.detalle.adapters.PesoAnimalAdapter;

import java.util.List;
import java.util.UUID;



public class PesosAnimalFragment extends Fragment {

    private static final String ARG_ID_ANIMAL = "id_animal";
    private static final String ARG_CROTAL = "crotal";
    private static final String ARG_ID_EXPLOTACION = "id_explotacion";

    private String idAnimal;
    private String crotal;
    private String idExplotacion;

    private DBHelper dbHelper;

    private TextView btnAddPeso;
    private RecyclerView rvPesos;

    private PesoAnimalAdapter adapter;

    public static PesosAnimalFragment newInstance(String idAnimal, String crotal, String idExplotacion) {
        PesosAnimalFragment fragment = new PesosAnimalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID_ANIMAL, idAnimal);
        args.putString(ARG_CROTAL, crotal);
        args.putString(ARG_ID_EXPLOTACION, idExplotacion);
        fragment.setArguments(args);
        return fragment;
    }

    public PesosAnimalFragment() {
        super(R.layout.fragment_pesos_animal);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            idAnimal = getArguments().getString(ARG_ID_ANIMAL);
            crotal = getArguments().getString(ARG_CROTAL);
            idExplotacion = getArguments().getString(ARG_ID_EXPLOTACION);
        }

        dbHelper = new DBHelper(requireContext());

        btnAddPeso = view.findViewById(R.id.btnAddPeso);
        rvPesos = view.findViewById(R.id.rvPesos);

        adapter = new PesoAnimalAdapter();

        adapter.setOnPesoLongClickListener(this::mostrarDialogoEliminarPeso);

        rvPesos.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPesos.setAdapter(adapter);

        btnAddPeso.setOnClickListener(v -> mostrarDialogoNuevoPeso());

        cargarPesos();
    }

    private void cargarPesos() {
        List<PesoAnimal> pesos = dbHelper.obtenerPesosPorAnimal(idAnimal);

        adapter.actualizarLista(pesos);
    }


    private void mostrarDialogoNuevoPeso() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_nuevo_peso, null);

        EditText etPeso = dialogView.findViewById(R.id.etPeso);
        EditText etObservaciones = dialogView.findViewById(R.id.etObservaciones);

        final String[] fechaIso = {FechaUtils.hoy()};

        new AlertDialog.Builder(requireContext())
                .setTitle("Nuevo peso")
                .setView(dialogView)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String pesoStr = etPeso.getText().toString().trim();

                    if (pesoStr.isEmpty()) {
                        Toast.makeText(requireContext(), "Introduce un peso", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int peso = Integer.parseInt(pesoStr);
                    String observaciones = etObservaciones.getText().toString().trim();

                    guardarPeso(peso, fechaIso[0], observaciones);
                })
                .show();
    }

    private void guardarPeso(int peso, String fecha, String observaciones) {
        {
            PesoAnimal p = new PesoAnimal();

            p.setId(UUID.randomUUID().toString());
            p.setIdAnimal(idAnimal);
            p.setIdExplotacionUuid(idExplotacion);
            p.setCrotal(crotal);
            p.setFecha(fecha);
            p.setPeso(peso);
            p.setObservaciones(observaciones);
            p.setSincronizado(0);
            p.setEliminado(0);
            p.setFechaActualizacion(FechaUtils.ahoraIso());
            p.setFechaEliminado(null);

            boolean ok = dbHelper.insertarPesoAnimal(p);

            if (ok) {
                Toast.makeText(requireContext(), "Peso guardado", Toast.LENGTH_SHORT).show();
                cargarPesos();
            } else {
                Toast.makeText(requireContext(), "Error al guardar peso", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void mostrarDialogoEliminarPeso(PesoAnimal peso) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar peso")
                .setMessage("¿Quieres eliminar el peso de "
                        + peso.getPeso()
                        + " kg del día "
                        + FechaUtils.formatearFecha(peso.getFecha())
                        + "?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    eliminarPeso(peso);
                })
                .show();
    }

    private void eliminarPeso(PesoAnimal peso) {
        boolean ok = dbHelper.eliminarPesoAnimal(peso.getId());

        if (ok) {
            Toast.makeText(requireContext(), "Peso eliminado", Toast.LENGTH_SHORT).show();
            cargarPesos();
        } else {
            Toast.makeText(requireContext(), "Error al eliminar peso", Toast.LENGTH_SHORT).show();
        }
    }
}