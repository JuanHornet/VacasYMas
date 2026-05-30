package com.example.vacasymas.ui.listados;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vacasymas.R;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.Animal;
import com.example.vacasymas.data.models.FiltroAnimales;
import com.example.vacasymas.session.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class FiltroAnimalesActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private Spinner spinnerTipoAnimal, spinnerSexo, spinnerEstadoReproductivo, spinnerCercado;
    private CheckBox checkSinCrotalIzquierdo, checkSinCrotalDerecho;
    private EditText etEdadMinima, etEdadMaxima;
    private EditText etEdadMinimaAnios, etEdadMaximaAnios;
    private Button btnVerAnimales;

    private DBHelper dbHelper;
    private String idExplotacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro_animales);

        dbHelper = new DBHelper(this);
        idExplotacion = SessionManager.getIdExplotacionSeleccionada(this);

        inicializarVistas();
        configurarToolbar();
        cargarSpinners();

        btnVerAnimales.setOnClickListener(v -> aplicarFiltros());
    }

    private void inicializarVistas() {
        toolbar = findViewById(R.id.toolbarFiltros);
        spinnerTipoAnimal = findViewById(R.id.spinnerTipoAnimal);
        spinnerSexo = findViewById(R.id.spinnerSexo);
        spinnerEstadoReproductivo = findViewById(R.id.spinnerEstadoReproductivo);
        spinnerCercado = findViewById(R.id.spinnerCercado);
        checkSinCrotalIzquierdo = findViewById(R.id.checkSinCrotalIzquierdo);
        checkSinCrotalDerecho = findViewById(R.id.checkSinCrotalDerecho);
        etEdadMinima = findViewById(R.id.etEdadMinima);
        etEdadMaxima = findViewById(R.id.etEdadMaxima);
        btnVerAnimales = findViewById(R.id.btnVerAnimales);
        etEdadMinimaAnios = findViewById(R.id.etEdadMinimaAnios);
        etEdadMaximaAnios = findViewById(R.id.etEdadMaximaAnios);
    }

    private void configurarToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void cargarSpinners() {
        cargarSpinner(spinnerTipoAnimal, new String[]{
                "Todos",
                "Vaca",
                "Toro",
                "Novilla",
                "Ternero macho",
                "Ternera hembra"
        });

        cargarSpinner(spinnerSexo, new String[]{
                "Todos",
                "Macho",
                "Hembra"
        });

        cargarSpinner(spinnerEstadoReproductivo, new String[]{
                "Todos",
                "Nada",
                "Vacía",
                "Cubierta",
                "Preñada"
        });

        List<String> cercados = new ArrayList<>();
        cercados.add("Todos");
        cercados.addAll(dbHelper.obtenerCercadosDeExplotacion(idExplotacion));

        ArrayAdapter<String> adapterCercados = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                cercados
        );
        adapterCercados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCercado.setAdapter(adapterCercados);
    }

    private void cargarSpinner(Spinner spinner, String[] datos) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                datos
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void aplicarFiltros() {
        FiltroAnimales filtro = construirFiltroDesdePantalla();

        List<Animal> animales = dbHelper.obtenerAnimalesFiltrados(idExplotacion, filtro);

        if (animales.isEmpty()) {
            Toast.makeText(this, "No se encontraron animales", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> idsAnimales = new ArrayList<>();

        for (Animal animal : animales) {
            idsAnimales.add(animal.getId());
        }

        Intent intent = new Intent(this, ResultadoFiltroAnimalesActivity.class);
        intent.putStringArrayListExtra("ids_animales", idsAnimales);
        startActivity(intent);
    }

    private FiltroAnimales construirFiltroDesdePantalla() {
        FiltroAnimales filtro = new FiltroAnimales();

        String tipo = spinnerTipoAnimal.getSelectedItem().toString();

        switch (tipo) {
            case "Vaca":
                filtro.setEstatus(10003);
                break;
            case "Toro":
                filtro.setEstatus(10004);
                break;
            case "Novilla":
                filtro.setEstatus(10005);
                break;
            case "Ternero macho":
                filtro.setEstatus(10001);
                break;
            case "Ternera hembra":
                filtro.setEstatus(10002);
                break;
        }

        String sexo = spinnerSexo.getSelectedItem().toString();
        if (!sexo.equals("Todos")) {
            filtro.setSexo(sexo.equals("Macho") ? "M" : "H");
        }

        String estado = spinnerEstadoReproductivo.getSelectedItem().toString();
        if (!estado.equals("Todos")) {
            switch (estado) {
                case "Nada":
                    filtro.setEstadoReproductivo("nada");
                    break;
                case "Vacía":
                    filtro.setEstadoReproductivo("vacia");
                    break;
                case "Cubierta":
                    filtro.setEstadoReproductivo("cubierta");
                    break;
                case "Preñada":
                    filtro.setEstadoReproductivo("preñada");
                    break;
            }
        }

        String cercado = spinnerCercado.getSelectedItem().toString();
        if (!cercado.equals("Todos")) {
            filtro.setCercado(cercado);
        }

        filtro.setSinCrotalIzquierdo(checkSinCrotalIzquierdo.isChecked());
        filtro.setSinCrotalDerecho(checkSinCrotalDerecho.isChecked());

        if (!etEdadMinima.getText().toString().trim().isEmpty()) {
            filtro.setEdadMinimaMeses(Integer.parseInt(etEdadMinima.getText().toString().trim()));
        }

        if (!etEdadMaxima.getText().toString().trim().isEmpty()) {
            filtro.setEdadMaximaMeses(Integer.parseInt(etEdadMaxima.getText().toString().trim()));
        }

        if (!etEdadMinimaAnios.getText().toString().trim().isEmpty()) {
            filtro.setEdadMinimaAnios(
                    Integer.parseInt(etEdadMinimaAnios.getText().toString().trim())
            );
        }

        if (!etEdadMaximaAnios.getText().toString().trim().isEmpty()) {
            filtro.setEdadMaximaAnios(
                    Integer.parseInt(etEdadMaximaAnios.getText().toString().trim())
            );
        }

        return filtro;
    }
}
