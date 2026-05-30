package com.example.vacasymas.ui.listados;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.Animal;
import com.example.vacasymas.session.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class ResultadoFiltroAnimalesActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvResumenResultado;
    private RecyclerView recyclerResultadoFiltro;
    private Button btnGuardarListadoFiltro;

    private ResultadoFiltroAnimalAdapter adapter;

    private DBHelper dbHelper;


    private ArrayList<String> idsAnimales;
    private List<Animal> animales;

    private String idExplotacion;

    private String tituloListadoSugerido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_filtro_animales);

        dbHelper = new DBHelper(this);
        idExplotacion = SessionManager.getIdExplotacionSeleccionada(this);



        idsAnimales = getIntent().getStringArrayListExtra("ids_animales");

        if (idsAnimales == null) {
            idsAnimales = new ArrayList<>();
        }

        inicializarVistas();
        configurarToolbar();
        tituloListadoSugerido = getIntent().getStringExtra("titulo_listado");

        if (tituloListadoSugerido != null && !tituloListadoSugerido.trim().isEmpty()) {
            toolbar.setTitle(tituloListadoSugerido);
        }
        cargarAnimales();

        btnGuardarListadoFiltro.setOnClickListener(v -> mostrarDialogoGuardarListado());
    }

    private void inicializarVistas() {
        toolbar = findViewById(R.id.toolbarResultadoFiltro);
        tvResumenResultado = findViewById(R.id.tvResumenResultado);
        recyclerResultadoFiltro = findViewById(R.id.recyclerResultadoFiltro);
        btnGuardarListadoFiltro = findViewById(R.id.btnGuardarListadoFiltro);
    }

    private void configurarToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void cargarAnimales() {
        animales = dbHelper.obtenerAnimalesPorIds(idsAnimales);

        adapter = new ResultadoFiltroAnimalAdapter(animales);

        adapter.setOnSeleccionCambioListener(this::actualizarResumen);

        recyclerResultadoFiltro.setLayoutManager(new LinearLayoutManager(this));
        recyclerResultadoFiltro.setAdapter(adapter);

        actualizarResumen();
    }

    private void mostrarDialogoGuardarListado() {
        EditText editText = new EditText(this);
        editText.setHint("Nombre del listado");

        if (tituloListadoSugerido != null && !tituloListadoSugerido.trim().isEmpty()) {
            editText.setText(tituloListadoSugerido);
            editText.setSelection(editText.getText().length());
        }

        new AlertDialog.Builder(this)
                .setTitle("Guardar listado")
                .setView(editText)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = editText.getText().toString().trim();

                    if (nombre.isEmpty()) {
                        Toast.makeText(this, "Introduce un nombre", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    guardarListado(nombre);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void guardarListado(String nombre) {

        List<Animal> animalesSeleccionados = adapter.getAnimalesSeleccionados();

        if (animalesSeleccionados.isEmpty()) {
            Toast.makeText(this, "No hay animales seleccionados", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean ok = dbHelper.crearListadoDesdeAnimales(
                nombre,
                idExplotacion,
                animalesSeleccionados
        );

        if (ok) {
            Toast.makeText(this, "Listado guardado correctamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar listado", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarResumen() {
        if (adapter == null) return;

        tvResumenResultado.setText(
                "Animales seleccionados: "
                        + adapter.getTotalSeleccionados()
                        + " / "
                        + adapter.getTotalAnimales()
        );
    }
}
