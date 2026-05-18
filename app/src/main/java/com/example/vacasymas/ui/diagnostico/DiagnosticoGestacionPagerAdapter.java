package com.example.vacasymas.ui.diagnostico;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.vacasymas.ui.animales.detalle.fragments.HistorialReproductivoFragment;

public class DiagnosticoGestacionPagerAdapter extends FragmentStateAdapter {

    private final String idExplotacion;
    private String idAnimalSeleccionado;
    private String crotalAnimalSeleccionado;

    public DiagnosticoGestacionPagerAdapter(@NonNull FragmentActivity fragmentActivity,
                                            String idExplotacion) {
        super(fragmentActivity);
        this.idExplotacion = idExplotacion;
    }

    public void setAnimalSeleccionado(String idAnimal, String crotalAnimal) {
        this.idAnimalSeleccionado = idAnimal;
        this.crotalAnimalSeleccionado = crotalAnimal;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return DiagnosticoRegistroFragment.newInstance(idExplotacion);
        } else if (position == 1) {
            if (idAnimalSeleccionado == null || crotalAnimalSeleccionado == null) {
                return DiagnosticoSinVacaFragment.newInstance();
            }

            return HistorialReproductivoFragment.newInstance(
                    idAnimalSeleccionado,
                    crotalAnimalSeleccionado
            );
        } else {
            return DiagnosticoListadoVacasFragment.newInstance(idExplotacion);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public String getTitulo(int position) {
        if (position == 0) return "Registrar";
        if (position == 1) return "Repro";
        return "Listado";
    }

    @Override
    public long getItemId(int position) {
        if (position == 1 && idAnimalSeleccionado != null) {
            return idAnimalSeleccionado.hashCode();
        }
        return position;
    }

    @Override
    public boolean containsItem(long itemId) {
        return true;
    }
}