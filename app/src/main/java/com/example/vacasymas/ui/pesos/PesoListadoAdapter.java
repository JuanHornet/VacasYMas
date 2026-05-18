package com.example.vacasymas.ui.pesos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.data.models.PesoAnimal;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PesoListadoAdapter extends RecyclerView.Adapter<PesoListadoAdapter.ViewHolder> {

    public interface PesoListener {
        void onEliminar(PesoAnimal peso);
        void onSeleccionCambiada();
    }

    private final List<PesoAnimal> lista = new ArrayList<>();
    private final Set<String> seleccionados = new HashSet<>();
    private final PesoListener listener;

    public PesoListadoAdapter(PesoListener listener) {
        this.listener = listener;
    }

    public void actualizarLista(List<PesoAnimal> nuevaLista) {
        lista.clear();
        seleccionados.clear();

        if (nuevaLista != null) {
            lista.addAll(nuevaLista);
        }

        notifyDataSetChanged();

        if (listener != null) {
            listener.onSeleccionCambiada();
        }
    }

    public List<PesoAnimal> obtenerSeleccionados() {
        List<PesoAnimal> resultado = new ArrayList<>();

        for (PesoAnimal p : lista) {
            if (seleccionados.contains(p.getId())) {
                resultado.add(p);
            }
        }

        return resultado;
    }

    @NonNull
    @Override
    public PesoListadoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_peso_listado, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PesoListadoAdapter.ViewHolder holder, int position) {
        PesoAnimal peso = lista.get(position);

        holder.tvCrotal.setText(peso.getCrotal() != null ? peso.getCrotal() : "-");
        holder.tvPeso.setText((peso.getPeso() != null ? peso.getPeso() : 0) + " kg");

        boolean seleccionado = seleccionados.contains(peso.getId());
        holder.cbSeleccionado.setOnCheckedChangeListener(null);
        holder.cbSeleccionado.setChecked(seleccionado);

        if (seleccionado) {
            holder.cardPeso.setStrokeWidth(3);
            holder.cardPeso.setStrokeColor(0xFF1D6F58);
        } else {
            holder.cardPeso.setStrokeWidth(0);
        }

        holder.cbSeleccionado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                seleccionados.add(peso.getId());
            } else {
                seleccionados.remove(peso.getId());
            }

            notifyItemChanged(position);

            if (listener != null) {
                listener.onSeleccionCambiada();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            boolean nuevoEstado = !seleccionados.contains(peso.getId());

            if (nuevoEstado) {
                seleccionados.add(peso.getId());
            } else {
                seleccionados.remove(peso.getId());
            }

            notifyItemChanged(position);

            if (listener != null) {
                listener.onSeleccionCambiada();
            }
        });

        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEliminar(peso);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardPeso;
        TextView tvCrotal, tvPeso;
        CheckBox cbSeleccionado;
        ImageView btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardPeso = itemView.findViewById(R.id.cardPeso);
            tvCrotal = itemView.findViewById(R.id.tvCrotal);
            tvPeso = itemView.findViewById(R.id.tvPeso);
            cbSeleccionado = itemView.findViewById(R.id.cbSeleccionado);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
