package com.example.satchelcards;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListDNI extends AppCompatActivity {

    ImageView gobackBtn;
    private RecyclerView recyclerView;
    private ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_dni);

        gobackBtn = (ImageView) findViewById(R.id.go_back);
        gobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListDNI.this, LogIn.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recyclerView_dni);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Item> itemList = new ArrayList<>();

        // Crear una consulta para obtener los datos de Firebase
        Query query = FirebaseDatabase.getInstance().getReference().child("items");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Recorrer los datos obtenidos de Firebase
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Obtener cada objeto Item y agregarlo a la lista
                    Item item = snapshot.getValue(Item.class);
                    itemList.add(item);
                }
                // Notificar al adaptador que los datos han cambiado
                itemsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error si es necesario
            }
        });

        // Crear el adaptador y establecerlo en el RecyclerView
        itemsAdapter = new ItemsAdapter(itemList);
        recyclerView.setAdapter(itemsAdapter);
    }
}
/*
En este ejemplo, se realiza una consulta a la base de datos de Firebase para obtener los datos de la referencia "items".
Luego, se utiliza un ValueEventListener para escuchar los cambios en los datos y agregarlos a la lista itemList.
Una vez que se obtienen todos los datos, se notifica al adaptador (itemsAdapter) que se han realizado cambios en los datos
mediante notifyDataSetChanged().

Asegúrate de tener correctamente configurada tu base de datos Firebase y que los datos se encuentren en la referencia "items".
Además, asegúrate de tener la configuración adecuada de Firebase en tu proyecto y de haber agregado las dependencias
necesarias en el archivo build.gradle de la app.
*/