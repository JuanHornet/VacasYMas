package com.example.vacasymas.ui.crotales;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.example.vacasymas.data.models.CrotalDisponible;
import com.example.vacasymas.data.repo.CrotalDisponibleRepository;
import com.example.vacasymas.session.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CrotalesActivity extends AppCompatActivity {

    private TextView tvExplotacionCrotales;
    private TextView tvCrotalesDisponibles;
    private TextView tvCrotalesUsados;
    private TextView tvCrotalesAnulados;
    private TextView tvSiguienteCrotal;
    private RadioGroup rgFiltroCrotales;
    private RecyclerView rvCrotales;
    private MaterialButton btnAnadirRangoCrotales;

    private CrotalDisponibleRepository repository;
    private CrotalDisponibleAdapter adapter;

    private String idExplotacion;
    private String nombreExplotacion;
    private String estadoSeleccionado = CrotalDisponible.DISPONIBLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crotales);

        repository = new CrotalDisponibleRepository(new DBHelper(this));

        idExplotacion = SessionManager.getIdExplotacionSeleccionada(this);
        nombreExplotacion = SessionManager.getNombreExplotacionSeleccionada(this);

        initViews();
        configurarToolbar();
        configurarRecycler();
        configurarFiltros();
        configurarBotones();

        cargarPantalla();
    }

    private void initViews() {
        tvExplotacionCrotales = findViewById(R.id.tvExplotacionCrotales);
        tvCrotalesDisponibles = findViewById(R.id.tvCrotalesDisponibles);
        tvCrotalesUsados = findViewById(R.id.tvCrotalesUsados);
        tvCrotalesAnulados = findViewById(R.id.tvCrotalesAnulados);
        tvSiguienteCrotal = findViewById(R.id.tvSiguienteCrotal);
        rgFiltroCrotales = findViewById(R.id.rgFiltroCrotales);
        rvCrotales = findViewById(R.id.rvCrotales);
        btnAnadirRangoCrotales = findViewById(R.id.btnAnadirRangoCrotales);
    }

    private void configurarToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbarCrotales);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void configurarRecycler() {
        adapter = new CrotalDisponibleAdapter();
        rvCrotales.setLayoutManager(new LinearLayoutManager(this));
        rvCrotales.setAdapter(adapter);

        adapter.setOnCrotalLongClickListener(this::mostrarOpcionesCrotal);
    }

    private void configurarFiltros() {
        rgFiltroCrotales.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDisponibles) {
                estadoSeleccionado = CrotalDisponible.DISPONIBLE;
            } else if (checkedId == R.id.rbUsados) {
                estadoSeleccionado = CrotalDisponible.USADO;
            } else if (checkedId == R.id.rbAnulados) {
                estadoSeleccionado = CrotalDisponible.ANULADO;
            }

            cargarListado();
        });
    }

    private void configurarBotones() {
        btnAnadirRangoCrotales.setOnClickListener(v -> mostrarDialogoAnadirRango());
    }

    private void cargarPantalla() {
        if (idExplotacion == null || idExplotacion.trim().isEmpty()) {
            Toast.makeText(this, "No hay explotación seleccionada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvExplotacionCrotales.setText(
                nombreExplotacion != null ? nombreExplotacion : "Explotación seleccionada"
        );

        cargarResumen();
        cargarSiguienteCrotal();
        cargarListado();
    }

    private void cargarResumen() {
        int disponibles = repository.contarCrotalesPorEstado(idExplotacion, CrotalDisponible.DISPONIBLE);
        int usados = repository.contarCrotalesPorEstado(idExplotacion, CrotalDisponible.USADO);
        int anulados = repository.contarCrotalesPorEstado(idExplotacion, CrotalDisponible.ANULADO);

        tvCrotalesDisponibles.setText("Disponibles\n" + disponibles);
        tvCrotalesUsados.setText("Usados\n" + usados);
        tvCrotalesAnulados.setText("Anulados\n" + anulados);
    }

    private void cargarSiguienteCrotal() {
        String siguiente = repository.obtenerSiguienteCrotalDisponible(idExplotacion);

        if (siguiente == null || siguiente.trim().isEmpty()) {
            tvSiguienteCrotal.setText("Sin crotales disponibles");
        } else {
            tvSiguienteCrotal.setText(siguiente);
        }
    }

    private void cargarListado() {
        List<CrotalDisponible> lista =
                repository.obtenerCrotalesPorEstado(idExplotacion, estadoSeleccionado);

        adapter.actualizarLista(lista);
    }

    private void mostrarOpcionesCrotal(CrotalDisponible crotal) {
        if (CrotalDisponible.DISPONIBLE.equals(crotal.getEstado())) {
            new AlertDialog.Builder(this)
                    .setTitle(crotal.getCrotal())
                    .setItems(new String[]{"Anular crotal"}, (dialog, which) -> anularCrotal(crotal))
                    .show();

        } else if (CrotalDisponible.ANULADO.equals(crotal.getEstado())) {
            new AlertDialog.Builder(this)
                    .setTitle(crotal.getCrotal())
                    .setItems(new String[]{"Restaurar a disponible"}, (dialog, which) -> restaurarCrotal(crotal))
                    .show();

        } else {
            Toast.makeText(this, "Crotal ya usado", Toast.LENGTH_SHORT).show();
        }
    }

    private void anularCrotal(CrotalDisponible crotal) {
        String ahora = FechaUtils.ahoraIso();

        boolean ok = repository.anularCrotal(
                crotal.getCrotal(),
                "Anulado manualmente",
                ahora
        );

        if (ok) {
            Toast.makeText(this, "Crotal anulado", Toast.LENGTH_SHORT).show();
            cargarPantalla();
        } else {
            Toast.makeText(this, "No se pudo anular", Toast.LENGTH_SHORT).show();
        }
    }

    private void restaurarCrotal(CrotalDisponible crotal) {
        String ahora = FechaUtils.ahoraIso();

        boolean ok = repository.restaurarCrotalDisponible(
                crotal.getCrotal(),
                ahora
        );

        if (ok) {
            Toast.makeText(this, "Crotal restaurado", Toast.LENGTH_SHORT).show();
            cargarPantalla();
        } else {
            Toast.makeText(this, "No se pudo restaurar", Toast.LENGTH_SHORT).show();
        }
    }
    private void mostrarDialogoAnadirRango() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, 0);

        final EditText etPrimerCrotal = new EditText(this);
        etPrimerCrotal.setHint("Primer crotal");
        etPrimerCrotal.setSingleLine(true);
        etPrimerCrotal.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        final EditText etUltimoCrotal = new EditText(this);
        etUltimoCrotal.setHint("Último crotal");
        etUltimoCrotal.setSingleLine(true);
        etUltimoCrotal.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        final EditText etObservaciones = new EditText(this);
        etObservaciones.setHint("Observaciones");
        etObservaciones.setMinLines(2);
        etObservaciones.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        layout.addView(etPrimerCrotal);
        layout.addView(etUltimoCrotal);
        layout.addView(etObservaciones);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Añadir rango de crotales")
                .setView(layout)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String primerCrotal = etPrimerCrotal.getText().toString().trim().toUpperCase();
                String ultimoCrotal = etUltimoCrotal.getText().toString().trim().toUpperCase();
                String observaciones = etObservaciones.getText().toString().trim();

                if (primerCrotal.isEmpty() || ultimoCrotal.isEmpty()) {
                    Toast.makeText(this, "Introduce primer y último crotal", Toast.LENGTH_SHORT).show();
                    return;
                }

                int cantidad = calcularCantidad(primerCrotal, ultimoCrotal);

                if (cantidad <= 0) {
                    Toast.makeText(this, "Rango no válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cantidad > 500) {
                    Toast.makeText(this, "Máximo 500 crotales por carga", Toast.LENGTH_LONG).show();
                    return;
                }

                String ahora = FechaUtils.ahoraIso();
                String hoy = ahora.length() >= 10 ? ahora.substring(0, 10) : ahora;

                boolean ok = repository.insertarRangoCrotales(
                        primerCrotal,
                        ultimoCrotal,
                        idExplotacion,
                        hoy,
                        observaciones,
                        ahora
                );

                if (ok) {
                    Toast.makeText(this, "Crotales creados: " + cantidad, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    cargarPantalla();
                } else {
                    Toast.makeText(this, "No se pudieron crear los crotales", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private int calcularCantidad(String primerCrotal, String ultimoCrotal) {
        try {
            String prefijoInicio = primerCrotal.replaceAll("\\d+$", "");
            String numeroInicioStr = primerCrotal.substring(prefijoInicio.length());

            String prefijoFin = ultimoCrotal.replaceAll("\\d+$", "");
            String numeroFinStr = ultimoCrotal.substring(prefijoFin.length());

            if (!prefijoInicio.equals(prefijoFin)) {
                return -1;
            }

            long inicio = Long.parseLong(numeroInicioStr);
            long fin = Long.parseLong(numeroFinStr);

            if (fin < inicio) {
                return -1;
            }

            long cantidad = fin - inicio + 1;

            if (cantidad > Integer.MAX_VALUE) {
                return -1;
            }

            return (int) cantidad;

        } catch (Exception e) {
            return -1;
        }
    }
}
