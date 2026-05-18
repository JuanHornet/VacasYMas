package com.example.vacasymas.ui.diagnostico;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.Animal;
import com.example.vacasymas.data.models.DiagnosticoGestacion;

import java.util.ArrayList;

public class DiagnosticoListadoVacasFragment extends Fragment {

    private static final String ARG_ID_EXPLOTACION = "id_explotacion";

    private String idExplotacion;

    private TextView tvResumenVacasDiagnostico, tvResumenEstadosDiagnostico;
    private Spinner spFiltroFechaDiagnostico, spFiltroEstadoDiagnostico;
    private RecyclerView rvVacasDiagnostico;

    private DBHelper dbHelper;
    private VacaDiagnosticoAdapter adapter;
    private final ArrayList<VacaDiagnosticoItem> listaItems = new ArrayList<>();

    private String filtroFecha = "Todas las fechas";
    private String filtroEstado = "Todos los estados";

    public static DiagnosticoListadoVacasFragment newInstance(String idExplotacion) {
        DiagnosticoListadoVacasFragment fragment = new DiagnosticoListadoVacasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID_EXPLOTACION, idExplotacion);
        fragment.setArguments(args);
        return fragment;
    }

    public DiagnosticoListadoVacasFragment() {
        super(R.layout.fragment_diagnostico_listado_vacas);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            idExplotacion = getArguments().getString(ARG_ID_EXPLOTACION);
        }

        dbHelper = new DBHelper(requireContext());

        tvResumenVacasDiagnostico = view.findViewById(R.id.tvResumenVacasDiagnostico);
        tvResumenEstadosDiagnostico = view.findViewById(R.id.tvResumenEstadosDiagnostico);
        spFiltroFechaDiagnostico = view.findViewById(R.id.spFiltroFechaDiagnostico);
        spFiltroEstadoDiagnostico = view.findViewById(R.id.spFiltroEstadoDiagnostico);
        rvVacasDiagnostico = view.findViewById(R.id.rvVacasDiagnostico);

        adapter = new VacaDiagnosticoAdapter(listaItems, animal -> {
            Toast.makeText(
                    requireContext(),
                    animal.getCrotal(),
                    Toast.LENGTH_SHORT
            ).show();
        });

        rvVacasDiagnostico.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvVacasDiagnostico.setAdapter(adapter);

        configurarFiltros();
        cargarVacas();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarVacas();
    }

    private void configurarFiltros() {

        ArrayList<String> fechasOriginales =
                dbHelper.obtenerFechasDiagnosticosGestacion(idExplotacion);

        ArrayList<String> fechasMostrar = new ArrayList<>();

        fechasMostrar.add("Todas las fechas");

        for (String fecha : fechasOriginales) {
            fechasMostrar.add(FechaUtils.formatearFecha(fecha));
        }

        ArrayAdapter<String> adapterFechas = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                fechasMostrar
        );

        adapterFechas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFiltroFechaDiagnostico.setAdapter(adapterFechas);

        String[] estados = {
                "Todos los estados",
                "Nada",
                "Vacía",
                "Cubierta",
                "Preñada"
        };

        ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                estados
        );

        adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFiltroEstadoDiagnostico.setAdapter(adapterEstados);

        spFiltroFechaDiagnostico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    filtroFecha = "Todas las fechas";
                } else {
                    filtroFecha = fechasOriginales.get(position - 1);
                }

                cargarVacas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spFiltroEstadoDiagnostico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                filtroEstado = parent.getItemAtPosition(position).toString();

                cargarVacas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void cargarVacas() {
        if (dbHelper == null || adapter == null) return;

        listaItems.clear();

        ArrayList<Animal> vacas = dbHelper.obtenerVacasPorExplotacion(idExplotacion);

        int preñadas = 0;
        int vacias = 0;
        int cubiertas = 0;
        int nada = 0;

        for (Animal vaca : vacas) {
            ArrayList<DiagnosticoGestacion> diagnosticos =
                    dbHelper.obtenerDiagnosticosGestacionPorAnimal(vaca.getId());

            DiagnosticoGestacion ultimo = null;

            if (diagnosticos != null && !diagnosticos.isEmpty()) {
                ultimo = diagnosticos.get(0);
            }

            String estado = vaca.getEstadoReproductivo();

            if (!pasaFiltroEstado(estado)) {
                continue;
            }

            if (!pasaFiltroFecha(ultimo)) {
                continue;
            }

            listaItems.add(new VacaDiagnosticoItem(vaca, ultimo));

            switch (normalizarEstado(estado)) {
                case "prenada":
                    preñadas++;
                    break;
                case "vacia":
                    vacias++;
                    break;
                case "cubierta":
                    cubiertas++;
                    break;
                case "nada":
                default:
                    nada++;
                    break;
            }
        }

        tvResumenVacasDiagnostico.setText("Vacas: " + listaItems.size());

        tvResumenEstadosDiagnostico.setText(
                "Preñadas: " + preñadas +
                        " | Vacías: " + vacias +
                        " | Cubiertas: " + cubiertas +
                        " | Nada: " + nada
        );

        adapter.notifyDataSetChanged();
    }

    private boolean pasaFiltroFecha(DiagnosticoGestacion ultimo) {
        if (filtroFecha == null || filtroFecha.equals("Todas las fechas")) {
            return true;
        }

        if (ultimo == null || ultimo.getFecha() == null) {
            return false;
        }

        return ultimo.getFecha().equals(filtroFecha);
    }

    private boolean pasaFiltroEstado(String estado) {
        if (filtroEstado == null || filtroEstado.equals("Todos los estados")) {
            return true;
        }

        return normalizarEstado(estado).equals(normalizarEstado(filtroEstado));
    }

    private String normalizarEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return "nada";
        }

        String e = estado.trim().toLowerCase();

        if (e.equals("vacía")) return "vacia";
        if (e.equals("preñada")) return "prenada";

        return e;
    }
}