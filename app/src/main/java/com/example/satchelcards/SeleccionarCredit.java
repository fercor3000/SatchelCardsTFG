package com.example.satchelcards;

import android.content.Context;
import android.content.Intent;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SeleccionarCredit extends ClassBlockOrientation {

    ImageView goBackBtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ImageView imagen;
    Button editBtn, deleteBtn, notesBtn;
    Date vFechaExpiracion;

    String formattedExpirationDate, nombre_tarjeta, numero_tarjeta, dueño, cvv;
    TextView nombreTarjeta, numeroTarjeta, fechaExpiracion, dueñoTarjeta, cvvTarjeta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seleccionar_credit_card);

        imagen = findViewById(R.id.imagen_tarjeta);
        nombreTarjeta = findViewById(R.id.nombre_tarjeta);
        numeroTarjeta = findViewById(R.id.numero_tarjeta);
        dueñoTarjeta = findViewById(R.id.dueño);
        cvvTarjeta = findViewById(R.id.cvv);
        fechaExpiracion = findViewById(R.id.fecha_expiracion);

        goBackBtn = (ImageView) findViewById(R.id.go_back);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeleccionarCredit.this, ListCreditCard.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        String itemId = intent.getStringExtra("itemId");


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        DocumentReference userRef = db.collection("user").document(email).collection("payment").document(itemId);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        nombre_tarjeta = String.valueOf(document.getString("cardName"));
                        numero_tarjeta = String.valueOf(document.get("cardNumber"));
                        char firstNumberIssuer = numero_tarjeta.charAt(0);
                        switch (firstNumberIssuer) {
                            case '2':
                            case '5':
                                imagen.setImageResource(R.drawable.mastercard_logo);
                                break;
                            case '3':
                                imagen.setImageResource(R.drawable.amex_logo);
                                break;
                            case '4':
                                imagen.setImageResource(R.drawable.visa_logo);
                                break;
                            case '6':
                                imagen.setImageResource(R.drawable.discover_logo);
                                break;
                            case '8':
                                imagen.setImageResource(R.drawable.unionpay_logo);
                                break;
                            default:
                                imagen.setImageResource(R.drawable.piccreditcard);
                        }
                        dueño = String.valueOf(document.getString("cardHolderName"));
                        cvv =  String.valueOf(document.get("CVV"));
                        vFechaExpiracion = document.getDate("expirationDate");
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        formattedExpirationDate = formatter.format(vFechaExpiracion);
                        try {
                            vFechaExpiracion = formatter.parse(formattedExpirationDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (nombre_tarjeta != null && numero_tarjeta != null && vFechaExpiracion != null && dueño != null && cvv != null) {
                            nombreTarjeta.setText(nombre_tarjeta);
                            numeroTarjeta.setText(numero_tarjeta);
                            dueñoTarjeta.setText(dueño);
                            cvvTarjeta.setText(cvv);
                            fechaExpiracion.setText(formattedExpirationDate);
                            //imagen.setImageResource(R.drawable.picgiftcard);
                        }
                    }
                }
            }
        });



        //METODOS RAUL ========================================================================================

        deleteBtn = (Button) findViewById(R.id.delete_button);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String email = currentUser.getEmail();
                DocumentReference cardRef = db.collection("user").document(email).collection("payment").document(itemId);

                cardRef.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Tarjeta borrada con exito
                                Context context = getApplicationContext();
                                Toast.makeText(context, "Tarjeta eliminada", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SeleccionarCredit.this, HomeMenu.class);
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
                Intent intent = new Intent(SeleccionarCredit.this, AddCredit.class);
                intent.putExtra("cardName", nombre_tarjeta);
                intent.putExtra("cardHolderName", dueño);
                intent.putExtra("expirationDate", vFechaExpiracion);
                intent.putExtra("itemId",itemId);
                intent.putExtra("cardNumber",numero_tarjeta);
                intent.putExtra("CVV",cvv);
                intent.putExtra("operation","edit");
                startActivity(intent);
            }
        });

        notesBtn = findViewById(R.id.notes_button);

        notesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeleccionarCredit.this, CardNotes.class);
                intent.putExtra("itemId",itemId);
                intent.putExtra("cardType","payment");
                startActivity(intent);
            }
        });

    }}