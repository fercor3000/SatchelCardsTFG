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

public class SeleccionarCredit extends AppCompatActivity {

    ImageView goBackBtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ImageView imagen;
    TextView nombreTarjeta, numeroTarjeta, holderTarjeta, fechaExpiracion, cvv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seleccionar_dni);

        goBackBtn = (ImageView) findViewById(R.id.go_back);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeleccionarCredit.this, ListDNI.class);
                startActivity(intent);
            }
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        DocumentReference userRef = db.collection("user").document(email).collection("payment").document();

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String vNombreTarjeta = document.getString("cardName");
                        String vHolderTarjeta = document.getString("cardHolderName");
                        String vFechaExpiracion = String.valueOf(document.getLong("expirationDate"));
                        String vNumeroTarjeta = String.valueOf(document.getLong("expirationDate"));
                        String vIssuer = String.valueOf(document.getString("issuer"));
                        String vCVV = String.valueOf(document.getLong("CVV"));

                        if (vNombreTarjeta != null && vHolderTarjeta != null && vFechaExpiracion != null && vNumeroTarjeta != null && vCVV != null) {
                            nombreTarjeta.setText(vNombreTarjeta);
                            holderTarjeta.setText(vHolderTarjeta);
                            numeroTarjeta.setText(vNumeroTarjeta);
                            cvv.setText(vCVV);
                            fechaExpiracion.setText(vFechaExpiracion);
                            switch(vIssuer){
                                case "MasterCard":
                                    imagen.setImageResource(R.drawable.mastercard_logo);
                                    break;
                                case "Visa":
                                    imagen.setImageResource(R.drawable.visa_logo);
                                    break;
                                case "Amex":
                                    imagen.setImageResource(R.drawable.amex_logo);
                                case "Discover":
                                    imagen.setImageResource(R.drawable.discover_logo);
                                case "UnionPay":
                                    imagen.setImageResource(R.drawable.unionpay_logo);
                                    break;
                                default:
                                    imagen.setImageResource(R.drawable.baseline_credit_card_24);
                            }

                        }
                    }
                }
            }
        });
    }
}

