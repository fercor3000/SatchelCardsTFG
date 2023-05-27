package com.example.satchelcards;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    private List<Item> itemList; // La lista de elementos que se mostrarán

    // Constructor para el adaptador
    public ItemsAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    // Clase ViewHolder para representar cada elemento en el RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView nombreTarjeta;

        public ViewHolder(View itemView) {
            super(itemView);
            // Obtén las referencias a las vistas dentro del elemento de la lista
            imageView = itemView.findViewById(R.id.imageView);
            nombreTarjeta = itemView.findViewById(R.id.nombre_tarjeta);
        }
    }

    // Crea las vistas para los elementos individuales
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Obtén el layout actual del RecyclerView
        int layoutResId = 0;
        if (parent.getId() == R.id.recyclerView_dni) {
            layoutResId = R.layout.list_dni;
        } else if (parent.getId() == R.id.recyclerView_transport) {
            layoutResId = R.layout.list_transport;
        } else if (parent.getId() == R.id.recyclerView_credit) {
            layoutResId = R.layout.list_credit_card;
        }

        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ViewHolder(itemView);
    }

    // Vincula los datos a las vistas en cada elemento
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = itemList.get(position);

        // Configura los datos en las vistas
        holder.imageView.setImageResource(item.getImageResId());
        holder.nombreTarjeta.setText(item.getTitulo());
    }

    // Devuelve la cantidad de elementos en la lista
    @Override
    public int getItemCount() {
        return itemList.size();
    }
}


