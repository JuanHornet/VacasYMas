package com.example.vacasymas.ui.listados;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vacasymas.R;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.Animal;
import com.example.vacasymas.session.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ListadosActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;

    private String idExplotacionUuid;

    private MaterialCardView cardNuevoListado, cardAbrirListado, cardListadoFiltros;

    private MaterialCardView cardFaltaCrotal;
    private MaterialCardView cardVacasViejas;
    private MaterialCardView cardVacasVacias;
    private MaterialCardView cardVacasVaciasDosAnios;
    private MaterialCardView cardTernerosParidera;
    private MaterialCardView cardEventosParidera;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listados);

        dbHelper = new DBHelper(this);

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
        cardListadoFiltros = findViewById(R.id.cardListadoFiltros);
        cardAbrirListado = findViewById(R.id.cardAbrirListado);

        cardFaltaCrotal = findViewById(R.id.cardFaltaCrotal);
        cardVacasViejas = findViewById(R.id.cardVacasViejas);
        cardVacasVacias = findViewById(R.id.cardVacasVacias);
        cardVacasVaciasDosAnios = findViewById(R.id.cardVacasVaciasDosAnios);
        cardTernerosParidera = findViewById(R.id.cardTernerosParidera);
        cardEventosParidera = findViewById(R.id.cardEventosParidera);
    }

    private void configurarToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void configurarClicks() {
        cardNuevoListado.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearListaManualActivity.class);
            startActivity(intent);
        });

        cardListadoFiltros.setOnClickListener(v -> {
            Intent intent = new Intent(this, FiltroAnimalesActivity.class);
            startActivity(intent);
        });

        cardAbrirListado.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListasManualesActivity.class);
            startActivity(intent);
        });

        cardFaltaCrotal.setOnClickListener(v -> {
            Intent intent = new Intent(this, FaltaCrotalActivity.class);
            startActivity(intent);
        });

        cardVacasViejas.setOnClickListener(v -> {
            List<Animal> animales = dbHelper.obtenerVacasViejas(idExplotacionUuid);
            abrirResultadoRapido("Vacas +14", animales);
        });

        cardVacasVacias.setOnClickListener(v -> {
            List<Animal> animales = dbHelper.obtenerVacasVaciasParideraActual(idExplotacionUuid);
            abrirResultadoRapido("Sin parir", animales);
        });

        cardVacasVaciasDosAnios.setOnClickListener(v -> {
            List<Animal> animales = dbHelper.obtenerVacasSinParirDosAnos(idExplotacionUuid);
            abrirResultadoRapido("Sin parir 2 años", animales);
        });

        cardTernerosParidera.setOnClickListener(v -> {
            Intent intent = new Intent(this, ParideraActivity.class);
            startActivity(intent);
        });

        cardEventosParidera.setOnLongClickListener(v -> {

            int insertados = dbHelper.migrarPartosHistoricosDesdeAnimales(idExplotacionUuid);

            Toast.makeText(
                    this,
                    "Partos históricos migrados: " + insertados,
                    Toast.LENGTH_LONG
            ).show();

            return true;
        });


        cardAbrirListado.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListasManualesActivity.class);
            startActivity(intent);
        });

        cardListadoFiltros.setOnClickListener(v -> {
            Intent intent = new Intent(ListadosActivity.this, FiltroAnimalesActivity.class);
            startActivity(intent);
        });
    }


    private void abrirResultadoRapido(String titulo, List<Animal> animales) {

        if (animales == null || animales.isEmpty()) {
            Toast.makeText(this, "No hay animales para este listado", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> idsAnimales = new ArrayList<>();

        for (Animal animal : animales) {
            idsAnimales.add(animal.getId());
        }

        Intent intent = new Intent(this, ResultadoFiltroAnimalesActivity.class);
        intent.putStringArrayListExtra("ids_animales", idsAnimales);
        intent.putExtra("titulo_listado", titulo);
        startActivity(intent);
    }
}