package com.example.vacasymas.ui.listados;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.Animal;
import com.example.vacasymas.data.models.AnimalEnLista;
import com.example.vacasymas.data.repo.AnimalRepository;
import com.example.vacasymas.session.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class DetalleListaManualActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText etBuscarCrotal;
    private TextView tvAnimalesAnadidos;
    private RecyclerView rvAnimalesLista;

    private DBHelper dbHelper;
    private AnimalRepository animalRepository;
    private AnimalEnListaAdapter adapter;

    private String idLista;
    private String nombreLista;
    private String idExplotacionUuid;

    private RadioGroup rgFiltroSexo;
    private RadioButton rbTodos, rbMachos, rbHembras;
    private TextView tvResumenFiltro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_lista_manual);

        dbHelper = new DBHelper(this);
        animalRepository = new AnimalRepository(dbHelper);

        idExplotacionUuid = SessionManager.getIdExplotacionSeleccionada(this);

        idLista = getIntent().getStringExtra("id_lista");
        nombreLista = getIntent().getStringExtra("nombre_lista");

        if (idLista == null || idLista.trim().isEmpty()) {
            Toast.makeText(this, "No se ha recibido la lista", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        initViews();
        configurarToolbar();
        configurarRecycler();

        rbTodos.setChecked(true);

        initBusquedaAutomatica();
        cargarAnimalesLista();
        initFiltrosSexo();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarDetalleLista);
        etBuscarCrotal = findViewById(R.id.etBuscarCrotal);
        tvAnimalesAnadidos = findViewById(R.id.tvAnimalesAnadidos);
        rvAnimalesLista = findViewById(R.id.rvAnimalesLista);
        rgFiltroSexo = findViewById(R.id.rgFiltroSexo);
        rbTodos = findViewById(R.id.rbTodos);
        rbMachos = findViewById(R.id.rbMachos);
        rbHembras = findViewById(R.id.rbHembras);
        tvResumenFiltro = findViewById(R.id.tvResumenFiltro);
    }

    private void configurarToolbar() {
        toolbar.setTitle(
                nombreLista != null && !nombreLista.trim().isEmpty()
                        ? nombreLista
                        : "Lista manual"
        );

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void configurarRecycler() {
        adapter = new AnimalEnListaAdapter(new AnimalEnListaAdapter.OnAnimalListaClickListener() {
            @Override
            public void onEliminarClick(AnimalEnLista animal) {
                confirmarEliminarAnimal(animal);
            }

            @Override
            public void onMarcadoLongClick(AnimalEnLista animal) {
                alternarMarcadoAnimal(animal);
            }
        });

        rvAnimalesLista.setLayoutManager(new LinearLayoutManager(this));
        rvAnimalesLista.setAdapter(adapter);
    }


    private void cargarAnimalesLista() {

        List<AnimalEnLista> animales =
                dbHelper.obtenerAnimalesDeLista(idLista);

        adapter.actualizarLista(animales);

        tvAnimalesAnadidos.setText(
                String.valueOf(animales.size())
        );

        actualizarResumenFiltro(animales);
    }

    private void confirmarEliminarAnimal(AnimalEnLista animal) {
        new AlertDialog.Builder(this)
                .setTitle("Quitar animal")
                .setMessage("¿Quieres quitar este animal de la lista?")
                .setPositiveButton("Quitar", (dialog, which) -> {
                    String ahora = FechaUtils.ahoraIso();

                    boolean ok = dbHelper.eliminarAnimalDeLista(
                            animal.getIdDetalle(),
                            ahora
                    );

                    if (ok) {
                        cargarAnimalesLista();
                    } else {
                        Toast.makeText(this, "No se pudo quitar el animal", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void initBusquedaAutomatica() {

        etBuscarCrotal.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {

                String texto = s != null
                        ? s.toString().trim()
                        : "";

                if (texto.length() == 4) {
                    buscarYAnadirAutomaticamente(texto);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void buscarYAnadirAutomaticamente(String texto) {

        animalRepository.buscarActivosPorUltimos4DigitosYExplotacionAsync(
                texto,
                idExplotacionUuid,

                new AnimalRepository.ListaAnimalesCallback() {

                    @Override
                    public void onSuccess(List<Animal> animales) {

                        runOnUiThread(() -> {

                            if (animales == null || animales.isEmpty()) {

                                Toast.makeText(
                                        DetalleListaManualActivity.this,
                                        "Animal no encontrado",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            if (animales.size() == 1) {

                                anadirAnimalALista(animales.get(0));
                                return;
                            }

                            mostrarDialogoSeleccionAnimal(animales);
                        });
                    }

                    @Override
                    public void onError(String error) {

                        runOnUiThread(() ->
                                Toast.makeText(
                                        DetalleListaManualActivity.this,
                                        "Error buscando animales",
                                        Toast.LENGTH_SHORT
                                ).show()
                        );
                    }
                }
        );
    }
    private void anadirAnimalALista(Animal animal) {

        String ahora = FechaUtils.ahoraIso();

        boolean ok = dbHelper.añadirAnimalALista(
                idLista,
                animal,
                ahora
        );

        if (ok) {

            etBuscarCrotal.setText("");

            cargarAnimalesLista();

            Toast.makeText(
                    this,
                    "Animal añadido",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "Ese animal ya está en la lista",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void mostrarDialogoSeleccionAnimal(List<Animal> animales) {

        String[] items = new String[animales.size()];

        for (int i = 0; i < animales.size(); i++) {

            Animal animal = animales.get(i);

            items[i] = animal.getCrotal()
                    + " - "
                    + (animal.getRaza() != null
                    ? animal.getRaza()
                    : "");
        }

        new AlertDialog.Builder(this)
                .setTitle("Seleccionar animal")
                .setItems(items, (dialog, which) -> {

                    Animal seleccionado = animales.get(which);

                    anadirAnimalALista(seleccionado);

                })
                .show();
    }
    private void initFiltrosSexo() {

        rgFiltroSexo.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.rbTodos) {

                cargarAnimalesLista();

            } else if (checkedId == R.id.rbMachos) {

                cargarAnimalesListaFiltrada("Macho");

            } else if (checkedId == R.id.rbHembras) {

                cargarAnimalesListaFiltrada("Hembra");
            }
        });
    }
    private void cargarAnimalesListaFiltrada(String sexo) {

        List<AnimalEnLista> animales =
                dbHelper.obtenerAnimalesDeListaPorSexo(idLista, sexo);

        adapter.actualizarLista(animales);

        tvAnimalesAnadidos.setText(
                String.valueOf(animales.size())
        );

        actualizarResumenFiltro(animales);
    }


    private void actualizarResumenFiltro(List<AnimalEnLista> animales) {

        int marcados = 0;

        for (AnimalEnLista animal : animales) {

            if (animal.getMarcado() == 1) {
                marcados++;
            }
        }

        tvResumenFiltro.setText(
                animales.size()
                        + " visibles · "
                        + marcados
                        + " marcados"
        );
    }

    private void alternarMarcadoAnimal(AnimalEnLista animal) {
        int nuevoMarcado = animal.getMarcado() == 1 ? 0 : 1;
        String ahora = FechaUtils.ahoraIso();

        boolean ok = dbHelper.actualizarMarcadoAnimalLista(
                animal.getIdDetalle(),
                nuevoMarcado,
                ahora
        );

        if (ok) {
            cargarSegunFiltroActual();
        }
    }

    private void cargarSegunFiltroActual() {
        if (rbMachos.isChecked()) {
            cargarAnimalesListaFiltrada("Macho");
        } else if (rbHembras.isChecked()) {
            cargarAnimalesListaFiltrada("Hembra");
        } else {
            cargarAnimalesLista();
        }
    }
}