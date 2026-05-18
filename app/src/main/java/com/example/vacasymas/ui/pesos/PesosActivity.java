package com.example.vacasymas.ui.pesos;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vacasymas.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class PesosActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TabLayout tabPesos;
    private ViewPager2 viewPagerPesos;

    private String idExplotacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesos);

        idExplotacion = getIntent().getStringExtra("id_explotacion");

        if (idExplotacion == null || idExplotacion.trim().isEmpty()) {
            Toast.makeText(this, "No se ha recibido la explotación", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        toolbar = findViewById(R.id.toolbarPesos);
        tabPesos = findViewById(R.id.tabPesos);
        viewPagerPesos = findViewById(R.id.viewPagerPesos);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        PesosPagerAdapter adapter = new PesosPagerAdapter(this, idExplotacion);
        viewPagerPesos.setAdapter(adapter);
        viewPagerPesos.setOffscreenPageLimit(2);

        new TabLayoutMediator(tabPesos, viewPagerPesos,
                (tab, position) -> tab.setText(adapter.getTitulo(position))
        ).attach();
    }
}