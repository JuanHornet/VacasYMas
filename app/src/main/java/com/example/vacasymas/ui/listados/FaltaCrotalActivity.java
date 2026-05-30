package com.example.vacasymas.ui.listados;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.Animal;
import com.example.vacasymas.session.SessionManager;
import com.example.vacasymas.ui.animales.DetalleAnimalActivity;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class FaltaCrotalActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvResumen;
    private RecyclerView recycler;

    private DBHelper dbHelper;
    private String idExplotacionUuid;

    private List<Animal> animales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_falta_crotal);

        dbHelper = new DBHelper(this);
        idExplotacionUuid = SessionManager.getIdExplotacionSeleccionada(this);

        if (idExplotacionUuid == null || idExplotacionUuid.trim().isEmpty()) {
            Toast.makeText(this, "No hay explotación seleccionada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        configurarToolbar();
        cargarAnimales();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarFaltaCrotal);
        tvResumen = findViewById(R.id.tvResumenFaltaCrotal);
        recycler = findViewById(R.id.recyclerFaltaCrotal);
    }

    private void configurarToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void cargarAnimales() {
        animales = dbHelper.obtenerAnimalesFaltaCrotal(idExplotacionUuid);

        tvResumen.setText("Animales con falta de crotal: " + animales.size());

        FaltaCrotalAdapter adapter = new FaltaCrotalAdapter(animales, animal -> {
            Intent intent = new Intent(this, DetalleAnimalActivity.class);
            intent.putExtra("id_animal", animal.getId());
            startActivity(intent);
        });

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dbHelper != null && idExplotacionUuid != null) {
            cargarAnimales();
        }
    }
}