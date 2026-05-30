package com.example.vacasymas.ui.listados;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.base.ParideraUtils;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.Animal;
import com.example.vacasymas.session.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class ParideraActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private Spinner spinnerParidera;
    private TextView tvTotalNacidos;
    private TextView tvTotalFallecidos;
    private TextView tvTotalStock;

    private TextView tvMachosNacidos;
    private TextView tvHembrasNacidos;

    private TextView tvMachosFallecidos;
    private TextView tvHembrasFallecidos;

    private TextView tvMachosStock;
    private TextView tvHembrasStock;


    private RecyclerView recyclerTernerosParidera;

    private DBHelper dbHelper;
    private String idExplotacionUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paridera);

        dbHelper = new DBHelper(this);
        idExplotacionUuid = SessionManager.getIdExplotacionSeleccionada(this);

        if (idExplotacionUuid == null || idExplotacionUuid.trim().isEmpty()) {
            Toast.makeText(this, "No hay explotación seleccionada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        configurarToolbar();
        configurarSpinner();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarParidera);
        spinnerParidera = findViewById(R.id.spinnerParidera);

        tvTotalNacidos = findViewById(R.id.tvTotalNacidos);
        tvMachosNacidos = findViewById(R.id.tvMachosNacidos);
        tvHembrasNacidos = findViewById(R.id.tvHembrasNacidos);

        tvTotalFallecidos = findViewById(R.id.tvTotalFallecidos);
        tvMachosFallecidos = findViewById(R.id.tvMachosFallecidos);
        tvHembrasFallecidos = findViewById(R.id.tvHembrasFallecidos);


        tvTotalStock = findViewById(R.id.tvTotalStock);
        tvMachosStock = findViewById(R.id.tvMachosStock);
        tvHembrasStock = findViewById(R.id.tvHembrasStock);

        recyclerTernerosParidera = findViewById(R.id.recyclerTernerosParidera);
        recyclerTernerosParidera.setLayoutManager(new LinearLayoutManager(this));
    }

    private void configurarToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void configurarSpinner() {
        List<String> parideras = ParideraUtils.obtenerUltimasCincoParideras();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                parideras
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParidera.setAdapter(adapter);

        spinnerParidera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(
                    AdapterView<?> parent,
                    View view,
                    int position,
                    long id
            ) {
                String paridera = parideras.get(position);
                cargarDatosParidera(paridera);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void cargarDatosParidera(String paridera) {

        List<Animal> nacidos =
                dbHelper.obtenerTernerosPorParidera(idExplotacionUuid, paridera);

        List<Animal> fallecidos =
                dbHelper.obtenerTernerosFallecidosPorParidera(idExplotacionUuid, paridera);

        List<Animal> stock =
                dbHelper.obtenerTernerosStockPorParidera(idExplotacionUuid, paridera);

        pintarResumen(nacidos, fallecidos, stock);

        TerneroParideraAdapter adapter = new TerneroParideraAdapter(nacidos);
        recyclerTernerosParidera.setAdapter(adapter);
    }

    private void pintarResumen(
            List<Animal> nacidos,
            List<Animal> fallecidos,
            List<Animal> stock
    ) {
        tvTotalNacidos.setText(String.valueOf(nacidos.size()));
        tvMachosNacidos.setText("♂ " + contarMachos(nacidos));
        tvHembrasNacidos.setText("♀ " + contarHembras(nacidos));

        tvTotalFallecidos.setText(String.valueOf(fallecidos.size()));
        tvMachosFallecidos.setText("♂ " + contarMachos(fallecidos));
        tvHembrasFallecidos.setText("♀ " + contarHembras(fallecidos));

        tvTotalStock.setText(String.valueOf(stock.size()));
        tvMachosStock.setText("♂ " + contarMachos(stock));
        tvHembrasStock.setText("♀ " + contarHembras(stock));
    }

    private int contarMachos(List<Animal> animales) {
        int total = 0;

        for (Animal animal : animales) {
            if (animal.getSexo() != null &&
                    (animal.getSexo().equalsIgnoreCase("M")
                            || animal.getSexo().equalsIgnoreCase("Macho"))) {
                total++;
            }
        }

        return total;
    }

    private int contarHembras(List<Animal> animales) {
        int total = 0;

        for (Animal animal : animales) {
            if (animal.getSexo() != null &&
                    (animal.getSexo().equalsIgnoreCase("H")
                            || animal.getSexo().equalsIgnoreCase("Hembra"))) {
                total++;
            }
        }

        return total;
    }
}
