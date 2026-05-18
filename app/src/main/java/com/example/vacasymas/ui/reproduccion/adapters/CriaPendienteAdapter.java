package com.example.vacasymas.ui.reproduccion.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.data.models.EventoReproductivo;

import java.util.ArrayList;
import java.util.List;

public class CriaPendienteAdapter extends RecyclerView.Adapter<CriaPendienteAdapter.ViewHolder> {

    public interface OnCriaPendienteClickListener {
        void onCriaClick(EventoReproductivo evento);
    }

    private final List<EventoReproductivo> lista = new ArrayList<>();
    private final OnCriaPendienteClickListener listener;

    public CriaPendienteAdapter(OnCriaPendienteClickListener listener) {
        this.listener = listener;
    }

    public void actualizarLista(List<EventoReproductivo> nuevaLista) {
        lista.clear();

        if (nuevaLista != null) {
            lista.addAll(nuevaLista);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CriaPendienteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cria_pendiente, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CriaPendienteAdapter.ViewHolder holder, int position) {
        EventoReproductivo evento = lista.get(position);
        holder.bind(evento, listener);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMadre, tvFechaParto, tvDiasPendiente, tvSexoEstimado, tvObservaciones;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMadre = itemView.findViewById(R.id.tvMadre);
            tvFechaParto = itemView.findViewById(R.id.tvFechaParto);
            tvDiasPendiente = itemView.findViewById(R.id.tvDiasPendiente);
            tvSexoEstimado = itemView.findViewById(R.id.tvSexoEstimado);
            tvObservaciones = itemView.findViewById(R.id.tvObservaciones);
        }

        void bind(EventoReproductivo evento, OnCriaPendienteClickListener listener) {
            tvMadre.setText("Madre: " + valorSeguro(evento.getCrotalMadre()));
            tvFechaParto.setText("Fecha parto: " + FechaUtils.formatearFecha(evento.getFechaEvento()));
            tvDiasPendiente.setText("Días pendiente: " + calcularDiasPendiente(evento.getFechaEvento()));
            tvSexoEstimado.setText("Sexo estimado: " + valorSeguro(evento.getSexoEstimado()));

            String obs = evento.getObservaciones();
            if (obs == null || obs.trim().isEmpty()) {
                tvObservaciones.setVisibility(View.GONE);
            } else {
                tvObservaciones.setVisibility(View.VISIBLE);
                tvObservaciones.setText(obs);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCriaClick(evento);
                }
            });
        }

        private String valorSeguro(String valor) {
            return valor == null || valor.trim().isEmpty() ? "-" : valor;
        }

        private long calcularDiasPendiente(String fechaIso) {
            try {
                java.text.SimpleDateFormat sdf =
                        new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());

                java.util.Date fecha = sdf.parse(fechaIso);
                java.util.Date hoy = new java.util.Date();

                long diferencia = hoy.getTime() - fecha.getTime();

                return Math.max(0, diferencia / (1000 * 60 * 60 * 24));

            } catch (Exception e) {
                return 0;
            }
        }
    }
}
