package com.example.satchelcards;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
/*
public class ItemList {
    protected ArrayList<Item> datos;
    
    ItemList(){

        for (:
             ) {
            
        }        
    }

    protected void obtenerItemBBDD() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        CollectionReference userRef = db.collection("user").document(email).collection(variable);


        userRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Item> cardList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String cardName = document.getString("cardName");
                        int cardNumber = document.getLong("cardNumber").intValue();

                        Item card = new Item(cardNumber, cardName);
                        cardList.add(card);
                    }

                    // Update your RecyclerView adapter with the retrieved data
                    ItemsAdapter adapter = new ItemsAdapter(cardList);
                    ItemList.setAdapter(adapter);
                } else {
                    Log.e("YourActivity", "Error getting documents: ", task.getException());
                }
            }
        });
        /*userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String tipoTarjeta = String.valueOf(document.getLong("cardtype"));
                        if (tipoTarjeta.equals("")) {
                            String vNombreTarjeta = String.valueOf(document.getLong("cardName"));
                            if (vNombreTarjeta != null) {
                                nombreTarjeta.setText(vNombreTarjeta);
                            }
                        }
                        if (tipoTarjeta.equals("")) {
                            String vNombreTarjeta = String.valueOf(document.getLong("cardName"));
                            if (vNombreTarjeta != null) {
                                nombreTarjeta.setText(vNombreTarjeta);
                            }
                        }
                        if (tipoTarjeta.equals("")) {
                            String vNombreTarjeta = String.valueOf(document.getLong("cardName"));
                            if (vNombreTarjeta != null) {
                                nombreTarjeta.setText(vNombreTarjeta);
                            }
                        }
                        if (tipoTarjeta.equals("")) {
                            String vNombreTarjeta = String.valueOf(document.getLong("cardName"));
                            if (vNombreTarjeta != null) {
                                nombreTarjeta.setText(vNombreTarjeta);
                            }
                        }
                    }
                }
            }
        });
    }
}*/
