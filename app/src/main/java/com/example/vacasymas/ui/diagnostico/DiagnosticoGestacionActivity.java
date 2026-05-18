package com.example.vacasymas.ui.diagnostico;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vacasymas.R;
import com.example.vacasymas.session.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DiagnosticoGestacionActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TabLayout tabDiagnosticoGestacion;
    private ViewPager2 viewPagerDiagnosticoGestacion;
    private DiagnosticoGestacionPagerAdapter adapter;
    private String idExplotacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostico_gestacion);

        idExplotacion = getIntent().getStringExtra("id_explotacion");

        if (idExplotacion == null || idExplotacion.trim().isEmpty()) {
            idExplotacion = SessionManager.getIdExplotacionSeleccionada(this);
        }

        if (idExplotacion == null || idExplotacion.trim().isEmpty()) {
            Toast.makeText(this, "No se ha recibido la explotación", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        toolbar = findViewById(R.id.toolbarDiagnosticoGestacion);
        tabDiagnosticoGestacion = findViewById(R.id.tabDiagnosticoGestacion);
        viewPagerDiagnosticoGestacion = findViewById(R.id.viewPagerDiagnosticoGestacion);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new DiagnosticoGestacionPagerAdapter(this, idExplotacion);

        viewPagerDiagnosticoGestacion.setAdapter(adapter);
        viewPagerDiagnosticoGestacion.setOffscreenPageLimit(2);

        new TabLayoutMediator(
                tabDiagnosticoGestacion,
                viewPagerDiagnosticoGestacion,
                (tab, position) -> tab.setText(adapter.getTitulo(position))
        ).attach();
    }

    public void actualizarVacaSeleccionadaParaRepro(String idAnimal, String crotalAnimal) {
        if (adapter != null) {
            adapter.setAnimalSeleccionado(idAnimal, crotalAnimal);
        }
    }
}