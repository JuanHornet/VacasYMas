package com.example.vacasymas.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vacasymas.R;
import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.base.ParideraUtils;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.Explotacion;
import com.example.vacasymas.data.repo.AnimalRepository;
import com.example.vacasymas.data.repo.EventoReproductivoRepository;
import com.example.vacasymas.data.repo.ExplotacionRepository;
import com.example.vacasymas.session.SessionManager;
import com.example.vacasymas.sync.SincronizadorGeneral;
import com.example.vacasymas.ui.animales.BuscarAnimalActivity;
import com.example.vacasymas.ui.crotales.CrotalesActivity;
import com.example.vacasymas.ui.diagnostico.DiagnosticoGestacionActivity;
import com.example.vacasymas.ui.listados.ListadosActivity;
import com.example.vacasymas.ui.pesos.PesosActivity;
import com.example.vacasymas.ui.reproduccion.CriasPendientesActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;


public class DashboardActivity extends AppCompatActivity {

    private MaterialAutoCompleteTextView actvExplotaciones;

    private Spinner spinnerExplotaciones;
    private DBHelper dbHelper;
    private java.util.List<Explotacion> listaExplotaciones;

    private SessionManager sessionManager;

    private TextView txtTotalVacas;
    private TextView txtTotalBecerros;
    private TextView txtTotalToros;
    private TextView txtTotalNovillos;
    private TextView txtTotalAnimalesExplotacion;

    private TextView txtProgresoVacas;
    private TextView txtPorcentajeVacas;
    private ProgressBar progressVacas;

    private TextView tvBadgeCriasPendientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        String idExplotacion = SessionManager.getIdExplotacionSeleccionada(this);
        txtTotalVacas = findViewById(R.id.txtTotalVacas);
        txtTotalBecerros = findViewById(R.id.txtTotalBecerros);
        txtTotalToros = findViewById(R.id.txtTotalToros);
        txtTotalNovillos = findViewById(R.id.txtTotalNovillos);
        txtTotalAnimalesExplotacion = findViewById(R.id.txtTotalAnimalesExplotacion);
        txtProgresoVacas = findViewById(R.id.txtProgresoVacas);
        txtPorcentajeVacas = findViewById(R.id.txtPorcentajeVacas);
        progressVacas = findViewById(R.id.progressVacas);
        tvBadgeCriasPendientes = findViewById(R.id.tvBadgeCriasPendientes);

        MaterialToolbar toolbar = findViewById(R.id.toolbarDashboard);
        setSupportActionBar(toolbar);

        dbHelper = new DBHelper(this);
        actvExplotaciones = findViewById(R.id.actvExplotaciones);

        new Thread(() -> {
            boolean ok = new SincronizadorGeneral(DashboardActivity.this).sincronizarTodo();

            runOnUiThread(() -> {
                cargarExplotaciones();


                if (!ok) {
                    Toast.makeText(DashboardActivity.this,
                            "Alguna sincronización no se completó correctamente",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
        actvExplotaciones.setOnItemClickListener((parent, view, position, id) -> {
            if (listaExplotaciones != null && !listaExplotaciones.isEmpty()) {
                Explotacion explotacionSeleccionada = listaExplotaciones.get(position);

                SessionManager.guardarExplotacionSeleccionada(
                        DashboardActivity.this,
                        explotacionSeleccionada.getId(),
                        explotacionSeleccionada.getNombre()
                );
                cargarResumenAnimales();

            }
        });

        findViewById(R.id.btnBuscarAnimal).setOnClickListener(v -> irABuscarAnimal());
        findViewById(R.id.btnGestacion).setOnClickListener(v -> irADiagnosticoGestacion());
        findViewById(R.id.btnPesos).setOnClickListener(v -> irAPesos());
        findViewById(R.id.btnCriasPendientes).setOnClickListener(v -> irACriasPendientes());
        findViewById(R.id.btnCrotales).setOnClickListener(v -> irACrotales());
        findViewById(R.id.btnListados).setOnClickListener(v -> irAListados());


    }

    private void cargarExplotaciones() {
        listaExplotaciones = dbHelper.obtenerListaExplotacionesActivasPorUsuario(
                SessionManager.getIdUsuarioLocal()
        );

        ArrayAdapter<Explotacion> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                listaExplotaciones
        );

        actvExplotaciones.setAdapter(adapter);

        if (listaExplotaciones == null || listaExplotaciones.isEmpty()) {
            actvExplotaciones.setText("", false);
            limpiarResumenAnimales();
            return;
        }

        String idGuardado = SessionManager.getIdExplotacionSeleccionada(this);

        if (idGuardado == null || idGuardado.trim().isEmpty()) {
            Explotacion primera = listaExplotaciones.get(0);

            SessionManager.guardarExplotacionSeleccionada(
                    this,
                    primera.getId(),
                    primera.getNombre()
            );

            actvExplotaciones.setText(primera.getNombre(), false);
            cargarResumenAnimales();
            cargarProgresoParidera();
            return;
        }

        restaurarExplotacionSeleccionada();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_explotacion) {
            mostrarDialogoCrearExplotacion();
            return true;
        } else if (item.getItemId() == R.id.menu_edit_explotacion) {
            mostrarDialogoEditarExplotacion();
            return true;
        } else if (item.getItemId() == R.id.menu_delete_explotacion) {
            mostrarDialogoEliminarExplotacion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void mostrarDialogoCrearExplotacion() {

        EditText input = new EditText(this);
        input.setHint("Nombre de la explotación");

        new AlertDialog.Builder(this)
                .setTitle("Nueva explotación")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {

                    String nombre = input.getText().toString().trim();

                    if (nombre.isEmpty()) {
                        Toast.makeText(this, "Introduce un nombre", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    guardarExplotacion(nombre);

                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void guardarExplotacion(String nombre) {

        Explotacion e = new Explotacion();

        e.setId(java.util.UUID.randomUUID().toString());
        e.setNombre(nombre);
        e.setIdUsuario(SessionManager.getIdUsuarioLocal());
        e.setFechaActualizacion(FechaUtils.ahoraIso());
        e.setSincronizado(0); // 🔴 IMPORTANTE: empieza como no sincronizado
        e.setEliminado(0);

        dbHelper.insertarExplotacionLocal(e);

        // ✅ Guardar como seleccionada
        SessionManager.guardarExplotacionSeleccionada(
                this,
                e.getId(),
                e.getNombre()
        );

        // 🔥 AQUÍ VA LA SUBIDA
        ExplotacionRepository repo = new ExplotacionRepository(dbHelper);

        repo.subirExplotacionesNoSincronizadas(new ExplotacionRepository.SyncCallback() {
            @Override
            public void onSuccess() {
                Log.d("Dashboard", "Subida OK");
            }

            @Override
            public void onError(String error) {
                Log.e("Dashboard", error);
            }
        });

        Toast.makeText(this, "Explotación creada", Toast.LENGTH_SHORT).show();

        cargarExplotaciones(); // 🔄 refrescar dropdown
    }

    private void restaurarExplotacionSeleccionada() {
        String idGuardado = SessionManager.getIdExplotacionSeleccionada(this);

        if (idGuardado == null || listaExplotaciones == null || listaExplotaciones.isEmpty()) {
            limpiarResumenAnimales();
            return;
        }

        for (Explotacion e : listaExplotaciones) {
            if (idGuardado.equals(e.getId())) {
                actvExplotaciones.setText(e.getNombre(), false);
                cargarResumenAnimales();

                return;
            }
        }

        limpiarResumenAnimales();
    }

    private Explotacion obtenerExplotacionSeleccionada() {
        String idSeleccionado = SessionManager.getIdExplotacionSeleccionada(this);

        if (idSeleccionado == null || listaExplotaciones == null || listaExplotaciones.isEmpty()) {
            return null;
        }

        for (Explotacion e : listaExplotaciones) {
            if (idSeleccionado.equals(e.getId())) {
                return e;
            }
        }

        return null;
    }

    private void mostrarDialogoEditarExplotacion() {
        Explotacion explotacion = obtenerExplotacionSeleccionada();

        if (explotacion == null) {
            Toast.makeText(this, "No hay ninguna explotación seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }

        final EditText input = new EditText(DashboardActivity.this);
        input.setHint("Nombre de la explotación");
        input.setText(explotacion.getNombre());
        input.setSelection(input.getText().length());

        new AlertDialog.Builder(DashboardActivity.this)
                .setTitle("Editar explotación")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevoNombre = input.getText().toString().trim();

                    if (nuevoNombre.isEmpty()) {
                        Toast.makeText(DashboardActivity.this, "Introduce un nombre", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    actualizarExplotacion(explotacion, nuevoNombre);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void actualizarExplotacion(Explotacion explotacion, String nuevoNombre) {
        explotacion.setNombre(nuevoNombre);
        explotacion.setFechaActualizacion(FechaUtils.ahoraIso());
        explotacion.setSincronizado(0);

        dbHelper.insertarOActualizarExplotacion(explotacion);

        SessionManager.guardarExplotacionSeleccionada(
                this,
                explotacion.getId(),
                explotacion.getNombre()
        );

        cargarExplotaciones();

        ExplotacionRepository repo = new ExplotacionRepository(dbHelper);
        repo.subirExplotacionesNoSincronizadas(new ExplotacionRepository.SyncCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() ->
                        Toast.makeText(DashboardActivity.this, "Explotación actualizada", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        Toast.makeText(DashboardActivity.this, "Actualizada en local. Pendiente de sincronizar.", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void mostrarDialogoEliminarExplotacion() {
        Explotacion explotacion = obtenerExplotacionSeleccionada();

        if (explotacion == null) {
            Toast.makeText(this, "No hay ninguna explotación seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(DashboardActivity.this)
                .setTitle("Eliminar explotación")
                .setMessage("¿Seguro que quieres eliminar la explotación \"" + explotacion.getNombre() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarExplotacion(explotacion))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarExplotacion(Explotacion explotacion) {
        explotacion.setEliminado(1);
        explotacion.setFechaEliminado(FechaUtils.ahoraIso());
        explotacion.setFechaActualizacion(FechaUtils.ahoraIso());
        explotacion.setSincronizado(0);

        dbHelper.insertarOActualizarExplotacion(explotacion);

        SessionManager.limpiarExplotacionSeleccionada(this);

        cargarExplotaciones();

        if (listaExplotaciones != null && !listaExplotaciones.isEmpty()) {
            Explotacion primera = listaExplotaciones.get(0);
            SessionManager.guardarExplotacionSeleccionada(
                    this,
                    primera.getId(),
                    primera.getNombre()
            );
            actvExplotaciones.setText(primera.getNombre(), false);
        } else {
            actvExplotaciones.setText("", false);
        }

        ExplotacionRepository repo = new ExplotacionRepository(dbHelper);
        repo.subirExplotacionesNoSincronizadas(new ExplotacionRepository.SyncCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() ->
                        Toast.makeText(DashboardActivity.this, "Explotación eliminada", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        Toast.makeText(DashboardActivity.this, "Eliminada en local. Pendiente de sincronizar.", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void irABuscarAnimal() {

        String idExplotacion = SessionManager.getIdExplotacionSeleccionada(this);
        String nombreExplotacion = SessionManager.getNombreExplotacionSeleccionada(this);

        if (idExplotacion == null || idExplotacion.isEmpty()) {
            Toast.makeText(this, "No hay explotación seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(DashboardActivity.this, BuscarAnimalActivity.class);
        intent.putExtra("id_explotacion_uuid", idExplotacion);
        intent.putExtra("nombre_explotacion", nombreExplotacion);
        startActivity(intent);
    }

    private void cargarResumenAnimales() {
        String idExplotacion = SessionManager.getIdExplotacionSeleccionada(this);

        if (idExplotacion == null || idExplotacion.trim().isEmpty()) {
            limpiarResumenAnimales();
            return;
        }

        AnimalRepository animalRepository = new AnimalRepository(dbHelper);

        animalRepository.obtenerResumenAnimalesExplotacionAsync(idExplotacion, new AnimalRepository.ResumenAnimalesCallback() {
            @Override
            public void onSuccess(int vacas, int terneros, int toros, int novillas, int total) {
                runOnUiThread(() -> {
                    txtTotalVacas.setText(String.valueOf(vacas));
                    txtTotalBecerros.setText(String.valueOf(terneros));
                    txtTotalToros.setText(String.valueOf(toros));
                    txtTotalNovillos.setText(String.valueOf(novillas));
                    txtTotalAnimalesExplotacion.setText("Animales en la explotación: " + total);

                    cargarProgresoParidera();
                    cargarBadgeCriasPendientes();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    limpiarResumenAnimales();
                    Toast.makeText(DashboardActivity.this, "Error cargando resumen de animales", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void limpiarResumenAnimales() {
        txtTotalVacas.setText("0");
        txtTotalBecerros.setText("0");
        txtTotalToros.setText("0");
        txtTotalNovillos.setText("0");
        txtTotalAnimalesExplotacion.setText("Animales en la explotación: 0");
    }

    private void cargarProgresoParidera() {
        String idExplotacion = SessionManager.getIdExplotacionSeleccionada(this);

        if (idExplotacion == null) return;

        String parideraActual = ParideraUtils.obtenerParideraActual();

        AnimalRepository repo = new AnimalRepository(dbHelper);

        repo.contarVacasParidasAsync(idExplotacion, parideraActual, new AnimalRepository.ProgresoParideraCallback() {
            @Override
            public void onSuccess(int vacasParidas) {
                runOnUiThread(() -> actualizarUIProgreso(vacasParidas));
            }

            @Override
            public void onError(String error) {
                Log.e("Dashboard", "Error progreso paridera: " + error);
            }
        });
    }

    private void actualizarUIProgreso(int vacasParidas) {

        String parideraActual = ParideraUtils.obtenerParideraActual();

        int totalVacas = Integer.parseInt(txtTotalVacas.getText().toString());

        int porcentaje = totalVacas > 0 ? (vacasParidas * 100 / totalVacas) : 0;

        txtProgresoVacas.setText("Paridera " + parideraActual + "   " + vacasParidas + " / " + totalVacas);
        txtPorcentajeVacas.setText(porcentaje + "%");
        progressVacas.setProgress(porcentaje);
    }

    private void irADiagnosticoGestacion() {
        String idExplotacion = SessionManager.getIdExplotacionSeleccionada(this);

        if (idExplotacion == null || idExplotacion.trim().isEmpty()) {
            Toast.makeText(this, "No hay explotación seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(DashboardActivity.this, DiagnosticoGestacionActivity.class);
        startActivity(intent);
    }

    private void irAPesos() {
        String idExplotacion = SessionManager.getIdExplotacionSeleccionada(this);

        if (idExplotacion == null || idExplotacion.trim().isEmpty()) {
            Toast.makeText(this, "No hay explotación seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(DashboardActivity.this, PesosActivity.class);
        intent.putExtra("id_explotacion", idExplotacion);
        startActivity(intent);
    }

    private void cargarBadgeCriasPendientes() {
        String idExplotacion = SessionManager.getIdExplotacionSeleccionada(this);

        if (idExplotacion == null || idExplotacion.trim().isEmpty()) {
            tvBadgeCriasPendientes.setVisibility(View.GONE);
            return;
        }

        new Thread(() -> {
            EventoReproductivoRepository repo =
                    new EventoReproductivoRepository(new DBHelper(DashboardActivity.this));

            int total = repo.contarCriasPendientesIdentificar(idExplotacion);

            runOnUiThread(() -> {
                if (total > 0) {
                    tvBadgeCriasPendientes.setText(String.valueOf(total));
                    tvBadgeCriasPendientes.setVisibility(View.VISIBLE);
                } else {
                    tvBadgeCriasPendientes.setVisibility(View.GONE);
                }
            });
        }).start();
    }

    private void irACriasPendientes() {
        String idExplotacion = SessionManager.getIdExplotacionSeleccionada(this);

        if (idExplotacion == null || idExplotacion.trim().isEmpty()) {
            Toast.makeText(this, "No hay explotación seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(DashboardActivity.this, CriasPendientesActivity.class);
        intent.putExtra("id_explotacion_uuid", idExplotacion);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarBadgeCriasPendientes();
    }

    private void irACrotales() {
        String idExplotacion = SessionManager.getIdExplotacionSeleccionada(this);

        if (idExplotacion == null || idExplotacion.trim().isEmpty()) {
            Toast.makeText(this, "No hay explotación seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(DashboardActivity.this, CrotalesActivity.class);
        intent.putExtra("id_explotacion_uuid", idExplotacion);
        startActivity(intent);
    }

    private void irAListados() {

        String idExplotacion = SessionManager.getIdExplotacionSeleccionada(this);

        if (idExplotacion == null || idExplotacion.trim().isEmpty()) {
            Toast.makeText(this,
                    "No hay explotación seleccionada",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(
                DashboardActivity.this,
                ListadosActivity.class
        );

        startActivity(intent);
    }
}