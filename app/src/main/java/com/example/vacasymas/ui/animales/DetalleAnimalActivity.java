package com.example.vacasymas.ui.animales;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vacasymas.R;
import com.example.vacasymas.base.AnimalUtils;
import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.base.TextoUtils;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.Animal;
import com.example.vacasymas.data.repo.AnimalRepository;
import com.example.vacasymas.ui.animales.detalle.adapters.DetalleAnimalPagerAdapter;
import com.example.vacasymas.ui.reproduccion.RegistrarPartoActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Calendar;

public class DetalleAnimalActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;

    private TextView tvCrotal;
    private TextView tvTipo;
    private TextView tvFechaNacimiento;
    private TextView tvCapa;
    private TextView tvCrotalMadre;
    private TextView tvEdad;
    private TextView tvRazaInline;
    private TextView tvEstadoReproductivo;
    private MaterialCardView cardPrincipal;
    private TabLayout tabDetalleAnimal;
    private ViewPager2 viewPagerDetalleAnimal;

    private ImageView ivCrotalIzquierdo;
    private ImageView ivCrotalDerecho;


    private AnimalRepository animalRepository;
    private String idAnimal;
    private Animal animalActual;

    private TextView tvSeparador1, tvSeparador2, tvSeparadorRaza, tvFechaBajaInline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_animal);

        idAnimal = getIntent().getStringExtra("id_animal");

        initViews();
        initToolbar();
        initRepository();
        cargarAnimal();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);

        tvCrotal = findViewById(R.id.tvCrotal);
        tvEdad = findViewById(R.id.tvEdad);
        tvTipo = findViewById(R.id.tvTipo);
        tvFechaNacimiento = findViewById(R.id.tvFechaNacimiento);
        tvCapa = findViewById(R.id.tvCapa);
        tvCrotalMadre = findViewById(R.id.tvCrotalMadre);
        tvRazaInline = findViewById(R.id.tvRazaInline);
        cardPrincipal = findViewById(R.id.cardPrincipal);
        tvSeparador1 = findViewById(R.id.tvSeparador1);
        tvSeparador2 = findViewById(R.id.tvSeparador2);
        tvFechaBajaInline = findViewById(R.id.tvFechaBajaInline);
        tvEstadoReproductivo = findViewById(R.id.tvEstadoReproductivo);
        tvSeparadorRaza = findViewById(R.id.tvSeparadorRaza);
        tvRazaInline = findViewById(R.id.tvRazaInline);
        tabDetalleAnimal = findViewById(R.id.tabDetalleAnimal);
        viewPagerDetalleAnimal = findViewById(R.id.viewPagerDetalleAnimal);
        tabDetalleAnimal = findViewById(R.id.tabDetalleAnimal);
        viewPagerDetalleAnimal = findViewById(R.id.viewPagerDetalleAnimal);
        ivCrotalIzquierdo = findViewById(R.id.ivCrotalIzquierdo);
        ivCrotalDerecho = findViewById(R.id.ivCrotalDerecho);

        ImageView ivCrotalIzquierdo = findViewById(R.id.ivCrotalIzquierdo);
        ImageView ivCrotalDerecho = findViewById(R.id.ivCrotalDerecho);

        ImageView btnAcciones = findViewById(R.id.btnAcciones);

        btnAcciones.setOnClickListener(v -> {
            if (animalActual != null) {
                mostrarMenuAcciones(animalActual);
            }
        });

    }

    private void initToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    private void initRepository() {
        DBHelper dbHelper = new DBHelper(this);
        animalRepository = new AnimalRepository(dbHelper);
    }

    private void cargarAnimal() {
        if (idAnimal == null || idAnimal.trim().isEmpty()) {
            Toast.makeText(this, "No se ha recibido el animal", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        animalRepository.buscarPorIdAsync(idAnimal, new AnimalRepository.AnimalCallback() {
            @Override
            public void onSuccess(Animal animal) {
                runOnUiThread(() -> {
                    if (animal == null) {
                        Toast.makeText(DetalleAnimalActivity.this, "Animal no encontrado", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    mostrarAnimal(animal);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(DetalleAnimalActivity.this, "Error al cargar el animal", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void mostrarAnimal(Animal animal) {

        this.animalActual = animal;

        Log.d("DetalleAnimal", "Estado reproductivo recibido: " + animal.getEstadoReproductivo());
        Log.d("DetalleAnimal", "Estatus recibido: " + animal.getEstatus());

        tvCrotal.setText(valorSeguro(animal.getCrotal()));

        // Primera card: línea dinámica
        aplicarEstadoVisual(animal);
        aplicarLineaPrincipal(animal);

        // Segunda card
        tvFechaNacimiento.setText(FechaUtils.formatearFecha(animal.getFechaNacimiento()));
        tvCrotalMadre.setText(valorSeguro(animal.getCrotalMadre()));
        configurarClickMadre(animal);
        tvCapa.setText(valorSeguro(animal.getCapa()));
        tvRazaInline.setText(valorSeguro(animal.getRaza()));

        configurarSeccionDinamica(animal);
        actualizarCrotalesUI(animal);

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

    private void aplicarEstadoVisual(Animal animal) {

        if ("0".equals(animal.getStatecode())) {
            // Animal fuera de explotación

            cardPrincipal.setStrokeColor(getColor(android.R.color.holo_red_dark));
            cardPrincipal.setStrokeWidth(4);

        } else {
            // Animal activo

            cardPrincipal.setStrokeColor(getColor(android.R.color.darker_gray));
            cardPrincipal.setStrokeWidth(1);
        }
    }


    private void aplicarLineaPrincipal(Animal animal) {

        if ("0".equals(animal.getStatecode())) {
            // ANIMAL FUERA DE LA EXPLOTACIÓN

            tvEdad.setVisibility(View.GONE);
            tvSeparador1.setVisibility(View.GONE);

            tvTipo.setVisibility(View.VISIBLE);
            tvTipo.setText(TextoUtils.capitalizar(animal.getEstatusDescripcion()));
            tvTipo.setBackgroundResource(R.drawable.bg_chip_estado_baja);

            tvSeparadorRaza.setVisibility(View.VISIBLE);

            tvEstadoReproductivo.setVisibility(View.VISIBLE);
            tvEstadoReproductivo.setText(
                    FechaUtils.formatearFecha(animal.getFechaBajaExplotacion())
            );

            return;
        }

        // ANIMAL ACTIVO

        tvEdad.setVisibility(View.VISIBLE);
        tvSeparador1.setVisibility(View.VISIBLE);

        tvTipo.setVisibility(View.VISIBLE);
        tvTipo.setText(TextoUtils.capitalizar(animal.getEstatusDescripcion()));
        tvTipo.setBackgroundResource(R.drawable.bg_chip_estado_activo);

        tvEdad.setText(AnimalUtils.calcularEdad(animal.getFechaNacimiento()));

        String estadoRep = obtenerEstadoReproductivo(animal);

        if (estadoRep == null || estadoRep.trim().isEmpty()) {
            tvSeparadorRaza.setVisibility(View.GONE);
            tvEstadoReproductivo.setVisibility(View.GONE);
        } else {
            tvSeparadorRaza.setVisibility(View.VISIBLE);
            tvEstadoReproductivo.setVisibility(View.VISIBLE);
            tvEstadoReproductivo.setText(TextoUtils.capitalizar(estadoRep));
        }
    }

    private String obtenerEstadoReproductivo(Animal animal) {
        if (animal.getEstatus() == null || animal.getEstatus() != 10003) {
            return null;
        }

        String estado = animal.getEstadoReproductivo();

        if (estado == null || estado.trim().isEmpty() || estado.equalsIgnoreCase("nada")) {
            return null;
        }

        return estado;
    }

    private void configurarSeccionDinamica(Animal animal) {
        Integer estatus = animal.getEstatus();


        DetalleAnimalPagerAdapter adapter =
                new DetalleAnimalPagerAdapter(
                        this,
                        animal.getId(),
                        animal.getCrotal(),
                        animal.getIdExplotacionUuid(),
                        estatus,
                        animal.getStatecode()
                );

        viewPagerDetalleAnimal.setAdapter(adapter);
        viewPagerDetalleAnimal.setOffscreenPageLimit(3);

        new TabLayoutMediator(tabDetalleAnimal, viewPagerDetalleAnimal,
                (tab, position) -> tab.setText(adapter.getTitulo(position))
        ).attach();
    }


    private void mostrarDialogoDarBajaAnimal(Animal animal) {

        View view = getLayoutInflater().inflate(R.layout.dialog_baja_animal, null);

        Spinner spMotivoBaja = view.findViewById(R.id.spMotivoBaja);
        EditText etFechaBaja = view.findViewById(R.id.etFechaBaja);

        String[] motivos = {
                "Vendido",
                "Fallecido",
                "Desaparecido",
                "Tuberculosis"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                motivos
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMotivoBaja.setAdapter(adapter);

        final String[] fechaBajaIso = {null};

        etFechaBaja.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (datePicker, year, month, dayOfMonth) -> {

                        String fechaVisible = String.format(
                                java.util.Locale.getDefault(),
                                "%02d/%02d/%04d",
                                dayOfMonth,
                                month + 1,
                                year
                        );

                        String fechaIso = String.format(
                                java.util.Locale.getDefault(),
                                "%04d-%02d-%02d",
                                year,
                                month + 1,
                                dayOfMonth
                        );

                        etFechaBaja.setText(fechaVisible);
                        fechaBajaIso[0] = fechaIso;
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.show();
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Dar de baja animal")
                .setView(view)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Dar de baja", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(getColor(android.R.color.holo_red_dark));

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

                if (fechaBajaIso[0] == null) {
                    Toast.makeText(this, "Selecciona la fecha de baja", Toast.LENGTH_SHORT).show();
                    return;
                }

                String motivo = spMotivoBaja.getSelectedItem().toString();
                int nuevoEstatus = obtenerCodigoBaja(motivo);

                boolean ok = animalRepository.darDeBajaAnimal(
                        animal.getId(),
                        nuevoEstatus,
                        fechaBajaIso[0]
                );

                if (ok) {
                    Toast.makeText(this, "Animal dado de baja", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    cargarAnimal();
                } else {
                    Toast.makeText(this, "Error al dar de baja", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void configurarClickMadre(Animal animal) {
        String crotalMadre = animal.getCrotalMadre();

        if (crotalMadre == null || crotalMadre.trim().isEmpty()) {
            tvCrotalMadre.setEnabled(false);
            tvCrotalMadre.setTextColor(getColor(android.R.color.black));
            tvCrotalMadre.setOnClickListener(null);
            return;
        }

        tvCrotalMadre.setEnabled(true);
        tvCrotalMadre.setTextColor(0xFF6750A4);
        tvCrotalMadre.setOnClickListener(v -> abrirDetallePorCrotal(crotalMadre));
    }

    private void abrirDetallePorCrotal(String crotal) {
        animalRepository.buscarPorCrotalAsync(crotal, new AnimalRepository.AnimalCallback() {
            @Override
            public void onSuccess(Animal animal) {
                runOnUiThread(() -> {
                    if (animal == null) {
                        Toast.makeText(
                                DetalleAnimalActivity.this,
                                "No se encontró la madre en la base de datos",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    android.content.Intent intent = new android.content.Intent(
                            DetalleAnimalActivity.this,
                            DetalleAnimalActivity.class
                    );
                    intent.putExtra("id_animal", animal.getId());
                    startActivity(intent);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        Toast.makeText(
                                DetalleAnimalActivity.this,
                                "Error al abrir la madre",
                                Toast.LENGTH_SHORT
                        ).show()
                );
            }
        });
    }

    private void mostrarDialogoEditarAnimal(Animal animal) {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 0);

        // ===== CAPA =====
        TextView tvLabelCapa = new TextView(this);
        tvLabelCapa.setText("Capa");
        tvLabelCapa.setTextColor(getColor(android.R.color.darker_gray));
        tvLabelCapa.setTextSize(18);
        layout.addView(tvLabelCapa);

        EditText etCapa = new EditText(this);
        etCapa.setText(valorSeguroParaEditar(animal.getCapa()));
        etCapa.setTextSize(18);
        layout.addView(etCapa);

        // ===== RAZA =====
        TextView tvLabelRaza = new TextView(this);
        tvLabelRaza.setText("Raza");
        tvLabelRaza.setTextColor(getColor(android.R.color.darker_gray));
        tvLabelRaza.setTextSize(18);
        tvLabelRaza.setPadding(0, 20, 0, 0);
        layout.addView(tvLabelRaza);

        Spinner spRaza = new Spinner(this);

        String[] razas = {
                "Retinta",
                "Limousine",
                "Cruzada"
        };

        ArrayAdapter<String> adapterRaza = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                razas
        );
        adapterRaza.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spRaza.setAdapter(adapterRaza);
        spRaza.setSelection(obtenerPosicionRaza(animal.getRaza()));

        layout.addView(spRaza);

        // ===== CAMBIO DE TIPO ANIMAL =====
        final CheckBox[] cbCambioTipo = new CheckBox[1];

        Integer estatusActual = animal.getEstatus();

        if (estatusActual != null && (estatusActual == 10002 || estatusActual == 10005)) {

            TextView tvLabelTipo = new TextView(this);
            tvLabelTipo.setText("Tipo Animal");
            tvLabelTipo.setTextColor(getColor(android.R.color.darker_gray));
            tvLabelTipo.setTextSize(18);
            tvLabelTipo.setPadding(0, 20, 0, 0);
            layout.addView(tvLabelTipo);

            cbCambioTipo[0] = new CheckBox(this);
            cbCambioTipo[0].setTextSize(24);

            if (estatusActual == 10002) {
                cbCambioTipo[0].setText("Pasar a novilla");
            } else {
                cbCambioTipo[0].setText("Pasar a vaca");
            }

            layout.addView(cbCambioTipo[0]);
        }

        new AlertDialog.Builder(this)
                .setTitle("Editar datos del animal")
                .setView(layout)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", (dialog, which) -> {

                    String nuevaCapa = etCapa.getText().toString().trim();
                    String nuevaRaza = spRaza.getSelectedItem().toString();

                    int nuevoEstatus = animal.getEstatus();

                    if (cbCambioTipo[0] != null && cbCambioTipo[0].isChecked()) {
                        if (animal.getEstatus() == 10002) {
                            nuevoEstatus = 10005; // Ternera hembra -> Novilla
                        } else if (animal.getEstatus() == 10005) {
                            nuevoEstatus = 10003; // Novilla -> Vaca
                        }
                    }

                    boolean ok = animalRepository.actualizarDatosBasicosAnimal(
                            animal.getId(),
                            nuevaCapa,
                            nuevaRaza,
                            nuevoEstatus
                    );

                    if (ok) {
                        Toast.makeText(this, "Animal actualizado", Toast.LENGTH_SHORT).show();
                        cargarAnimal();
                    } else {
                        Toast.makeText(this, "Error al actualizar animal", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void mostrarDialogoGestionarCrotales(Animal animal) {

        View view = getLayoutInflater().inflate(R.layout.dialog_crotales, null);

        CheckBox cbIzquierdo = view.findViewById(R.id.cbCrotalIzquierdo);
        CheckBox cbDerecho = view.findViewById(R.id.cbCrotalDerecho);

        cbIzquierdo.setChecked(Boolean.TRUE.equals(animal.getCrotalIzquierdoPresente()));
        cbDerecho.setChecked(Boolean.TRUE.equals(animal.getCrotalDerechoPresente()));

        new AlertDialog.Builder(this)
                .setTitle("Gestionar crotales")
                .setView(view)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", (dialog, which) -> {

                    boolean izquierdo = cbIzquierdo.isChecked();
                    boolean derecho = cbDerecho.isChecked();

                    boolean ok = animalRepository.actualizarCrotalesAnimal(
                            animal.getId(),
                            izquierdo,
                            derecho
                    );

                    if (ok) {
                        Toast.makeText(this, "Crotales actualizados", Toast.LENGTH_SHORT).show();
                        cargarAnimal();
                    } else {
                        Toast.makeText(this, "Error al actualizar crotales", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void mostrarMenuAcciones(Animal animal) {

        boolean animalDeBaja = "0".equals(animal.getStatecode());
        boolean esVacaActiva = animal.getEstatus() != null
                && animal.getEstatus() == 10003
                && !animalDeBaja;

        String[] opciones;

        if (animalDeBaja) {
            opciones = new String[]{
                    "Reactivar animal"
            };
        } else if (esVacaActiva) {
            opciones = new String[]{
                    "Registrar parto / aborto",
                    "Editar datos del animal",
                    "Gestionar crotales",
                    "Dar de baja animal"
            };
        } else {
            opciones = new String[]{
                    "Editar datos del animal",
                    "Gestionar crotales",
                    "Dar de baja animal"
            };
        }

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                opciones
        ) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = view.findViewById(android.R.id.text1);

                tv.setTypeface(null, android.graphics.Typeface.NORMAL);
                tv.setTextColor(getColor(android.R.color.black));
                tv.setTextSize(17);

                if (animalDeBaja && position == 0) {
                    tv.setTextColor(0xFF2E7D32);
                    tv.setTypeface(null, android.graphics.Typeface.BOLD);
                }

                if (!animalDeBaja) {
                    int posicionDarBaja = esVacaActiva ? 3 : 2;

                    if (position == posicionDarBaja) {
                        tv.setTextColor(getColor(android.R.color.holo_red_dark));
                        tv.setTypeface(null, android.graphics.Typeface.BOLD);
                    }

                    if (esVacaActiva && position == 0) {
                        tv.setTextColor(0xFF6750A4);
                        tv.setTypeface(null, android.graphics.Typeface.BOLD);
                    }
                }

                return view;
            }
        };

        new AlertDialog.Builder(this)
                .setTitle(animalDeBaja ? "Animal de baja" : "Acciones del animal")
                .setAdapter(adapter, (dialog, which) -> {

                    if (animalDeBaja) {
                        if (which == 0) {
                            mostrarDialogoReactivarAnimal(animal);
                        }
                        return;
                    }

                    if (esVacaActiva) {
                        switch (which) {
                            case 0:
                                abrirRegistrarParto(animal);
                                break;

                            case 1:
                                mostrarDialogoEditarAnimal(animal);
                                break;

                            case 2:
                                mostrarDialogoGestionarCrotales(animal);
                                break;

                            case 3:
                                mostrarDialogoDarBajaAnimal(animal);
                                break;
                        }
                    } else {
                        switch (which) {
                            case 0:
                                mostrarDialogoEditarAnimal(animal);
                                break;

                            case 1:
                                mostrarDialogoGestionarCrotales(animal);
                                break;

                            case 2:
                                mostrarDialogoDarBajaAnimal(animal);
                                break;
                        }
                    }
                })
                .show();
    }

    private String valorSeguroParaEditar(String valor) {
        return valor == null || valor.trim().isEmpty() || valor.equals("-") ? "" : valor;
    }

    private int obtenerPosicionTipo(Integer estatus) {
        if (estatus == null) return 0;

        switch (estatus) {
            case 10003: return 0; // Vaca
            case 10004: return 1; // Toro
            case 10005: return 2; // Novilla
            case 10001: return 3; // Ternero macho
            case 10002: return 4; // Ternera hembra
            default: return 0;
        }
    }

    private int obtenerCodigoEstatus(String tipo) {
        switch (tipo) {
            case "Vaca":
                return 10003;
            case "Toro":
                return 10004;
            case "Novilla":
                return 10005;
            case "Ternero macho":
                return 10001;
            case "Ternera hembra":
                return 10002;
            default:
                return 10003;
        }
    }

    private int obtenerPosicionRaza(String raza) {
        if (raza == null) return 0;

        switch (raza.trim().toLowerCase()) {
            case "retinta":
                return 0;

            case "limousine":
            case "limusina": // por si queda algún dato antiguo en SQLite
                return 1;

            case "cruzada":
                return 2;

            default:
                return 0;
        }
    }

    private void actualizarCrotalesUI(Animal animal) {

        if (Boolean.TRUE.equals(animal.getCrotalIzquierdoPresente())) {
            ivCrotalIzquierdo.setImageResource(R.drawable.ic_crotal_presente);
        } else {
            ivCrotalIzquierdo.setImageResource(R.drawable.ic_falta_crotal);
        }

        if (Boolean.TRUE.equals(animal.getCrotalDerechoPresente())) {
            ivCrotalDerecho.setImageResource(R.drawable.ic_crotal_presente);
        } else {
            ivCrotalDerecho.setImageResource(R.drawable.ic_falta_crotal);
        }

        // CLICK → abrir diálogo
        ivCrotalIzquierdo.setOnClickListener(v -> mostrarDialogoGestionarCrotales(animal));
        ivCrotalDerecho.setOnClickListener(v -> mostrarDialogoGestionarCrotales(animal));
    }

    private int obtenerCodigoBaja(String motivo) {
        switch (motivo) {
            case "Vendido":
                return 10006;
            case "Fallecido":
                return 10007;
            case "Desaparecido":
                return 10008;
            case "Tuberculosis":
                return 10010;
            default:
                return 10006;
        }
    }

    private void mostrarDialogoReactivarAnimal(Animal animal) {

        View view = getLayoutInflater().inflate(R.layout.dialog_reactivar_animal, null);

        Spinner spTipo = view.findViewById(R.id.spTipoReactivar);

        String[] tipos = obtenerTiposReactivacion(animal);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tipos
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Reactivar animal")
                .setView(view)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Reactivar", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

                String tipo = spTipo.getSelectedItem().toString();
                int nuevoEstatus = obtenerCodigoEstatus(tipo);

                boolean ok = animalRepository.reactivarAnimal(
                        animal.getId(),
                        nuevoEstatus
                );

                if (ok) {
                    Toast.makeText(this, "Animal reactivado", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    cargarAnimal();
                } else {
                    Toast.makeText(this, "Error al reactivar", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private String[] obtenerTiposReactivacion(Animal animal) {

        String sexo = animal.getSexo() != null
                ? animal.getSexo().trim().toLowerCase()
                : "";

        if (sexo.equals("hembra") || sexo.equals("h")) {
            return new String[]{
                    "Vaca",
                    "Novilla",
                    "Ternera hembra"
            };
        }

        if (sexo.equals("macho") || sexo.equals("m")) {
            return new String[]{
                    "Toro",
                    "Ternero macho"
            };
        }

        return new String[]{
                "Vaca",
                "Toro",
                "Novilla",
                "Ternero macho",
                "Ternera hembra"
        };
    }

    private void abrirRegistrarParto(Animal animal) {
        android.content.Intent intent = new android.content.Intent(
                this,
                RegistrarPartoActivity.class
        );

        intent.putExtra("id_madre", animal.getId());
        intent.putExtra("crotal_madre", animal.getCrotal());
        intent.putExtra("id_explotacion_uuid", animal.getIdExplotacionUuid());

        startActivity(intent);
    }
}
