package com.example.vacasymas.ui.reproduccion;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vacasymas.R;
import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.base.ParideraUtils;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.Animal;
import com.example.vacasymas.data.repo.EventoReproductivoRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.UUID;

public class IdentificarCriaActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvMadre;
    private EditText etCrotal, etCapa;
    private Spinner spSexo, spRaza;
    private MaterialButton btnGuardar, btnCancelar;

    private String idEvento;
    private String idMadre;
    private String crotalMadre;
    private String idExplotacionUuid;
    private String fechaNacimiento;
    private String sexoEstimado;

    private EventoReproductivoRepository repository;

    private final String[] sexos = {
            "Macho",
            "Hembra"
    };

    private final String[] razas = {
            "Cruzada",
            "Retinta",
            "Limousine"

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identificar_cria);

        recogerIntent();
        repository = new EventoReproductivoRepository(new DBHelper(this));

        initViews();
        initToolbar();
        configurarSpinners();
        configurarEventos();
    }

    private void recogerIntent() {
        idEvento = getIntent().getStringExtra("id_evento");
        idMadre = getIntent().getStringExtra("id_madre");
        crotalMadre = getIntent().getStringExtra("crotal_madre");
        idExplotacionUuid = getIntent().getStringExtra("id_explotacion_uuid");
        fechaNacimiento = getIntent().getStringExtra("fecha_nacimiento");
        sexoEstimado = getIntent().getStringExtra("sexo_estimado");
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvMadre = findViewById(R.id.tvMadre);
        etCrotal = findViewById(R.id.etCrotal);
        etCapa = findViewById(R.id.etCapa);
        spSexo = findViewById(R.id.spSexo);
        spRaza = findViewById(R.id.spRaza);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        tvMadre.setText("Madre: " + valorSeguro(crotalMadre));
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void configurarSpinners() {
        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                sexos
        );
        adapterSexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSexo.setAdapter(adapterSexo);

        if ("Hembra".equalsIgnoreCase(sexoEstimado)) {
            spSexo.setSelection(1);
        } else {
            spSexo.setSelection(0);
        }

        ArrayAdapter<String> adapterRaza = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                razas
        );
        adapterRaza.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRaza.setAdapter(adapterRaza);
    }

    private void configurarEventos() {
        btnCancelar.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> guardarCria());
    }

    private void guardarCria() {
        String crotal = etCrotal.getText().toString().trim().toUpperCase();
        String capa = etCapa.getText().toString().trim();
        String sexo = spSexo.getSelectedItem().toString();
        String raza = spRaza.getSelectedItem().toString();

        if (idEvento == null || idEvento.trim().isEmpty()) {
            Toast.makeText(this, "No se ha recibido el evento", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idMadre == null || idMadre.trim().isEmpty()) {
            Toast.makeText(this, "No se ha recibido la madre", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idExplotacionUuid == null || idExplotacionUuid.trim().isEmpty()) {
            Toast.makeText(this, "No se ha recibido la explotación", Toast.LENGTH_SHORT).show();
            return;
        }

        if (crotal.isEmpty()) {
            Toast.makeText(this, "Introduce el crotal", Toast.LENGTH_SHORT).show();
            return;
        }

        Animal cria = new Animal();
        cria.setId(UUID.randomUUID().toString());
        cria.setIdSharepoint(null);
        cria.setCrotal(crotal);
        cria.setIdExplotacionUuid(idExplotacionUuid);
        cria.setEstatus(obtenerEstatusCria(sexo));
        cria.setFechaNacimiento(fechaNacimiento);
        cria.setRaza(raza);
        cria.setSexo(sexo);
        cria.setCrotalMadre(crotalMadre);
        cria.setCapa(capa);
        cria.setCercado(null);
        cria.setIdCercadoHistorico(null);
        cria.setParidera(
                ParideraUtils.calcularParidera(cria.getFechaNacimiento())
        );
        cria.setAltaGestionada("No");
        cria.setStatecode("1");
        cria.setFactor(null);
        cria.setFechaBajaExplotacion(null);
        cria.setCausaAlta("Nacimiento");
        cria.setExplotacionNacimiento(null);
        cria.setCheckParidera(null);
        cria.setIschosen(null);
        cria.setEstadoReproductivo("nada");
        cria.setCrotalIzquierdoPresente(true);
        cria.setCrotalDerechoPresente(true);
        cria.setFechaActualizacion(FechaUtils.ahoraIso());
        cria.setSincronizado(0);
        cria.setEliminado(0);
        cria.setFechaEliminado(null);

        boolean ok = repository.identificarCria(
                idEvento,
                cria,
                FechaUtils.ahoraIso()
        );

        if (ok) {
            Toast.makeText(this, "Cría identificada", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al identificar la cría", Toast.LENGTH_SHORT).show();
        }
    }

    private int obtenerEstatusCria(String sexo) {
        if ("Hembra".equalsIgnoreCase(sexo)) {
            return 10002;
        }

        return 10001;
    }

    private String valorSeguro(String valor) {
        return valor == null || valor.trim().isEmpty() ? "-" : valor;
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
