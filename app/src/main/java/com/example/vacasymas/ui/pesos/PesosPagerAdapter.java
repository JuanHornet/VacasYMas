package com.example.vacasymas.ui.pesos;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PesosPagerAdapter extends FragmentStateAdapter {

    private final String idExplotacion;

    public PesosPagerAdapter(@NonNull FragmentActivity fragmentActivity, String idExplotacion) {
        super(fragmentActivity);
        this.idExplotacion = idExplotacion;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return PesosRegistroFragment.newInstance(idExplotacion);
        } else {
            return PesosListadoFragment.newInstance(idExplotacion);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public String getTitulo(int position) {
        return position == 0 ? "Registrar" : "Listado";
    }
}
