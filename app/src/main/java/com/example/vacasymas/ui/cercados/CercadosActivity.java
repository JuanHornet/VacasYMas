package com.example.vacasymas.ui.cercados;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.CensoCercado;
import com.example.vacasymas.data.models.CercadoConCenso;
import com.example.vacasymas.session.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CercadosActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private RecyclerView recycler;
    private CercadoOperativoAdapter adapter;
    private List<CercadoConCenso> lista = new ArrayList<>();

    private String idExplotacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cercados);

        dbHelper = new DBHelper(this);
        idExplotacion = SessionManager.getIdExplotacionSeleccionada(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbarCercadosOperativo);
        recycler = findViewById(R.id.recyclerCercadosOperativo);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Cercados");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (idExplotacion == null || idExplotacion.trim().isEmpty()) {
            Toast.makeText(this, "No hay explotación seleccionada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        adapter = new CercadoOperativoAdapter(lista, new CercadoOperativoAdapter.OnCercadoOperativoClickListener() {
            @Override
            public void onRegistrarCenso(CercadoConCenso item) {
                mostrarDialogoCenso(item);
            }

            @Override
            public void onVerHistorial(CercadoConCenso item) {
                Toast.makeText(
                        CercadosActivity.this,
                        "Historial de " + item.getCercado().getNombre(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        cargarCercados();
    }

    private void cargarCercados() {
        lista.clear();
        lista.addAll(dbHelper.obtenerCercadosConUltimoCenso(idExplotacion));
        adapter.notifyDataSetChanged();
    }

    private void mostrarDialogoCenso(CercadoConCenso item) {
        View view = getLayoutInflater().inflate(R.layout.dialog_censo_cercado, null);

        EditText etVacas = view.findViewById(R.id.etVacas);
        EditText etTerneros = view.findViewById(R.id.etTerneros);
        EditText etToros = view.findViewById(R.id.etToros);
        EditText etNovillas = view.findViewById(R.id.etNovillas);
        EditText etObservaciones = view.findViewById(R.id.etObservacionesCenso);

        CensoCercado ultimo = item.getUltimoCenso();

        if (ultimo != null) {
            etVacas.setText(String.valueOf(ultimo.getVacas()));
            etTerneros.setText(String.valueOf(ultimo.getTerneros()));
            etToros.setText(String.valueOf(ultimo.getToros()));
            etNovillas.setText(String.valueOf(ultimo.getNovillas()));

            if (ultimo.getObservaciones() != null) {
                etObservaciones.setText(ultimo.getObservaciones());
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Conteo: " + item.getCercado().getNombre())
                .setView(view)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

                int vacas = leerEntero(etVacas);
                int terneros = leerEntero(etTerneros);
                int toros = leerEntero(etToros);
                int novillas = leerEntero(etNovillas);

                CensoCercado censo = new CensoCercado();
                censo.setId(UUID.randomUUID().toString());
                censo.setIdCercado(item.getCercado().getId());
                censo.setIdExplotacionUuid(idExplotacion);
                censo.setFecha(FechaUtils.hoyIso());
                censo.setVacas(vacas);
                censo.setTerneros(terneros);
                censo.setToros(toros);
                censo.setNovillas(novillas);
                censo.setObservaciones(etObservaciones.getText().toString().trim());
                censo.setSincronizado(0);
                censo.setEliminado(0);
                censo.setFechaActualizacion(FechaUtils.ahoraIso());

                dbHelper.insertarOActualizarCensoCercado(censo);

                Toast.makeText(this, "Conteo guardado", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
                cargarCercados();
            });
        });

        dialog.show();
    }

    private int leerEntero(EditText editText) {
        String valor = editText.getText().toString().trim();

        if (valor.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}