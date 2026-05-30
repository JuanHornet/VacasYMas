package com.example.vacasymas.ui.cercados;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.data.models.CensoCercado;
import com.example.vacasymas.data.models.CercadoConCenso;

import java.util.List;

public class CercadoOperativoAdapter extends RecyclerView.Adapter<CercadoOperativoAdapter.ViewHolder> {

    public interface OnCercadoOperativoClickListener {
        void onRegistrarCenso(CercadoConCenso item);
        void onVerHistorial(CercadoConCenso item);
    }

    private final List<CercadoConCenso> lista;
    private final OnCercadoOperativoClickListener listener;

    public CercadoOperativoAdapter(List<CercadoConCenso> lista, OnCercadoOperativoClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CercadoOperativoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cercado_operativo, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CercadoOperativoAdapter.ViewHolder holder, int position) {
        CercadoConCenso item = lista.get(position);
        CensoCercado censo = item.getUltimoCenso();

        holder.tvNombreCercado.setText(item.getCercado().getNombre());

        if (censo == null) {
            holder.tvFechaCenso.setText("Sin censo registrado");
            holder.tvTotalAnimales.setText("0");
            holder.tvDetalleAnimales.setText("Vacas 0 · Terneros 0 · Toros 0 · Novillas 0");
        } else {
            holder.tvFechaCenso.setText("Último censo: " + censo.getFecha());
            holder.tvTotalAnimales.setText(String.valueOf(censo.getTotal()));

            holder.tvDetalleAnimales.setText(
                    "Vacas " + censo.getVacas() +
                            " · Terneros " + censo.getTerneros() +
                            " · Toros " + censo.getToros() +
                            " · Novillas " + censo.getNovillas()
            );
        }

        holder.btnRegistrarCenso.setOnClickListener(v -> listener.onRegistrarCenso(item));
        holder.itemView.setOnClickListener(v -> listener.onVerHistorial(item));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreCercado;
        TextView tvFechaCenso;
        TextView tvTotalAnimales;
        TextView tvDetalleAnimales;
        TextView btnRegistrarCenso;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombreCercado = itemView.findViewById(R.id.tvNombreCercado);
            tvFechaCenso = itemView.findViewById(R.id.tvFechaCenso);
            tvTotalAnimales = itemView.findViewById(R.id.tvTotalAnimales);
            tvDetalleAnimales = itemView.findViewById(R.id.tvDetalleAnimales);
            btnRegistrarCenso = itemView.findViewById(R.id.btnRegistrarCenso);
        }
    }
}
