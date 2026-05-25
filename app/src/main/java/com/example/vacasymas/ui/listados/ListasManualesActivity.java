package com.example.vacasymas.ui.listados;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.ListaAnimal;
import com.example.vacasymas.session.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class ListasManualesActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView rvListas;

    private DBHelper dbHelper;
    private ListaManualAdapter adapter;

    private String idExplotacionUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listas_manuales);

        dbHelper = new DBHelper(this);

        idExplotacionUuid = SessionManager.getIdExplotacionSeleccionada(this);

        initViews();
        configurarToolbar();
        configurarRecycler();

        }

    @Override
    protected void onResume() {
        super.onResume();
        cargarListas();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarListas);
        rvListas = findViewById(R.id.rvListas);
    }

    private void configurarToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void configurarRecycler() {

        adapter = new ListaManualAdapter(new ListaManualAdapter.OnListaClickListener() {
            @Override
            public void onListaClick(ListaAnimal lista) {
                Intent intent = new Intent(ListasManualesActivity.this, DetalleListaManualActivity.class);
                intent.putExtra("id_lista", lista.getId());
                intent.putExtra("nombre_lista", lista.getNombre());
                startActivity(intent);
            }

            @Override
            public void onEditarClick(ListaAnimal lista) {
                mostrarDialogoEditarLista(lista);
            }

            @Override
            public void onEliminarClick(ListaAnimal lista) {
                confirmarEliminarLista(lista);
            }
        });

        rvListas.setLayoutManager(new LinearLayoutManager(this));
        rvListas.setAdapter(adapter);
    }

    private void cargarListas() {

        List<ListaAnimal> listas =
                dbHelper.obtenerListasAnimales(idExplotacionUuid);

        adapter.actualizar(listas);
    }

    private void mostrarDialogoEditarLista(ListaAnimal lista) {
        final EditText input = new EditText(this);
        input.setText(lista.getNombre());
        input.setSelectAllOnFocus(true);

        new AlertDialog.Builder(this)
                .setTitle("Editar nombre")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevoNombre = input.getText().toString().trim();

                    if (nuevoNombre.isEmpty()) {
                        Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean ok = dbHelper.actualizarNombreListaAnimal(
                            lista.getId(),
                            nuevoNombre,
                            FechaUtils.ahoraIso()
                    );

                    if (ok) cargarListas();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminarLista(ListaAnimal lista) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar lista")
                .setMessage("¿Eliminar la lista \"" + lista.getNombre() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    boolean ok = dbHelper.eliminarListaAnimal(
                            lista.getId(),
                            FechaUtils.ahoraIso()
                    );

                    if (ok) cargarListas();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
