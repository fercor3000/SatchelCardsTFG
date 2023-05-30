package com.example.satchelcards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListGift extends AppCompatActivity{

    ImageView gobackBtn;
    private RecyclerView recyclerView;
    private ItemsAdapter itemsAdapter;
    Boolean noFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_gift);
        Context context = getApplicationContext();

        //#region BUTTON BACK
        gobackBtn = (ImageView) findViewById(R.id.go_back);
        gobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListGift.this, HomeMenu.class);
                startActivity(intent);
            }
        });
        //#endregion

        recyclerView = findViewById(R.id.recyclerView_gift);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Item> itemList = new ArrayList<>();

        //Coge el usuario autentificado y su email
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = auth.getCurrentUser().getEmail();

        //Coge todas las tarjetas
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("/user/" + email + "/loyalty/");

        //#region OBTENER LISTA DE TARJETAS
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        //POR CADA DOCUMENTO
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            ImageView imageView = new ImageView(context);
                            // Resto del código...

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.HORIZONTAL
                            );
                            String documentId = document.getId();
                            String cardName = document.getData().get("cardName").toString();
                            Item item = new Item(imageView, cardName, documentId);
                            
                            itemList.add(item);
                        }
                        // Notificar al adaptador que los datos han cambiado
                        itemsAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(context, "Error!! DNI no encontrado!", Toast.LENGTH_SHORT).show();
                }
            }
        });
//#endregion


        // Crear el adaptador y establecerlo en el RecyclerView
        itemsAdapter = new ItemsAdapter(itemList);
        recyclerView.setAdapter(itemsAdapter);
    }
}
