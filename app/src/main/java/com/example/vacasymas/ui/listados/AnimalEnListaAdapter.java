package com.example.vacasymas.ui.listados;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.data.models.AnimalEnLista;

import java.util.ArrayList;
import java.util.List;

public class AnimalEnListaAdapter extends RecyclerView.Adapter<AnimalEnListaAdapter.ViewHolder> {

    public interface OnAnimalListaClickListener {
        void onEliminarClick(AnimalEnLista animal);
        void onMarcadoLongClick(AnimalEnLista animal);
    }

    private final List<AnimalEnLista> lista = new ArrayList<>();
    private final OnAnimalListaClickListener listener;

    public AnimalEnListaAdapter(OnAnimalListaClickListener listener) {
        this.listener = listener;
    }

    public void actualizarLista(List<AnimalEnLista> nuevaLista) {
        lista.clear();
        if (nuevaLista != null) {
            lista.addAll(nuevaLista);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnimalEnListaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_animal_en_lista, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalEnListaAdapter.ViewHolder holder, int position) {
        AnimalEnLista animal = lista.get(position);

        holder.tvCrotal.setText(animal.getCrotal());

        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEliminarClick(animal);
            }
        });

        String sexo = animal.getSexo() != null ? animal.getSexo().trim() : "";

        if (sexo.equalsIgnoreCase("Macho")) {

            holder.tvSexoAnimal.setText("♂");
            holder.tvSexoAnimal.setTextColor(Color.parseColor("#1565C0"));
            holder.tvCrotal.setTextColor(Color.parseColor("#1565C0"));

        } else if (sexo.equalsIgnoreCase("Hembra")) {

            holder.tvSexoAnimal.setText("♀");
            holder.tvSexoAnimal.setTextColor(Color.parseColor("#C2185B"));
            holder.tvCrotal.setTextColor(Color.parseColor("#C2185B"));

        } else {

            holder.tvSexoAnimal.setText("?");
            holder.tvSexoAnimal.setTextColor(Color.GRAY);
            holder.tvCrotal.setTextColor(Color.BLACK);
        }
        if (animal.getMarcado() == 1) {
            holder.itemView.setBackgroundColor(Color.parseColor("#DFF3E3"));
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onMarcadoLongClick(animal);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvSexoAnimal, tvCrotal;
        ImageButton btnEliminar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSexoAnimal = itemView.findViewById(R.id.tvSexoAnimal);
            tvCrotal = itemView.findViewById(R.id.tvCrotalLista);
            btnEliminar = itemView.findViewById(R.id.btnEliminarAnimalLista);
        }
    }
}
