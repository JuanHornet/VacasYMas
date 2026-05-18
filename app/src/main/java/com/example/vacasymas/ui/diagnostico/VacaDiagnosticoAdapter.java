package com.example.vacasymas.ui.diagnostico;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.base.AnimalUtils;
import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.data.models.Animal;
import com.example.vacasymas.data.models.DiagnosticoGestacion;

import java.util.ArrayList;

public class VacaDiagnosticoAdapter extends RecyclerView.Adapter<VacaDiagnosticoAdapter.VacaViewHolder> {

    private final ArrayList<VacaDiagnosticoItem> lista;
    private final OnVacaClickListener listener;

    public interface OnVacaClickListener {
        void onVacaClick(Animal animal);
    }

    public VacaDiagnosticoAdapter(ArrayList<VacaDiagnosticoItem> lista, OnVacaClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VacaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vaca_diagnostico, parent, false);

        return new VacaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VacaViewHolder holder, int position) {
        VacaDiagnosticoItem item = lista.get(position);
        Animal vaca = item.getAnimal();
        DiagnosticoGestacion ultimo = item.getUltimoDiagnostico();

        holder.tvCrotalVaca.setText(valorSeguro(vaca.getCrotal()));

        holder.tvEdadVaca.setText(valorSeguro(AnimalUtils.calcularEdad(vaca.getFechaNacimiento()))

        );

        holder.tvEstadoVaca.setText("Estado: " + valorSeguro(vaca.getEstadoReproductivoMostrar()));
        holder.tvEstadoVaca.setTextColor(obtenerColorEstado(vaca.getEstadoReproductivo()));

        if (ultimo != null) {
            holder.tvUltimoDiagnostico.setText(
                    "Últ. diagnóstico: " +
                            valorSeguro(ultimo.getResultado()) +
                            " · " +
                            FechaUtils.formatearFecha(ultimo.getFecha())
            );
        } else {
            holder.tvUltimoDiagnostico.setText("Último diagnóstico: -");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVacaClick(vaca);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class VacaViewHolder extends RecyclerView.ViewHolder {

        TextView tvCrotalVaca, tvEdadVaca, tvEstadoVaca, tvUltimoDiagnostico;

        public VacaViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCrotalVaca = itemView.findViewById(R.id.tvCrotalVaca);
            tvEdadVaca = itemView.findViewById(R.id.tvEdadVaca);
            tvEstadoVaca = itemView.findViewById(R.id.tvEstadoVaca);
            tvUltimoDiagnostico = itemView.findViewById(R.id.tvUltimoDiagnostico);
        }
    }

    private int obtenerColorEstado(String estado) {
        if (estado == null) return 0xFF9E9E9E;

        switch (estado.toLowerCase()) {
            case "preñada":
            case "prenada":
                return 0xFF2E7D32;

            case "vacia":
            case "vacía":
                return 0xFFD32F2F;

            case "cubierta":
                return 0xFFF9A825;

            case "nada":
            default:
                return 0xFF9E9E9E;
        }
    }

    private String valorSeguro(String valor) {
        return valor == null || valor.trim().isEmpty() ? "-" : valor;
    }
}