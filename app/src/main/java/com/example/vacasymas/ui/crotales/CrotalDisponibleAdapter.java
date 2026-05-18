package com.example.vacasymas.ui.crotales;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.data.models.CrotalDisponible;

import java.util.ArrayList;
import java.util.List;

public class CrotalDisponibleAdapter
        extends RecyclerView.Adapter<CrotalDisponibleAdapter.CrotalViewHolder> {

    public interface OnCrotalLongClickListener {
        void onCrotalLongClick(CrotalDisponible crotal);
    }

    private final List<CrotalDisponible> lista = new ArrayList<>();
    private OnCrotalLongClickListener longClickListener;

    public void setOnCrotalLongClickListener(OnCrotalLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void actualizarLista(List<CrotalDisponible> nuevaLista) {
        lista.clear();
        if (nuevaLista != null) {
            lista.addAll(nuevaLista);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CrotalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crotal, parent, false);
        return new CrotalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CrotalViewHolder holder, int position) {
        CrotalDisponible crotal = lista.get(position);

        holder.tvCrotal.setText(crotal.getCrotal());
        holder.tvEstadoCrotal.setText(crotal.getEstado());

        String detalle = "Fecha asignación: " +
                (crotal.getFechaAsignacion() != null ? crotal.getFechaAsignacion() : "-");

        if ("USADO".equals(crotal.getEstado())) {
            detalle = "Fecha uso: " +
                    (crotal.getFechaUso() != null ? crotal.getFechaUso() : "-");
        } else if ("ANULADO".equals(crotal.getEstado())) {
            detalle = "Anulado";
            if (crotal.getObservaciones() != null && !crotal.getObservaciones().trim().isEmpty()) {
                detalle += " | " + crotal.getObservaciones();
            }
        }

        holder.tvDetalleCrotal.setText(detalle);

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onCrotalLongClick(crotal);
            }
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(
                    v.getContext(),
                    crotal.getCrotal(),
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class CrotalViewHolder extends RecyclerView.ViewHolder {

        TextView tvCrotal;
        TextView tvEstadoCrotal;
        TextView tvDetalleCrotal;

        public CrotalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCrotal = itemView.findViewById(R.id.tvCrotal);
            tvEstadoCrotal = itemView.findViewById(R.id.tvEstadoCrotal);
            tvDetalleCrotal = itemView.findViewById(R.id.tvDetalleCrotal);
        }
    }
}
