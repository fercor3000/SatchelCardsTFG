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

public class SeleccionarDni extends AppCompatActivity {

    ImageView goBackBtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ImageView imagen;
    Button editBtn, deleteBtn, notesBtn;
    Date vFechaExpiracion;
    String nombre_dni, num_dni, dni_elec, sexo, fecha_expiracionFormatted;
    TextView nombreDniTarjeta, numeroDniTarjeta, dniElectronicoTarjeta, sexoTarjeta, fechaExpiracionTarjeta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seleccionar_dni);

        imagen = (ImageView) findViewById(R.id.imagen_tarjeta);
        nombreDniTarjeta = findViewById(R.id.nombre_dni);
        numeroDniTarjeta = findViewById(R.id.num_dni);
        dniElectronicoTarjeta = findViewById(R.id.dni_elec);
        sexoTarjeta = findViewById(R.id.sexo);
        fechaExpiracionTarjeta = findViewById(R.id.fecha_expiracion);

        goBackBtn = (ImageView) findViewById(R.id.go_back);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeleccionarDni.this, ListDNI.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        String itemId = intent.getStringExtra("itemId");
        String imageUri = intent.getStringExtra("imageUri");

        if (imageUri.equals("nada")) {
            imagen.setImageResource(R.drawable.picdnicard);
        } else {
            Picasso.get().load(imageUri).into(imagen);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        DocumentReference userRef = db.collection("user").document(email).collection("dni").document(itemId);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String nombre = String.valueOf(document.getString("nombre"));
                        String apellidos = String.valueOf(document.getString("apellidos"));
                        nombre_dni = nombre + " " + apellidos;
                        num_dni = String.valueOf(document.getString("dni"));
                        dni_elec = String.valueOf(document.getString("numSoport"));
                        sexo = String.valueOf(document.getString("sexo"));


                        vFechaExpiracion = document.getDate("DValidez");
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        fecha_expiracionFormatted = formatter.format(vFechaExpiracion);
                        try {
                            vFechaExpiracion = formatter.parse(fecha_expiracionFormatted);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (nombre_dni != null && num_dni != null && num_dni != null && dni_elec != null && vFechaExpiracion != null) {
                            nombreDniTarjeta.setText(nombre_dni);
                            numeroDniTarjeta.setText(num_dni);
                            dniElectronicoTarjeta.setText(dni_elec);
                            sexoTarjeta.setText(sexo);
                            fechaExpiracionTarjeta.setText(fecha_expiracionFormatted);
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
                DocumentReference cardRef = db.collection("user").document(email).collection("dni").document(itemId);

                cardRef.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Tarjeta borrada con exito
                                Context context = getApplicationContext();
                                Toast.makeText(context, "Tarjeta eliminada", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SeleccionarDni.this, ListGift.class);
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

        /*editBtn = findViewById(R.id.edit_button);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeleccionarDni.this, AddDNI.class);
                intent.putExtra("name", nombre_dni);
                intent.putExtra("number", num_dni);
                intent.putExtra("dniElec", dni_elec);
                intent.putExtra("sex", sexo);
                intent.putExtra("expirationDate", fecha_expiracionFormatted);
                intent.putExtra("itemId",itemId);
                intent.putExtra("operation","edit");
                startActivity(intent);
            }
        });*/

        notesBtn = findViewById(R.id.notes_button);

        notesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeleccionarDni.this, CardNotes.class);
                intent.putExtra("itemId",itemId);
                startActivity(intent);
            }
        });

    }}