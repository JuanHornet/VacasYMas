package com.example.vacasymas.ui.listados;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vacasymas.R;
import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.ListaAnimal;
import com.example.vacasymas.session.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;

public class CrearListaManualActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputEditText etNombreLista;
    private Button btnCrearLista;

    private DBHelper dbHelper;
    private String idExplotacionUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_lista_manual);

        dbHelper = new DBHelper(this);
        idExplotacionUuid = SessionManager.getIdExplotacionSeleccionada(this);

        toolbar = findViewById(R.id.toolbarCrearLista);
        etNombreLista = findViewById(R.id.etNombreLista);
        btnCrearLista = findViewById(R.id.btnCrearLista);

        toolbar.setNavigationOnClickListener(v -> finish());

        btnCrearLista.setOnClickListener(v -> crearLista());
    }

    private void crearLista() {
        String nombre = etNombreLista.getText() != null
                ? etNombreLista.getText().toString().trim()
                : "";

        if (nombre.isEmpty()) {
            etNombreLista.setError("Introduce un nombre");
            return;
        }

        String idLista = UUID.randomUUID().toString();
        String ahora = FechaUtils.ahoraIso();

        ListaAnimal lista = new ListaAnimal();
        lista.setId(idLista);
        lista.setIdExplotacionUuid(idExplotacionUuid);
        lista.setNombre(nombre);
        lista.setTipo("");
        lista.setObservaciones("");
        lista.setFechaCreacion(ahora);
        lista.setSincronizado(0);
        lista.setEliminado(0);
        lista.setFechaActualizacion(ahora);
        lista.setFechaEliminado(null);

        boolean ok = dbHelper.guardarListaAnimal(lista);

        if (!ok) {
            Toast.makeText(this, "Error al crear la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, DetalleListaManualActivity.class);
        intent.putExtra("id_lista", idLista);
        intent.putExtra("nombre_lista", nombre);
        startActivity(intent);

        finish();
    }
}
