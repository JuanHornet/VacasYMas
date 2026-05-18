package com.example.vacasymas.ui.reproduccion;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.EventoReproductivo;
import com.example.vacasymas.data.repo.EventoReproductivoRepository;
import com.example.vacasymas.ui.reproduccion.adapters.CriaPendienteAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class CriasPendientesActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView rvCriasPendientes;
    private android.widget.TextView tvVacio;

    private CriaPendienteAdapter adapter;
    private EventoReproductivoRepository repository;

    private String idExplotacionUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crias_pendientes);

        idExplotacionUuid = getIntent().getStringExtra("id_explotacion_uuid");

        repository = new EventoReproductivoRepository(new DBHelper(this));

        initViews();
        initToolbar();
        configurarRecycler();
        cargarPendientes();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvCriasPendientes = findViewById(R.id.rvCriasPendientes);
        tvVacio = findViewById(R.id.tvVacio);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void configurarRecycler() {
        adapter = new CriaPendienteAdapter(this::mostrarOpcionesCria);

        rvCriasPendientes.setLayoutManager(new LinearLayoutManager(this));
        rvCriasPendientes.setAdapter(adapter);
    }

    private void cargarPendientes() {
        if (idExplotacionUuid == null || idExplotacionUuid.trim().isEmpty()) {
            Toast.makeText(this, "No se ha recibido la explotación", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        new Thread(() -> {
            List<EventoReproductivo> lista =
                    repository.obtenerCriasPendientesIdentificar(idExplotacionUuid);

            runOnUiThread(() -> {
                adapter.actualizarLista(lista);

                if (lista == null || lista.isEmpty()) {
                    tvVacio.setVisibility(android.view.View.VISIBLE);
                    rvCriasPendientes.setVisibility(android.view.View.GONE);
                } else {
                    tvVacio.setVisibility(android.view.View.GONE);
                    rvCriasPendientes.setVisibility(android.view.View.VISIBLE);
                }
            });
        }).start();
    }

    private void mostrarOpcionesCria(EventoReproductivo evento) {
        String[] opciones = {
                "Identificar cría",
                "Marcar muerte antes de identificar"
        };

        new AlertDialog.Builder(this)
                .setTitle("Cría pendiente")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        abrirIdentificarCria(evento);
                    } else if (which == 1) {
                        mostrarDialogoMarcarMuerte(evento);
                    }
                })
                .show();
    }

    private void abrirIdentificarCria(EventoReproductivo evento) {
        android.content.Intent intent = new android.content.Intent(
                this,
                IdentificarCriaActivity.class
        );

        intent.putExtra("id_evento", evento.getId());
        intent.putExtra("id_madre", evento.getIdMadre());
        intent.putExtra("crotal_madre", evento.getCrotalMadre());
        intent.putExtra("id_explotacion_uuid", evento.getIdExplotacionUuid());
        intent.putExtra("fecha_nacimiento", evento.getFechaEvento());
        intent.putExtra("sexo_estimado", evento.getSexoEstimado());

        startActivity(intent);
    }

    private void mostrarDialogoMarcarMuerte(EventoReproductivo evento) {
        EditText etObservaciones = new EditText(this);
        etObservaciones.setHint("Observaciones");
        etObservaciones.setMinLines(3);
        etObservaciones.setText(evento.getObservaciones() != null ? evento.getObservaciones() : "");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Marcar muerte")
                .setMessage("La cría dejará de aparecer como pendiente y no se creará animal.")
                .setView(etObservaciones)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Marcar muerte", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(getColor(android.R.color.holo_red_dark));

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                boolean ok = repository.marcarCriaMuertaAntesIdentificar(
                        evento.getId(),
                        etObservaciones.getText().toString().trim(),
                        FechaUtils.ahoraIso()
                );

                if (ok) {
                    Toast.makeText(this, "Cría marcada como muerta", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    cargarPendientes();
                } else {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPendientes();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
