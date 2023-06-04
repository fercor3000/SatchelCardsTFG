package com.example.satchelcards;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class ListAccess extends AppCompatActivity {

    ImageView gobackBtn;
    Button addCardBtn;
    private RecyclerView recyclerView;
    private ItemsAdapter itemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_access);

        gobackBtn = (ImageView) findViewById(R.id.go_back);
        gobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListAccess.this, HomeMenu.class);
                startActivity(intent);
            }
        });
        addCardBtn = (Button) findViewById(R.id.addCardBtn);
        addCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListAccess.this, AddAccess.class);
                intent.putExtra("lista",true);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recyclerView_access);
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