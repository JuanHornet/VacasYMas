package com.example.vacasymas.ui.animales.detalle.fragments;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.data.db.DBHelper;
import com.example.vacasymas.data.models.NotaAnimal;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class NotasAnimalFragment extends Fragment {

    private static final String ARG_ID_ANIMAL = "id_animal";

    private String idAnimal;
    private DBHelper dbHelper;
    private LinearLayout layoutContenido;

    public static NotasAnimalFragment newInstance(String idAnimal) {
        NotasAnimalFragment fragment = new NotasAnimalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID_ANIMAL, idAnimal);
        fragment.setArguments(args);
        return fragment;
    }

    public NotasAnimalFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater,
                             @Nullable android.view.ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            idAnimal = getArguments().getString(ARG_ID_ANIMAL);
        }

        dbHelper = new DBHelper(requireContext());

        android.widget.ScrollView scrollView = new android.widget.ScrollView(requireContext());
        scrollView.setFillViewport(false);

        scrollView.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        layoutContenido = new LinearLayout(requireContext());
        layoutContenido.setOrientation(LinearLayout.VERTICAL);
        layoutContenido.setPadding(dp(16), dp(12), dp(16), dp(24));

        scrollView.addView(layoutContenido, new android.widget.ScrollView.LayoutParams(
                android.widget.ScrollView.LayoutParams.MATCH_PARENT,
                android.widget.ScrollView.LayoutParams.WRAP_CONTENT
        ));

        cargarNotas();

        return scrollView;
    }

    private void cargarNotas() {
        layoutContenido.removeAllViews();

        TextView btnAnadir = crearBoton("+ Añadir nota");
        btnAnadir.setOnClickListener(v -> mostrarDialogoAnadirNota());
        layoutContenido.addView(btnAnadir);

        ArrayList<NotaAnimal> lista = dbHelper.obtenerNotasPorAnimal(idAnimal);

        if (lista == null || lista.isEmpty()) {
            TextView mensaje = crearTextoSecundario("No hay notas registradas.");
            mensaje.setPadding(0, dp(12), 0, 0);
            layoutContenido.addView(mensaje);
            return;
        }

        TextView titulo = crearTitulo("Notas: " + lista.size());
        titulo.setPadding(0, dp(12), 0, 0);
        layoutContenido.addView(titulo);

        for (NotaAnimal nota : lista) {
            layoutContenido.addView(crearCardNota(nota));
        }
    }

    private MaterialCardView crearCardNota(NotaAnimal nota) {
        MaterialCardView card = new MaterialCardView(requireContext());

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, dp(10), 0, 0);
        card.setLayoutParams(cardParams);

        card.setRadius(dp(14));
        card.setCardElevation(dp(2));
        card.setStrokeWidth(dp(1));
        card.setStrokeColor(0xFFE0E0E0);
        card.setUseCompatPadding(true);

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(12), dp(10), dp(12), dp(10));

        TextView tvFecha = crearTextoPrincipal("📝 " + FechaUtils.formatearFecha(nota.getFecha()));
        TextView tvTexto = crearTextoSecundario(nota.getTexto());

        root.addView(tvFecha);
        root.addView(tvTexto);

        card.setOnLongClickListener(v -> {
            confirmarEliminarNota(nota);
            return true;
        });

        card.addView(root);
        return card;
    }

    private void mostrarDialogoAnadirNota() {
        EditText etNota = new EditText(requireContext());
        etNota.setHint("Escribe una nota");
        etNota.setMinLines(3);
        etNota.setPadding(dp(12), dp(8), dp(12), dp(8));

        new AlertDialog.Builder(requireContext())
                .setTitle("Añadir nota")
                .setView(etNota)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String texto = etNota.getText().toString().trim();

                    if (texto.isEmpty()) {
                        Toast.makeText(requireContext(), "La nota no puede estar vacía", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean ok = dbHelper.insertarNotaAnimal(idAnimal, texto);

                    if (ok) {
                        Toast.makeText(requireContext(), "Nota guardada", Toast.LENGTH_SHORT).show();
                        cargarNotas();
                    } else {
                        Toast.makeText(requireContext(), "Error al guardar nota", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private TextView crearBoton(String texto) {
        TextView tv = new TextView(requireContext());
        tv.setText(texto);
        tv.setTextSize(14);
        tv.setTextColor(0xFF6750A4);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setPadding(dp(12), dp(10), dp(12), dp(10));
        tv.setGravity(android.view.Gravity.CENTER);
        return tv;
    }

    private TextView crearTitulo(String texto) {
        TextView tv = new TextView(requireContext());
        tv.setText(texto);
        tv.setTextSize(15);
        tv.setTextColor(0xFF000000);
        tv.setTypeface(null, Typeface.BOLD);
        return tv;
    }

    private TextView crearTextoPrincipal(String texto) {
        TextView tv = new TextView(requireContext());
        tv.setText(texto);
        tv.setTextSize(14);
        tv.setTextColor(0xFF000000);
        tv.setTypeface(null, Typeface.BOLD);
        return tv;
    }

    private TextView crearTextoSecundario(String texto) {
        TextView tv = new TextView(requireContext());
        tv.setText(texto);
        tv.setTextSize(13);
        tv.setTextColor(0xFF666666);
        tv.setPadding(0, dp(4), 0, 0);
        return tv;
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
    }

    private void confirmarEliminarNota(NotaAnimal nota) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar nota")
                .setMessage("¿Quieres eliminar esta nota?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    boolean ok = dbHelper.eliminarNotaAnimalLogico(nota.getId());

                    if (ok) {
                        Toast.makeText(requireContext(), "Nota eliminada", Toast.LENGTH_SHORT).show();
                        cargarNotas();
                    } else {
                        Toast.makeText(requireContext(), "Error al eliminar nota", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}