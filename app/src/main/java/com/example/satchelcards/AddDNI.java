package com.example.satchelcards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddDNI extends AppCompatActivity {
    ImageView gobackBtn;
    Button btn_guardar;
    // Obtén una referencia al EditText de fecha
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dni);
        gobackBtn = (ImageView)findViewById(R.id.go_back);
        gobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddDNI.this, AddCards.class);
                startActivity(intent);
            }
        });

        btn_guardar = (Button) findViewById(R.id.btn_guardar);
        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //#region DATOS
                EditText nombreEditText = findViewById(R.id.dni_nombre);
                EditText apellidosEditText = findViewById(R.id.dni_apellidos);
                EditText sexoEditText = findViewById(R.id.dni_sexo);
                EditText nacionalidadEditText = findViewById(R.id.dni_nacionalidad);
                EditText numSoportEditText = findViewById(R.id.dni_num_soport);
                EditText canEditText = findViewById(R.id.dni_can);
                EditText dniEditText = findViewById(R.id.dni_dni);
                EditText equipoEditText = findViewById(R.id.dni_equipo);
                EditText provinciaEditText = findViewById(R.id.dni_provincia);
                EditText domicilioEditText = findViewById(R.id.dni_domicilio);
                EditText lugarNacimientoEditText = findViewById(R.id.dni_lugar_nacimiento);
                EditText nombrePadreEditText = findViewById(R.id.dni_nombre_padre);
                EditText nombreMadreEditText = findViewById(R.id.dni_nombre_madre);
                DatePicker fechaNacimientoDatePicker = findViewById(R.id.dni_FechaNacimiento);
                DatePicker fechaValidezDatePicker = findViewById(R.id.dni_FechaValidez);

                // Obtener los valores ingresados en los campos
                String nombre = nombreEditText.getText().toString();
                String apellidos = apellidosEditText.getText().toString();
                String sexo = sexoEditText.getText().toString();
                String nacionalidad = nacionalidadEditText.getText().toString();
                String numSoport = numSoportEditText.getText().toString();
                String can = canEditText.getText().toString();
                String dni = dniEditText.getText().toString();
                String equipo = equipoEditText.getText().toString();
                String provincia = provinciaEditText.getText().toString();
                String domicilio = domicilioEditText.getText().toString();
                String lugarNacimiento = lugarNacimientoEditText.getText().toString();
                String nombrePadre = nombrePadreEditText.getText().toString();
                String nombreMadre = nombreMadreEditText.getText().toString();

                // Obtener la fecha de nacimiento seleccionada
                int yearNacimiento = fechaNacimientoDatePicker.getYear();
                int monthNacimiento = fechaNacimientoDatePicker.getMonth();
                int dayNacimiento = fechaNacimientoDatePicker.getDayOfMonth();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, yearNacimiento);
                calendar.set(Calendar.MONTH, monthNacimiento - 1); //
                calendar.set(Calendar.DAY_OF_MONTH, dayNacimiento);
                Date DNacimiento = calendar.getTime();

                // Obtener la fecha de validez seleccionada
                int yearValidez = fechaValidezDatePicker.getYear();
                int monthValidez = fechaValidezDatePicker.getMonth();
                int dayValidez = fechaValidezDatePicker.getDayOfMonth();
                Calendar calendar2 = Calendar.getInstance();
                calendar2.set(Calendar.YEAR, yearValidez);
                calendar2.set(Calendar.MONTH, monthValidez - 1);
                calendar2.set(Calendar.DAY_OF_MONTH, dayValidez);
                Date DValidez = calendar2.getTime();

                //#endregion
                meterdatosenBBDD(nombre, apellidos, sexo, nacionalidad, numSoport, can, dni, equipo, provincia, domicilio, lugarNacimiento, nombrePadre, nombreMadre, DNacimiento, DValidez);

            }

            private void meterdatosenBBDD(String nombre, String apellidos, String sexo, String nacionalidad, String numSoport, String can, String dni, String equipo, String provincia, String domicilio, String lugarNacimiento, String nombrePadre, String nombreMadre, Date DNacimiento, Date DValidez) {
                //OBTIENE LA INSTANCIA
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                //GUARDA LA COLECCIÓN
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String email = currentUser.getEmail();
                DocumentReference docRef = db.collection("user").document(email).collection("dni").document(dni);
                //CREA EL DOCUMENTO Y AÑADE LOS CAMPOS
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("nombre", nombre);
                userMap.put("apellidos", apellidos);
                userMap.put("sexo", sexo);
                userMap.put("nacionalidad", nacionalidad);
                userMap.put("numSoport", numSoport);
                userMap.put("can", can);
                userMap.put("dni", dni);
                userMap.put("equipo", equipo);
                userMap.put("provincia", provincia);
                userMap.put("domicilio", domicilio);
                userMap.put("lugarNacimiento", lugarNacimiento);
                userMap.put("nombrePadre", nombrePadre);
                userMap.put("nombreMadre", nombreMadre);
                userMap.put("DNacimiento", DNacimiento);
                userMap.put("DValidez", DValidez);

                //AÑADE EL DOCUMENTO A LA COLECCIÓN
                docRef.set(userMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) { //SI TODO VA BIEN
                                Intent intent = new Intent(AddDNI.this, HomeMenu.class);
                                startActivity(intent);
                                Context context = getApplicationContext();
                                Toast.makeText(context, "DNI insertado !", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) { //SI HAY ALGÚN ERROR
                                Context context = getApplicationContext();
                                Toast.makeText(context, "Error al registrar los datos del usuario!" + e, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            //#endregion
        });
    }
}
