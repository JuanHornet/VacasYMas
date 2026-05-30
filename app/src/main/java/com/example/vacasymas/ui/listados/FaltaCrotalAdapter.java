package com.example.vacasymas.ui.listados;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.data.models.Animal;

import java.util.List;

public class FaltaCrotalAdapter extends RecyclerView.Adapter<FaltaCrotalAdapter.ViewHolder> {

    public interface OnAnimalClickListener {
        void onAnimalClick(Animal animal);
    }

    private final List<Animal> lista;
    private final OnAnimalClickListener listener;

    public FaltaCrotalAdapter(List<Animal> lista, OnAnimalClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FaltaCrotalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_falta_crotal, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull FaltaCrotalAdapter.ViewHolder holder, int position) {
        Animal animal = lista.get(position);

        holder.tvCrotal.setText(valorSeguro(animal.getCrotal()));

        String datos = "";

        if (animal.getEstatusDescripcion() != null) {
            datos += animal.getEstatusDescripcion();
        }

        if (animal.getRaza() != null && !animal.getRaza().trim().isEmpty()) {
            datos += " · " + animal.getRaza();
        }

        if (animal.getCercado() != null && !animal.getCercado().trim().isEmpty()) {
            datos += " · " + animal.getCercado();
        }

        holder.tvDatos.setText(datos);

        if (Boolean.TRUE.equals(animal.getCrotalIzquierdoPresente())) {
            holder.ivIzquierdo.setImageResource(R.drawable.ic_crotal_presente);
        } else {
            holder.ivIzquierdo.setImageResource(R.drawable.ic_falta_crotal);
        }

        if (Boolean.TRUE.equals(animal.getCrotalDerechoPresente())) {
            holder.ivDerecho.setImageResource(R.drawable.ic_crotal_presente);
        } else {
            holder.ivDerecho.setImageResource(R.drawable.ic_falta_crotal);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAnimalClick(animal);
            }
        });

        holder.ivIzquierdo.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAnimalClick(animal);
            }
        });

        holder.ivDerecho.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAnimalClick(animal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    private String valorSeguro(String valor) {
        return valor == null || valor.trim().isEmpty() ? "-" : valor;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCrotal, tvDatos;
        ImageView ivIzquierdo, ivDerecho;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCrotal = itemView.findViewById(R.id.tvCrotalFalta);
            tvDatos = itemView.findViewById(R.id.tvDatosFalta);
            ivIzquierdo = itemView.findViewById(R.id.ivCrotalIzquierdoFalta);
            ivDerecho = itemView.findViewById(R.id.ivCrotalDerechoFalta);
        }
    }
}
