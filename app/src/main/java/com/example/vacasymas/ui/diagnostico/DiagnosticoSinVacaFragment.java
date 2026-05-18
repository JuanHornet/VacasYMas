package com.example.vacasymas.ui.diagnostico;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DiagnosticoSinVacaFragment extends Fragment {

    public static DiagnosticoSinVacaFragment newInstance() {
        return new DiagnosticoSinVacaFragment();
    }

    public DiagnosticoSinVacaFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater,
                             @Nullable android.view.ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        TextView tv = new TextView(requireContext());
        tv.setText("Selecciona primero una vaca en la pestaña Registrar.");
        tv.setTextSize(16);
        tv.setTextColor(0xFF666666);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(32, 32, 32, 32);

        return tv;
    }
}
