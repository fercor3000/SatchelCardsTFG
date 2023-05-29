package com.example.satchelcards;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SeleccionarTransport extends AppCompatActivity {

    ImageView goBackBtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ImageView imagen;
    TextView holderNombreTarjeta, nombreTarjeta, fechaExpiracion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seleccionar_dni);

        goBackBtn = (ImageView) findViewById(R.id.go_back);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeleccionarTransport.this, ListDNI.class);
                startActivity(intent);
            }
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        DocumentReference userRef = db.collection("user").document(email).collection("transport").document();
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String vCardHolderName = String.valueOf(document.getLong("cardHolderName"));
                        String vCardNombre = String.valueOf(document.getLong("cardName"));
                        String vFechaExpiracion = String.valueOf(document.getLong("expirationDate"));

                        if (vCardHolderName != null && vCardNombre != null && vFechaExpiracion != null) {
                            holderNombreTarjeta.setText(vCardHolderName); ;
                            nombreTarjeta.setText(vCardNombre);
                            fechaExpiracion.setText(vFechaExpiracion);
                            imagen.setImageResource(R.drawable.pictransportcard);
                        }
                    }
                }
            }
        });
    }
}
