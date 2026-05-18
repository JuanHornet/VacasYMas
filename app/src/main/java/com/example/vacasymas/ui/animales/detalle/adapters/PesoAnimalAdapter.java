package com.example.vacasymas.ui.animales.detalle.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.data.models.PesoAnimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PesoAnimalAdapter extends RecyclerView.Adapter<PesoAnimalAdapter.PesoViewHolder> {


    private final List<PesoAnimal> lista = new ArrayList<>();

    private OnPesoLongClickListener listener;

    public void actualizarLista(List<PesoAnimal> nuevaLista) {
        lista.clear();

        if (nuevaLista != null) {
            lista.addAll(nuevaLista);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PesoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_peso_animal, parent, false);

        return new PesoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PesoViewHolder holder, int position) {
        PesoAnimal peso = lista.get(position);

        int pesoKg = 0;

        if (peso.getPeso() != null) {
            pesoKg = peso.getPeso();
        }

        holder.tvPeso.setText(String.format(
                Locale.getDefault(),
                "%d kg",
                pesoKg
        ));

        holder.tvFechaPeso.setText(FechaUtils.formatearFecha(peso.getFecha()));

        String obs = peso.getObservaciones();

        if (obs == null || obs.trim().isEmpty()) {
            holder.tvObservacionesPeso.setVisibility(View.GONE);
        } else {
            holder.tvObservacionesPeso.setVisibility(View.VISIBLE);
            holder.tvObservacionesPeso.setText(obs);
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onPesoLongClick(peso);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class PesoViewHolder extends RecyclerView.ViewHolder {

        TextView tvPeso, tvFechaPeso, tvObservacionesPeso;

        public PesoViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPeso = itemView.findViewById(R.id.tvPeso);
            tvFechaPeso = itemView.findViewById(R.id.tvFechaPeso);
            tvObservacionesPeso = itemView.findViewById(R.id.tvObservacionesPeso);
        }
    }

    public interface OnPesoLongClickListener {
        void onPesoLongClick(PesoAnimal peso);
    }

    public void setOnPesoLongClickListener(OnPesoLongClickListener listener) {
        this.listener = listener;
    }
}
