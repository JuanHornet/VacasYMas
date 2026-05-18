package com.example.vacasymas.ui.animales;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.Animal;
import com.example.vacasymas.data.repo.AnimalRepository;
import com.example.vacasymas.ui.animales.detalle.adapters.AnimalAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class BuscarAnimalActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText etBuscar;
    private RecyclerView recyclerAnimales;
    private LinearLayout layoutEmpty;
    private com.google.android.material.button.MaterialButton btnBuscar;

    private AnimalAdapter adapter;
    private AnimalRepository animalRepository;

    private final Handler handlerBusqueda = new Handler(Looper.getMainLooper());
    private Runnable runnableBusqueda;

    private String idExplotacionUuid;

    private TextView tvExplotacion;
    private String nombreExplotacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_animal);

        idExplotacionUuid = getIntent().getStringExtra("id_explotacion_uuid");
        nombreExplotacion = getIntent().getStringExtra("nombre_explotacion");


        initViews();
        initToolbar();
        initRepository();
        initRecycler();
        initBusqueda();
        mostrarListaVaciaInicial();
        mostrarExplotacion();

        android.util.Log.d("BuscarAnimal", "idExplotacionUuid recibido: " + idExplotacionUuid);
        android.util.Log.d("BuscarAnimal", "nombreExplotacion recibido: " + nombreExplotacion);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etBuscar = findViewById(R.id.etBuscar);
        recyclerAnimales = findViewById(R.id.recyclerAnimales);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvExplotacion = findViewById(R.id.tvExplotacion);
        btnBuscar = findViewById(R.id.btnBuscar);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initRepository() {
        DBHelper dbHelper = new DBHelper(this);
        animalRepository = new AnimalRepository(dbHelper);
    }

    private void initRecycler() {
        adapter = new AnimalAdapter(animal -> abrirDetalleAnimal(animal));
        recyclerAnimales.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        recyclerAnimales.setAdapter(adapter);
    }

    private void initBusqueda() {
        btnBuscar.setOnClickListener(v -> buscarDesdeCampo());

        etBuscar.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String texto = s != null ? s.toString().trim() : "";

                if (texto.length() == 4) {
                    buscarDesdeCampo();
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });

        etBuscar.setOnEditorActionListener((v, actionId, event) -> {
            buscarDesdeCampo();
            return true;
        });
    }

    private void lanzarBusquedaConDelay(String texto) {
        if (runnableBusqueda != null) {
            handlerBusqueda.removeCallbacks(runnableBusqueda);
        }

        runnableBusqueda = () -> ejecutarBusqueda(texto);
        handlerBusqueda.postDelayed(runnableBusqueda, 300);
    }

    private void ejecutarBusqueda(String texto) {

        android.util.Log.d("BuscarAnimal", "Buscando texto: " + texto);
        android.util.Log.d("BuscarAnimal", "Explotación: " + idExplotacionUuid);

        if (texto.isEmpty()) {
            Toast.makeText(this, "Introduce los 4 últimos dígitos del crotal", Toast.LENGTH_SHORT).show();
            return;
        }

        if (texto.length() != 4) {
            Toast.makeText(this, "Debes introducir exactamente 4 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idExplotacionUuid == null || idExplotacionUuid.trim().isEmpty()) {
            adapter.setLista(new ArrayList<>());
            mostrarEstadoVacio(true);
            Toast.makeText(this, "No se ha recibido la explotación seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }

        animalRepository.buscarPorUltimos4DigitosYExplotacionAsync(texto, idExplotacionUuid, new AnimalRepository.ListaAnimalesCallback() {
            @Override
            public void onSuccess(List<Animal> animales) {
                runOnUiThread(() -> {
                    if (animales == null || animales.isEmpty()) {
                        adapter.setLista(new ArrayList<>());
                        mostrarEstadoVacio(true);
                        android.util.Log.d("BuscarAnimal", "Resultados encontrados: " + animales.size());
                        return;
                    }

                    if (animales.size() == 1) {
                        abrirDetalleAnimal(animales.get(0));
                        return;
                    }

                    adapter.setLista(animales);
                    mostrarEstadoVacio(false);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    adapter.setLista(new ArrayList<>());
                    mostrarEstadoVacio(true);
                    Toast.makeText(BuscarAnimalActivity.this, "Error al buscar animales", Toast.LENGTH_SHORT).show();
                });
            }
        });


    }

    private void mostrarListaVaciaInicial() {
        adapter.setLista(new ArrayList<>());
        mostrarEstadoVacio(true);
    }

    private void mostrarEstadoVacio(boolean mostrar) {
        layoutEmpty.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        recyclerAnimales.setVisibility(mostrar ? View.GONE : View.VISIBLE);
    }

    private String valorSeguro(String valor) {
        return valor == null || valor.trim().isEmpty() ? "-" : valor;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (runnableBusqueda != null) {
            handlerBusqueda.removeCallbacks(runnableBusqueda);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        limpiarBusqueda();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void mostrarExplotacion() {
        if (nombreExplotacion == null || nombreExplotacion.trim().isEmpty()) {
            tvExplotacion.setText("Explotación: -");
        } else {
            tvExplotacion.setText("Explotación: " + nombreExplotacion);
        }
    }

    private void abrirDetalleAnimal(Animal animal) {
        android.content.Intent intent = new android.content.Intent(BuscarAnimalActivity.this, DetalleAnimalActivity.class);
        intent.putExtra("id_animal", animal.getId());
        startActivity(intent);
    }
    private void buscarDesdeCampo() {
        String texto = etBuscar.getText() != null ? etBuscar.getText().toString().trim() : "";
        ejecutarBusqueda(texto);
    }


    private void limpiarBusqueda() {
        if (etBuscar != null) {
            etBuscar.setText("");
            etBuscar.clearFocus();
        }

        if (adapter != null) {
            adapter.setLista(new ArrayList<>());
        }

        mostrarEstadoVacio(true);
    }

}