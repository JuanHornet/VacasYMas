package com.example.vacasymas.ui.animales.detalle.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.base.TextoUtils;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.Animal;
import com.example.vacasymas.data.repo.AnimalRepository;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class HistorialReproductivoFragment extends Fragment {

    private static final String ARG_ID_ANIMAL = "id_animal";
    private static final String ARG_CROTAL_ANIMAL = "crotal_animal";

    private String idAnimal;
    private String crotalAnimal;

    private LinearLayout layoutContenido;
    private AnimalRepository animalRepository;

    public static HistorialReproductivoFragment newInstance(String idAnimal, String crotalAnimal) {
        HistorialReproductivoFragment fragment = new HistorialReproductivoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID_ANIMAL, idAnimal);
        args.putString(ARG_CROTAL_ANIMAL, crotalAnimal);
        fragment.setArguments(args);
        return fragment;
    }

    public HistorialReproductivoFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater,
                             @Nullable android.view.ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            idAnimal = getArguments().getString(ARG_ID_ANIMAL);
            crotalAnimal = getArguments().getString(ARG_CROTAL_ANIMAL);
        }

        DBHelper dbHelper = new DBHelper(requireContext());
        animalRepository = new AnimalRepository(dbHelper);

        android.widget.ScrollView scrollView = new android.widget.ScrollView(requireContext());
        scrollView.setFillViewport(false);

        scrollView.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        layoutContenido = new LinearLayout(requireContext());
        layoutContenido.setOrientation(LinearLayout.VERTICAL);
        layoutContenido.setPadding(16, 16, 16, 16);

        scrollView.addView(layoutContenido, new android.widget.ScrollView.LayoutParams(
                android.widget.ScrollView.LayoutParams.MATCH_PARENT,
                android.widget.ScrollView.LayoutParams.WRAP_CONTENT
        ));

        mostrarCargando();
        cargarCrias();

        return scrollView;
    }

    private void cargarCrias() {
        if (crotalAnimal == null || crotalAnimal.trim().isEmpty()) {
            mostrarMensaje("No se ha recibido el crotal de la vaca.");
            return;
        }

        animalRepository.obtenerCriasPorCrotalMadreAsync(crotalAnimal, new AnimalRepository.ListaAnimalesCallback() {
            @Override
            public void onSuccess(List<Animal> animales) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    if (animales == null || animales.isEmpty()) {
                        mostrarMensaje("No hay crías registradas para esta vaca.");
                    } else {
                        mostrarCrias(animales);
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() ->
                        mostrarMensaje("Error al cargar el historial reproductivo.")
                );
            }
        });
    }

    private void mostrarCrias(List<Animal> crias) {
        layoutContenido.removeAllViews();

        TextView titulo = crearTitulo("Crías registradas: " + crias.size());
        layoutContenido.addView(titulo);

        for (Animal cria : crias) {
            layoutContenido.addView(crearCardCria(cria));
        }
    }

    private MaterialCardView crearCardCria(Animal cria) {
        MaterialCardView card = new MaterialCardView(requireContext());

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 12, 0, 0);
        card.setLayoutParams(cardParams);

        card.setRadius(dp(14));
        card.setCardElevation(dp(2));
        card.setStrokeWidth(dp(1));
        card.setStrokeColor(0xFFE0E0E0);
        card.setUseCompatPadding(true);

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setPadding(dp(12), dp(12), dp(12), dp(12));
        root.setGravity(Gravity.CENTER_VERTICAL);


        LinearLayout textos = new LinearLayout(requireContext());
        textos.setOrientation(LinearLayout.VERTICAL);
        textos.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        ));

        // LINEA 1 → crotal + fecha nacimiento
        String crotal = valorSeguro(cria.getCrotal());
        String fecha = FechaUtils.formatearFecha(cria.getFechaNacimiento());

        String textoCompleto = crotal + " · " + fecha;

        android.text.SpannableString spannable = new android.text.SpannableString(textoCompleto);

// Crotal → negro + bold
        spannable.setSpan(
                new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                0,
                crotal.length(),
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

// Fecha → gris
        spannable.setSpan(
                new android.text.style.ForegroundColorSpan(0xFF888888),
                crotal.length() + 3, // " · "
                textoCompleto.length(),
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        TextView tvLinea1 = crearTextoLineaSuperior(spannable);

// Color por sexo SOLO en la primera línea
        aplicarColorSexo(tvLinea1, cria);

// LINEA 2 → icono + estado + fecha baja
        TextView tvLinea2 = crearTextoSecundario(
                obtenerIconoEstado(cria) + " " + crearTextoEstadoSimple(cria)
        );

        textos.addView(tvLinea1);
        textos.addView(tvLinea2);

        root.addView(textos);

        card.addView(root);

        return card;
    }

    private String crearTextoEstado(Animal animal) {
        if ("1".equals(animal.getStatecode())) {
            return "Estado: Activo en explotación";
        }

        String estado = TextoUtils.capitalizar(animal.getEstatusDescripcion());
        String fechaBaja = FechaUtils.formatearFecha(animal.getFechaBajaExplotacion());

        return "Estado: " + estado + " · Baja: " + fechaBaja;
    }

    private String obtenerIconoEstado(Animal animal) {
        if ("1".equals(animal.getStatecode())) {
            return "🟢";
        }

        Integer estatus = animal.getEstatus();

        if (estatus == null) return "🔴";

        switch (estatus) {
            case 10006:
                return "💰"; // vendido
            case 10007:
                return "✝"; // fallecido
            case 10008:
                return "❓"; // desaparecido
            case 10010:
                return "⚠"; // tuberculosis
            default:
                return "🔴";
        }
    }

    private void mostrarCargando() {
        layoutContenido.removeAllViews();
        layoutContenido.addView(crearTextoSecundario("Cargando historial reproductivo..."));
    }

    private void mostrarMensaje(String mensaje) {
        layoutContenido.removeAllViews();
        layoutContenido.addView(crearTextoSecundario(mensaje));
    }

    private TextView crearTitulo(String texto) {
        TextView tv = new TextView(requireContext());
        tv.setText(texto);
        tv.setTextSize(16);
        tv.setTextColor(0xFF000000);
        tv.setTypeface(null, Typeface.BOLD);
        return tv;
    }

    private TextView crearTextoPrincipal(String texto) {
        TextView tv = new TextView(requireContext());
        tv.setText(texto);
        tv.setTextSize(15);
        tv.setTextColor(0xFF000000);
        tv.setTypeface(null, Typeface.BOLD);
        return tv;
    }

    private TextView crearTextoSecundario(String texto) {
        TextView tv = new TextView(requireContext());
        tv.setText(texto);
        tv.setTextSize(13);
        tv.setTextColor(0xFF666666);
        tv.setPadding(0, dp(3), 0, 0);
        return tv;
    }

    private String valorSeguro(String valor) {
        return valor == null || valor.trim().isEmpty() ? "-" : valor;
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
    }

    private String crearTextoEstadoSimple(Animal animal) {
        if ("1".equals(animal.getStatecode())) {
            return "Activo";
        }

        String estado = TextoUtils.capitalizar(animal.getEstatusDescripcion());
        String fechaBaja = FechaUtils.formatearFecha(animal.getFechaBajaExplotacion());

        if (fechaBaja == null || fechaBaja.equals("-")) {
            return estado;
        }

        return estado + " · " + fechaBaja;
    }


    private void aplicarColorSexo(TextView tv, Animal animal) {
        String sexo = animal.getSexo();

        if (sexo == null) return;

        if (sexo.equalsIgnoreCase("macho")) {
            tv.setTextColor(0xFF1565C0); // azul
        } else if (sexo.equalsIgnoreCase("hembra")) {
            tv.setTextColor(0xFFC62828); // rojo
        } else {
            tv.setTextColor(0xFF000000); // fallback
        }
    }
    private TextView crearTextoLineaSuperior(CharSequence  texto) {
        TextView tv = new TextView(requireContext());
        tv.setText(texto);
        tv.setTextSize(13); // antes era más grande (15-16)
        tv.setTextColor(0xFF000000);
        tv.setTypeface(null, Typeface.BOLD);
        return tv;
    }
}