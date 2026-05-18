package com.example.vacasymas.ui.animales.detalle.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.vacasymas.ui.animales.detalle.fragments.DiagnosticosAnimalFragment;
import com.example.vacasymas.ui.animales.detalle.fragments.HistorialReproductivoFragment;
import com.example.vacasymas.ui.animales.detalle.fragments.NotasAnimalFragment;
import com.example.vacasymas.ui.animales.detalle.fragments.PesosAnimalFragment;

import java.util.ArrayList;
import java.util.List;

public class DetalleAnimalPagerAdapter extends FragmentStateAdapter {

    public static class TabDetalle {
        public String titulo;
        public String tipo;

        public TabDetalle(String titulo, String tipo) {
            this.titulo = titulo;
            this.tipo = tipo;
        }
    }

    private final String idAnimal;
    private final String crotalAnimal;
    private final List<TabDetalle> tabs = new ArrayList<>();
    private final String statecode;
    private final String idExplotacion;

    public DetalleAnimalPagerAdapter(@NonNull FragmentActivity fragmentActivity,
                                     String idAnimal,
                                     String crotalAnimal,
                                     String idExplotacion,
                                     Integer estatus,
                                     String statecode) {
        super(fragmentActivity);
        this.idAnimal = idAnimal;
        this.crotalAnimal = crotalAnimal;
        this.idExplotacion = idExplotacion;
        this.statecode = statecode;
        configurarTabs(estatus, statecode);
    }

    private void configurarTabs(Integer estatus, String statecode) {
        tabs.clear();

        if ("0".equals(statecode)) {
            tabs.add(new TabDetalle("Repro", "reproductivo"));
            tabs.add(new TabDetalle("Diagnósticos", "diagnosticos"));
            tabs.add(new TabDetalle("Notas", "notas"));
            return;
        }

        if (estatus == null) {
            tabs.add(new TabDetalle("Notas", "notas"));
            return;
        }

        switch (estatus) {
            case 10003:
                tabs.add(new TabDetalle("Repro", "reproductivo"));
                tabs.add(new TabDetalle("Diagnósticos", "diagnosticos"));
                tabs.add(new TabDetalle("Notas", "notas"));
                break;

            case 10001:
            case 10002:
                tabs.add(new TabDetalle("Pesos", "pesos"));
                tabs.add(new TabDetalle("Notas", "notas"));
                break;

            case 10004:
            case 10005:
            default:
                tabs.add(new TabDetalle("Notas", "notas"));
                break;
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String tipo = tabs.get(position).tipo;

        switch (tipo) {
            case "reproductivo":
                return HistorialReproductivoFragment.newInstance(idAnimal, crotalAnimal);

            case "diagnosticos":
                return DiagnosticosAnimalFragment.newInstance(idAnimal, idExplotacion);

            case "pesos":
                return PesosAnimalFragment.newInstance(idAnimal, crotalAnimal, idExplotacion);

            case "notas":
            default:
                return NotasAnimalFragment.newInstance(idAnimal);
        }
    }

    @Override
    public int getItemCount() {
        return tabs.size();
    }

    public String getTitulo(int position) {
        return tabs.get(position).titulo;
    }
}
