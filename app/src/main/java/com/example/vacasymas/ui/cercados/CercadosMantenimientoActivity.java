package com.example.vacasymas.ui.cercados;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.Cercado;
import com.example.vacasymas.session.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CercadosMantenimientoActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerCercados;
    private FloatingActionButton fabAddCercado;

    private DBHelper dbHelper;
    private CercadoAdapter adapter;
    private List<Cercado> listaCercados = new ArrayList<>();

    private String idExplotacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cercados_mantenimiento);

        dbHelper = new DBHelper(this);
        idExplotacion = SessionManager.getIdExplotacionSeleccionada(this);

        toolbar = findViewById(R.id.toolbarCercados);
        recyclerCercados = findViewById(R.id.recyclerCercados);
        fabAddCercado = findViewById(R.id.fabAddCercado);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Cercados");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        adapter = new CercadoAdapter(listaCercados, new CercadoAdapter.OnCercadoClickListener() {
            @Override
            public void onClick(Cercado cercado) {
                Toast.makeText(
                        CercadosMantenimientoActivity.this,
                        cercado.getNombre(),
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onLongClick(Cercado cercado) {
                mostrarOpcionesCercado(cercado);
            }
        });

        recyclerCercados.setLayoutManager(new LinearLayoutManager(this));
        recyclerCercados.setAdapter(adapter);

        fabAddCercado.setOnClickListener(v -> mostrarDialogoCercado(null));

        cargarCercados();
    }

    private void cargarCercados() {
        if (idExplotacion == null || idExplotacion.trim().isEmpty()) {
            Toast.makeText(this, "No hay explotación seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }

        listaCercados.clear();
        listaCercados.addAll(dbHelper.obtenerCercadosActivosPorExplotacion(idExplotacion));
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void mostrarDialogoCercado(Cercado cercadoEditar) {

        View view = getLayoutInflater().inflate(R.layout.dialog_cercado, null);

        EditText etNombre = view.findViewById(R.id.etNombreCercado);
        EditText etSuperficie = view.findViewById(R.id.etSuperficie);
        Spinner spTipo = view.findViewById(R.id.spTipoCercado);
        EditText etObservaciones = view.findViewById(R.id.etObservaciones);

        String[] tipos = {
                "Dehesa",
                "Paridera",
                "Manejo",
                "Toros",
                "Cuarentena",
                "Otro"
        };

        ArrayAdapter<String> adapterTipos = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tipos
        );
        adapterTipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapterTipos);

        boolean editando = cercadoEditar != null;

        if (editando) {
            etNombre.setText(cercadoEditar.getNombre());

            if (cercadoEditar.getSuperficieHa() != null) {
                etSuperficie.setText(String.valueOf(cercadoEditar.getSuperficieHa()));
            }

            if (cercadoEditar.getObservaciones() != null) {
                etObservaciones.setText(cercadoEditar.getObservaciones());
            }

            if (cercadoEditar.getTipo() != null) {
                for (int i = 0; i < tipos.length; i++) {
                    if (tipos[i].equalsIgnoreCase(cercadoEditar.getTipo())) {
                        spTipo.setSelection(i);
                        break;
                    }
                }
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(editando ? "Editar cercado" : "Nuevo cercado")
                .setView(view)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

                String nombre = etNombre.getText().toString().trim();
                String superficieTexto = etSuperficie.getText().toString().trim();
                String tipo = spTipo.getSelectedItem().toString();
                String observaciones = etObservaciones.getText().toString().trim();

                if (nombre.isEmpty()) {
                    etNombre.setError("Introduce un nombre");
                    return;
                }

                Double superficie = null;

                if (!superficieTexto.isEmpty()) {
                    try {
                        superficie = Double.parseDouble(superficieTexto);
                    } catch (NumberFormatException e) {
                        etSuperficie.setError("Superficie no válida");
                        return;
                    }
                }

                Cercado cercado;

                if (editando) {
                    cercado = cercadoEditar;
                } else {
                    cercado = new Cercado();
                    cercado.setId(UUID.randomUUID().toString());
                    cercado.setIdExplotacionUuid(idExplotacion);
                    cercado.setActivo(1);
                    cercado.setEliminado(0);
                }

                cercado.setNombre(nombre);
                cercado.setSuperficieHa(superficie);
                cercado.setTipo(tipo);
                cercado.setObservaciones(observaciones);
                cercado.setFechaActualizacion(FechaUtils.ahoraIso());
                cercado.setSincronizado(0);

                dbHelper.insertarOActualizarCercado(cercado);

                Toast.makeText(
                        this,
                        editando ? "Cercado actualizado" : "Cercado creado",
                        Toast.LENGTH_SHORT
                ).show();

                dialog.dismiss();
                cargarCercados();
            });
        });

        dialog.show();
    }

    private void mostrarOpcionesCercado(Cercado cercado) {
        String[] opciones = {"Editar", "Eliminar"};

        new AlertDialog.Builder(this)
                .setTitle(cercado.getNombre())
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        mostrarDialogoCercado(cercado);
                    } else if (which == 1) {
                        confirmarEliminarCercado(cercado);
                    }
                })
                .show();
    }

    private void confirmarEliminarCercado(Cercado cercado) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar cercado")
                .setMessage("¿Seguro que quieres eliminar \"" + cercado.getNombre() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    dbHelper.eliminarCercadoLogico(
                            cercado.getId(),
                            FechaUtils.ahoraIso()
                    );

                    Toast.makeText(this, "Cercado eliminado", Toast.LENGTH_SHORT).show();
                    cargarCercados();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
