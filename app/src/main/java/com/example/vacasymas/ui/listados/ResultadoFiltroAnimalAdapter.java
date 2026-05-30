package com.example.vacasymas.ui.listados;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.data.models.Animal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class ResultadoFiltroAnimalAdapter extends RecyclerView.Adapter<ResultadoFiltroAnimalAdapter.ViewHolder> {

    public interface OnSeleccionCambioListener {
        void onSeleccionCambio();
    }

    private final List<Animal> lista;
    private final Set<String> idsSeleccionados = new HashSet<>();
    private OnSeleccionCambioListener listener;

    public ResultadoFiltroAnimalAdapter(List<Animal> lista) {
        this.lista = lista;

        for (Animal animal : lista) {
            idsSeleccionados.add(animal.getId());
        }
    }

    public void setOnSeleccionCambioListener(OnSeleccionCambioListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ResultadoFiltroAnimalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_animal_resultado_filtro, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultadoFiltroAnimalAdapter.ViewHolder holder, int position) {
        Animal animal = lista.get(position);

        holder.tvCrotal.setText(animal.getCrotal());

        String datos = "";

        if (animal.getSexo() != null) {
            datos += animal.getSexo();
        }

        if (animal.getRaza() != null && !animal.getRaza().isEmpty()) {
            datos += " · " + animal.getRaza();
        }

        if (animal.getFechaNacimiento() != null && !animal.getFechaNacimiento().isEmpty()) {
            datos += " · Nac: " + FechaUtils.formatearFecha(animal.getFechaNacimiento());
        }

        holder.tvDatos.setText(datos);

        holder.checkSeleccionado.setOnCheckedChangeListener(null);
        holder.checkSeleccionado.setChecked(idsSeleccionados.contains(animal.getId()));

        holder.checkSeleccionado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                idsSeleccionados.add(animal.getId());
            } else {
                idsSeleccionados.remove(animal.getId());
            }

            if (listener != null) {
                listener.onSeleccionCambio();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            boolean nuevoEstado = !holder.checkSeleccionado.isChecked();
            holder.checkSeleccionado.setChecked(nuevoEstado);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public List<Animal> getAnimalesSeleccionados() {
        List<Animal> seleccionados = new ArrayList<>();

        for (Animal animal : lista) {
            if (idsSeleccionados.contains(animal.getId())) {
                seleccionados.add(animal);
            }
        }

        return seleccionados;
    }

    public int getTotalSeleccionados() {
        return idsSeleccionados.size();
    }

    public int getTotalAnimales() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkSeleccionado;
        TextView tvCrotal, tvDatos;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkSeleccionado = itemView.findViewById(R.id.checkSeleccionado);
            tvCrotal = itemView.findViewById(R.id.tvCrotalResultado);
            tvDatos = itemView.findViewById(R.id.tvDatosResultado);
        }
    }
}