package com.example.vacasymas.ui.reproduccion;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vacasymas.R;
import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.EventoReproductivo;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class RegistrarPartoActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvMadre;
    private EditText etFecha, etObservaciones;
    private Spinner spSexoEstimado;
    private Spinner spTipoEvento, spResultadoCria;
    private MaterialButton btnGuardar, btnCancelar;;

    private String idMadre;
    private String crotalMadre;
    private String idExplotacionUuid;
    private String fechaIso;

    private DBHelper dbHelper;

    private final String[] tiposEvento = {
            "Parto",
            "Aborto"
    };

    private final String[] resultadosParto = {
            "Cría viva pendiente de crotal",
            "Cría nacida muerta",
            "Cría viva murió antes de identificar"
    };

    private final String[] sexos = {
            "Macho",
            "Hembra"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_parto);

        idMadre = getIntent().getStringExtra("id_madre");
        crotalMadre = getIntent().getStringExtra("crotal_madre");
        idExplotacionUuid = getIntent().getStringExtra("id_explotacion_uuid");

        dbHelper = new DBHelper(this);

        initViews();
        initToolbar();
        configurarSpinners();
        configurarFechaActual();
        configurarEventos();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvMadre = findViewById(R.id.tvMadre);

        etFecha = findViewById(R.id.etFecha);
        spSexoEstimado = findViewById(R.id.spSexoEstimado);
        etObservaciones = findViewById(R.id.etObservaciones);
        btnCancelar = findViewById(R.id.btnCancelar);

        spTipoEvento = findViewById(R.id.spTipoEvento);
        spResultadoCria = findViewById(R.id.spResultadoCria);

        btnGuardar = findViewById(R.id.btnGuardar);

        tvMadre.setText(crotalMadre != null ? crotalMadre : "-");
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void configurarSpinners() {
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tiposEvento
        );
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoEvento.setAdapter(adapterTipo);

        ArrayAdapter<String> adapterResultado = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                resultadosParto
        );

        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                sexos
        );

        adapterSexo.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spSexoEstimado.setAdapter(adapterSexo);
        adapterResultado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spResultadoCria.setAdapter(adapterResultado);
    }

    private void configurarFechaActual() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        fechaIso = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
        etFecha.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month, year));
    }

    private void configurarEventos() {
        etFecha.setOnClickListener(v -> mostrarDatePicker());

        spTipoEvento.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                actualizarVisibilidadSegunTipo();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {

            }
        });

        btnGuardar.setOnClickListener(v -> guardarEvento());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (datePicker, year, month, dayOfMonth) -> {
                    int mes = month + 1;

                    fechaIso = String.format(
                            Locale.getDefault(),
                            "%04d-%02d-%02d",
                            year,
                            mes,
                            dayOfMonth
                    );

                    etFecha.setText(String.format(
                            Locale.getDefault(),
                            "%02d/%02d/%04d",
                            dayOfMonth,
                            mes,
                            year
                    ));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void guardarEvento() {
        if (idMadre == null || idMadre.trim().isEmpty()) {
            Toast.makeText(this, "No se ha recibido la madre", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idExplotacionUuid == null || idExplotacionUuid.trim().isEmpty()) {
            Toast.makeText(this, "No se ha recibido la explotación", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fechaIso == null || fechaIso.trim().isEmpty()) {
            Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        String tipoSeleccionado = spTipoEvento.getSelectedItem().toString();

        EventoReproductivo evento = new EventoReproductivo();
        evento.setId(UUID.randomUUID().toString());
        evento.setIdMadre(idMadre);
        evento.setIdCria(null);
        evento.setIdExplotacionUuid(idExplotacionUuid);
        evento.setFechaEvento(fechaIso);
        evento.setCriaIdentificada(0);
        evento.setCercado(null);
        evento.setObservaciones(etObservaciones.getText().toString().trim());
        evento.setFechaActualizacion(FechaUtils.ahoraIso());
        evento.setSincronizado(0);
        evento.setEliminado(0);
        evento.setFechaEliminado(null);

        if ("Aborto".equals(tipoSeleccionado)) {
            evento.setTipoEvento("ABORTO");
            evento.setResultadoCria(null);
            evento.setSexoEstimado(null);
            evento.setRazaEstimada(null);
            evento.setCapaEstimada(null);
        } else {
            evento.setTipoEvento(EventoReproductivo.TIPO_PARTO);
            evento.setResultadoCria(obtenerCodigoResultadoParto());
            evento.setSexoEstimado(spSexoEstimado.getSelectedItem().toString());
            evento.setRazaEstimada(null);
            evento.setCapaEstimada(null);
        }

        boolean ok = dbHelper.registrarEventoReproductivoSinCria(evento);

        if (ok) {
            Toast.makeText(this, "Evento reproductivo registrado", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al registrar evento", Toast.LENGTH_SHORT).show();
        }
    }


    private String obtenerCodigoResultadoParto() {
        String resultado = spResultadoCria.getSelectedItem().toString();

        switch (resultado) {
            case "Cría nacida muerta":
                return EventoReproductivo.NACIDA_MUERTA;

            case "Cría viva murió antes de identificar":
                return EventoReproductivo.MUERE_ANTES_IDENTIFICAR;

            case "Cría viva pendiente de crotal":
            default:
                return EventoReproductivo.VIVA_PENDIENTE_IDENTIFICAR;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void actualizarVisibilidadSegunTipo() {
        String tipo = spTipoEvento.getSelectedItem().toString();

        if ("Aborto".equals(tipo)) {
            spResultadoCria.setVisibility(View.GONE);
            spSexoEstimado.setVisibility(View.GONE);
        } else {
            spResultadoCria.setVisibility(View.VISIBLE);
            spSexoEstimado.setVisibility(View.VISIBLE);
        }
    }
}
