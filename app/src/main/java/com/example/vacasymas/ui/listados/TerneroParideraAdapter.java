package com.example.vacasymas.ui.listados;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.base.FechaUtils;
import com.example.vacasymas.data.models.Animal;
import com.example.vacasymas.ui.animales.DetalleAnimalActivity;

import java.util.List;

public class TerneroParideraAdapter extends RecyclerView.Adapter<TerneroParideraAdapter.ViewHolder> {

    private final List<Animal> lista;

    public TerneroParideraAdapter(List<Animal> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public TerneroParideraAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ternero_paridera, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull TerneroParideraAdapter.ViewHolder holder, int position) {
        Animal animal = lista.get(position);

        android.util.Log.d(
                "TerneroParidera",
                "crotal=" + animal.getCrotal()
                        + " sexo=" + animal.getSexo()
                        + " estatus=" + animal.getEstatus()
        );

        holder.tvCrotalTernero.setText(animal.getCrotal());


        String sexo = animal.getSexo() != null
                ? animal.getSexo().trim().toLowerCase()
                : "";

        Integer estatus = animal.getEstatus();

        if (sexo.contains("macho") || (estatus != null && estatus == 10001)) {

            holder.tvIconoSexo.setText("♂");
            holder.tvIconoSexo.setTextColor(Color.parseColor("#1976D2"));

            holder.tvCrotalTernero.setTextColor(Color.parseColor("#1976D2"));

        } else if (sexo.contains("hembra") || (estatus != null && estatus == 10002)) {

            holder.tvIconoSexo.setText("♀");
            holder.tvIconoSexo.setTextColor(Color.parseColor("#D32F2F"));

            holder.tvCrotalTernero.setTextColor(Color.parseColor("#D32F2F"));

        } else {
            holder.tvIconoSexo.setText("?");
            holder.tvIconoSexo.setTextColor(Color.parseColor("#757575"));
        }

        String fecha = FechaUtils.formatearFecha(animal.getFechaNacimiento());

        String madre = "-";
        if (animal.getCrotalMadre() != null && !animal.getCrotalMadre().isEmpty()) {
            madre = animal.getCrotalMadre();
        }

        holder.tvDatosTernero.setText(
                fecha + " · " + madre
        );

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetalleAnimalActivity.class);
            intent.putExtra("id_animal", animal.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvIconoSexo;
        TextView tvCrotalTernero;
        TextView tvDatosTernero;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvIconoSexo = itemView.findViewById(R.id.tvIconoSexo);
            tvCrotalTernero = itemView.findViewById(R.id.tvCrotalTernero);
            tvDatosTernero = itemView.findViewById(R.id.tvDatosTernero);
        }
    }
}
