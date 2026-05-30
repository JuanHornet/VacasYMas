package com.example.vacasymas.ui.mantenimientos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vacasymas.R;
import com.example.vacasymas.session.SessionManager;
import com.example.vacasymas.ui.cercados.CercadosMantenimientoActivity;
import com.example.vacasymas.ui.crotales.CrotalesActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class MantenimientosActivity extends AppCompatActivity {

    private MaterialToolbar toolbarMantenimientos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mantenimientos);

        toolbarMantenimientos = findViewById(R.id.toolbarMantenimientos);
        setSupportActionBar(toolbarMantenimientos);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mantenimientos");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.cardExplotaciones).setOnClickListener(v -> abrirExplotaciones());
        findViewById(R.id.cardCercados).setOnClickListener(v -> abrirCercados());
        findViewById(R.id.cardCrotales).setOnClickListener(v -> abrirCrotales());
        findViewById(R.id.cardUsuarios).setOnClickListener(v -> abrirUsuarios());
    }

    private void abrirExplotaciones() {
        Toast.makeText(this, "Mantenimiento de explotaciones pendiente", Toast.LENGTH_SHORT).show();
    }

    private void abrirCercados() {
        Intent intent = new Intent(this, CercadosMantenimientoActivity.class);
        startActivity(intent);
    }

    private void abrirCrotales() {
        String idExplotacion = SessionManager.getIdExplotacionSeleccionada(this);

        if (idExplotacion == null || idExplotacion.trim().isEmpty()) {
            Toast.makeText(this, "No hay explotación seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CrotalesActivity.class);
        intent.putExtra("id_explotacion_uuid", idExplotacion);
        startActivity(intent);
    }

    private void abrirUsuarios() {
        Toast.makeText(this, "Mantenimiento de usuarios pendiente", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
