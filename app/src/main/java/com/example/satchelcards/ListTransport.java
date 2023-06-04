package com.example.satchelcards;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ListTransport extends ClassBlockOrientation {

    ImageView gobackBtn;
    Button addCardBtn;
    private RecyclerView recyclerView;
    private ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_transport);
        Context context = getApplicationContext();

        //#region BUTTON BACK
        gobackBtn = (ImageView) findViewById(R.id.go_back);
        gobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListTransport.this, HomeMenu.class);
                startActivity(intent);
            }
        });
        addCardBtn = (Button) findViewById(R.id.addCardBtn);
        addCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListTransport.this, AddTransport.class);
                intent.putExtra("lista",true);
                startActivity(intent);
            }
        });
        //#endregion

        recyclerView = findViewById(R.id.recyclerView_transport);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Item> itemList = new ArrayList<>();

        //Coge el usuario autentificado y su email
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = auth.getCurrentUser().getEmail();

        //Coge todas las tarjetas
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("/user/" + email + "/transport/");

        //#region OBTENER LISTA DE TARJETAS
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        //POR CADA DOCUMENTO
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.HORIZONTAL
                            );
                            String documentId = document.getId();
                            String cardName = document.getData().get("cardName").toString();

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            String storagePath = "cardImages/";
                            String rute_storage_photo = storagePath + FirebaseAuth.getInstance().getCurrentUser().getUid() + "_transport_" + documentId;
                            StorageReference storageRef = storage.getReference().child(rute_storage_photo);
                            //ImageView imageView = findViewById(R.id.profile);

                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Item item = new Item(uri, cardName, documentId, "transport");
                                    itemList.add(item);
                                    itemsAdapter.notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Item item = new Item(null, cardName, documentId, "transport");
                                    itemList.add(item);
                                    itemsAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        itemsAdapter.notifyDataSetChanged();

                    }
                } else {
                    Toast.makeText(context, "Error!! Tarjetas no encontradas!", Toast.LENGTH_SHORT).show();
                }
            }
        });
//#endregion


        // Crear el adaptador y establecerlo en el RecyclerView
        itemsAdapter = new ItemsAdapter(itemList);
        recyclerView.setAdapter(itemsAdapter);
    }
}
