package com.example.satchelcards;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class SeleccionarGift extends ClassBlockOrientation {

    ImageView goBackBtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ImageView imagen;
    Button editBtn, deleteBtn, notesBtn;
    Date vFechaExpiracion;

    String formattedExpirationDate, vNombreTarjeta, vHolderTarjeta, imageUri;
    TextView nombreTarjeta, holderTarjeta, fechaExpiracion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seleccionar_gift);

        imagen = findViewById(R.id.imagen_tarjeta);
        nombreTarjeta = findViewById(R.id.nombre_tarjeta_gift);
        holderTarjeta = findViewById(R.id.nombre_titular_gift);
        fechaExpiracion = findViewById(R.id.fecha_validez_gift);

        goBackBtn = (ImageView) findViewById(R.id.go_back);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeleccionarGift.this, ListGift.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        String itemId = intent.getStringExtra("itemId");
        imageUri = intent.getStringExtra("imageUri");

        if (imageUri.equals("nada")) {
            imagen.setImageResource(R.drawable.picgiftcard);
        } else {
            Picasso.get().load(imageUri).into(imagen);
        }


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        DocumentReference userRef = db.collection("user").document(email).collection("loyalty").document(itemId);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        vNombreTarjeta = String.valueOf(document.getString("cardName"));
                        vHolderTarjeta = String.valueOf(document.getString("cardHolderName"));
                        vFechaExpiracion = document.getDate("expirationDate");
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        formattedExpirationDate = formatter.format(vFechaExpiracion);
                        try {
                            vFechaExpiracion = formatter.parse(formattedExpirationDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (vNombreTarjeta != null && vHolderTarjeta != null && vFechaExpiracion != null) {
                            nombreTarjeta.setText(vNombreTarjeta);
                            holderTarjeta.setText(vHolderTarjeta);
                            fechaExpiracion.setText(formattedExpirationDate);
                            //imagen.setImageResource(R.drawable.picgiftcard);
                        }
                    }
                }
            }
        });

        deleteBtn = (Button) findViewById(R.id.delete_button);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String email = currentUser.getEmail();
                DocumentReference cardRef = db.collection("user").document(email).collection("loyalty").document(itemId);

                cardRef.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Tarjeta borrada con exito
                                Context context = getApplicationContext();
                                Toast.makeText(context, "Tarjeta eliminada", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SeleccionarGift.this, HomeMenu.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error al borrar tarjeta
                                Context context = getApplicationContext();
                                Toast.makeText(context, "Error al eliminar la tarjeta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("AddGift", "Error deleting card in Firestore", e);
                            }
                        });
            }
        });

        editBtn = findViewById(R.id.edit_button);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeleccionarGift.this, AddGift.class);
                intent.putExtra("cardName", vNombreTarjeta);
                intent.putExtra("cardHolderName", vHolderTarjeta);
                intent.putExtra("expirationDate", vFechaExpiracion);
                intent.putExtra("itemId",itemId);
                intent.putExtra("imageUri",imageUri);
                intent.putExtra("operation","edit");
                startActivity(intent);
            }
        });

        notesBtn = findViewById(R.id.notes_button);

        notesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeleccionarGift.this, CardNotes.class);
                intent.putExtra("itemId",itemId);
                intent.putExtra("imageUri",imageUri);
                intent.putExtra("cardType","loyalty");
                startActivity(intent);
            }
        });

    }}


