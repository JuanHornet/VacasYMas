package com.example.vacasymas.ui.listados;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacasymas.R;
import com.example.vacasymas.data.models.ListaAnimal;

import java.util.ArrayList;
import java.util.List;

public class ListaManualAdapter extends RecyclerView.Adapter<ListaManualAdapter.ViewHolder> {

    public interface OnListaClickListener {
        void onListaClick(ListaAnimal lista);
        void onEditarClick(ListaAnimal lista);
        void onEliminarClick(ListaAnimal lista);
    }

    private final List<ListaAnimal> listas = new ArrayList<>();
    private final OnListaClickListener listener;

    public ListaManualAdapter(OnListaClickListener listener) {
        this.listener = listener;
    }

    public void actualizar(List<ListaAnimal> nuevas) {
        listas.clear();
        listas.addAll(nuevas);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lista_manual, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ListaAnimal lista = listas.get(position);

        holder.tvNombre.setText(lista.getNombre());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onListaClick(lista);
        });

        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) listener.onEditarClick(lista);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) listener.onEliminarClick(lista);
        });
    }

    @Override
    public int getItemCount() {
        return listas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre;
        ImageButton btnEditar, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tvNombreLista);
            btnEditar = itemView.findViewById(R.id.btnEditarLista);
            btnEliminar = itemView.findViewById(R.id.btnEliminarLista);
        }
    }
}
