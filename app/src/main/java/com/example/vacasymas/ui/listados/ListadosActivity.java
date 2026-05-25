package com.example.vacasymas.ui.listados;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vacasymas.R;
import com.example.vacasymas.session.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

public class ListadosActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private MaterialCardView cardNuevoListado, cardAbrirListado;

    private String idExplotacionUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listados);

        idExplotacionUuid = SessionManager.getIdExplotacionSeleccionada(this);

        if (idExplotacionUuid == null || idExplotacionUuid.trim().isEmpty()) {
            Toast.makeText(this, "No hay explotación seleccionada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        configurarToolbar();
        configurarClicks();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarListados);
        cardNuevoListado = findViewById(R.id.cardNuevoListado);
        cardAbrirListado = findViewById(R.id.cardAbrirListado);
    }

    private void configurarToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void configurarClicks() {
        cardNuevoListado.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearListaManualActivity.class);
            startActivity(intent);
        });

        cardAbrirListado.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListasManualesActivity.class);
            startActivity(intent);
        });
    }
}