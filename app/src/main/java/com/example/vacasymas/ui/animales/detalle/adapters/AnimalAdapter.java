package com.example.vacasymas.ui.animales.detalle.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.data.models.Animal;

import java.util.ArrayList;
import java.util.List;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder> {

    public interface OnAnimalClickListener {
        void onAnimalClick(Animal animal);
    }

    private final List<Animal> listaAnimales = new ArrayList<>();
    private final OnAnimalClickListener listener;


    public AnimalAdapter(OnAnimalClickListener listener) {
        this.listener = listener;
    }

    public void setLista(List<Animal> nuevaLista) {
        listaAnimales.clear();
        if (nuevaLista != null) {
            listaAnimales.addAll(nuevaLista);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_animal, parent, false);
        return new AnimalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, int position) {
        holder.bind(listaAnimales.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return listaAnimales.size();
    }

    static class AnimalViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvCrotal;
        private final TextView tvTipo;
        private final TextView tvFechaNacimiento;
        private final TextView tvCapa;
        private final TextView tvMotivoBaja;

        public AnimalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCrotal = itemView.findViewById(R.id.tvCrotal);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvFechaNacimiento = itemView.findViewById(R.id.tvFechaNacimiento);
            tvCapa = itemView.findViewById(R.id.tvCapa);
            tvMotivoBaja = itemView.findViewById(R.id.tvMotivoBaja);
        }

        public void bind(Animal animal, OnAnimalClickListener listener) {
            tvCrotal.setText(valorSeguro(animal.getCrotal()));
            tvTipo.setText("Tipo: " + valorSeguro(animal.getEstatusDescripcion()));
            tvFechaNacimiento.setText("Fecha nacimiento: " + valorSeguro(animal.getFechaNacimiento()));
            tvCapa.setText("Capa: " + valorSeguro(animal.getCapa()));

            String motivoBaja = animal.getFechaBajaExplotacion();
            if (motivoBaja == null || motivoBaja.trim().isEmpty()) {
                tvMotivoBaja.setVisibility(View.GONE);
            } else {
                tvMotivoBaja.setVisibility(View.VISIBLE);
                tvMotivoBaja.setText("Baja en explotación: " + motivoBaja);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAnimalClick(animal);
                }
            });
        }

        private String valorSeguro(String valor) {
            return valor == null || valor.trim().isEmpty() ? "-" : valor;
        }
    }


}