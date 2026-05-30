package com.example.vacasymas.ui.cercados;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.data.models.Cercado;

import java.util.List;

public class CercadoAdapter extends RecyclerView.Adapter<CercadoAdapter.CercadoViewHolder> {

    public interface OnCercadoClickListener {
        void onClick(Cercado cercado);
        void onLongClick(Cercado cercado);
    }

    private final List<Cercado> lista;
    private final OnCercadoClickListener listener;

    public CercadoAdapter(List<Cercado> lista, OnCercadoClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CercadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_cercado, parent, false);

        return new CercadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CercadoViewHolder holder, int position) {
        Cercado cercado = lista.get(position);

        holder.tvNombreCercado.setText(cercado.getNombre());

        String tipo = cercado.getTipo();
        if (tipo == null || tipo.trim().isEmpty()) {
            tipo = "Sin tipo";
        }

        String superficie = "";
        if (cercado.getSuperficieHa() != null) {
            superficie = " · " + cercado.getSuperficieHa() + " ha";
        }

        holder.tvDetalleCercado.setText(tipo + superficie);

        String observaciones = cercado.getObservaciones();
        if (observaciones == null || observaciones.trim().isEmpty()) {
            holder.tvObservacionesCercado.setVisibility(View.GONE);
        } else {
            holder.tvObservacionesCercado.setVisibility(View.VISIBLE);
            holder.tvObservacionesCercado.setText(observaciones);
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(cercado));

        holder.itemView.setOnLongClickListener(v -> {
            listener.onLongClick(cercado);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class CercadoViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreCercado;
        TextView tvDetalleCercado;
        TextView tvObservacionesCercado;

        public CercadoViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombreCercado = itemView.findViewById(R.id.tvNombreCercado);
            tvDetalleCercado = itemView.findViewById(R.id.tvDetalleCercado);
            tvObservacionesCercado = itemView.findViewById(R.id.tvObservacionesCercado);
        }
    }
}