package com.example.satchelcards;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.HalfFloat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddDNI extends ClassBlockOrientation {
    ImageView gobackBtn;
    Button btn_guardar;
    Button btnCambiarImg;
    Uri selectedImageUri;
    ImageView imageViewPhoto;
    String itemId;
    private static final int REQUEST_CODE_SELECT_IMAGE = 100;
    private NfcAdapter nfcAdapter;
    private ImageView nfcLogoImageView;
    private PendingIntent nfcPendingIntent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dni);
        boolean comesFromList = getIntent().getBooleanExtra("lista",false);

        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        btnCambiarImg = (Button) findViewById(R.id.dni_btn_add_image);
        btnCambiarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
            }
        });


        //Recibimos la operacion que queremos realizar
        Intent intent = getIntent();
        String operation = intent.getStringExtra("operation");
        itemId = intent.getStringExtra("itemId");

        if("edit".equals(operation)){
            //fillInterface();
        }

        gobackBtn = (ImageView) findViewById(R.id.go_back);
        gobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("edit".equals(operation)){
                    Intent intent = new Intent(AddDNI.this, SeleccionarDni.class);
                    intent.putExtra("itemId",itemId);
                    startActivity(intent);
                } else if(comesFromList){
                        Intent intent = new Intent(AddDNI.this, ListDNI.class);
                        startActivity(intent);
                } else {
                    Intent intent = new Intent(AddDNI.this, AddCards.class);
                    startActivity(intent);
                }
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
                calendar.set(Calendar.MONTH, monthNacimiento); //
                calendar.set(Calendar.DAY_OF_MONTH, dayNacimiento);
                Date DNacimiento = calendar.getTime();

                // Obtener la fecha de validez seleccionada
                int yearValidez = fechaValidezDatePicker.getYear();
                int monthValidez = fechaValidezDatePicker.getMonth();
                int dayValidez = fechaValidezDatePicker.getDayOfMonth();
                Calendar calendar2 = Calendar.getInstance();
                calendar2.set(Calendar.YEAR, yearValidez);
                calendar2.set(Calendar.MONTH, monthValidez);
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
                //SI HAY ALGO NULL
                if (nombre.equals("") || apellidos.equals("") || sexo.equals("") || nacionalidad.equals("") || numSoport.equals("")
                        || can.equals("") ||dni.equals("") || equipo.equals("") || provincia.equals("") || domicilio.equals("")
                        || lugarNacimiento.equals("") || nombrePadre.equals("") || nombreMadre.equals("") || DNacimiento.equals("")
                        || DValidez.equals("")) {
                    Context context = getApplicationContext();
                    Toast.makeText(context, "ERROR! Faltan datos!", Toast.LENGTH_SHORT).show();
                } else {
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

                    Intent intent = new Intent(AddDNI.this, LoadingData.class);
                    startActivity(intent);

                    //AÑADE EL DOCUMENTO A LA COLECCIÓN
                    docRef.set(userMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) { //SI TODO VA BIEN
                                    if (selectedImageUri != null) {
                                        FirebaseStorage storage = FirebaseStorage.getInstance();
                                        StorageReference storageRef = storage.getReference();
                                        StorageReference imagenRef = storageRef.child("cardImages/" + currentUser.getUid() + "_dni_" + dni);

                                        UploadTask uploadTask = imagenRef.putFile(selectedImageUri);

                                        uploadTask.addOnSuccessListener(taskSnapshot -> {

                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Context context = getApplicationContext();
                                                Intent intent = new Intent(AddDNI.this, HalfFloat.class);
                                                startActivity(intent);
                                                Toast.makeText(context, "Error al cargar la imagen de perfil!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    Intent intent;
                                    intent = new Intent(AddDNI.this, HomeMenu.class);
                                    startActivity(intent);
                                    Context context = getApplicationContext();
                                    Toast.makeText(context, "DNI insertado !", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) { //SI HAY ALGÚN ERROR
                                    Context context = getApplicationContext();
                                    Intent intent = new Intent(AddDNI.this, HomeMenu.class);
                                    startActivity(intent);
                                    Toast.makeText(context, "Error al registrar los datos del usuario!" + e, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            //#endregion
        });

        nfcLogoImageView = findViewById(R.id.nfc_logo);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Crear PendingIntent para la detección de NFC
        Intent nfcIntent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, PendingIntent.FLAG_MUTABLE);
    }

    //#region IMAGEN ----
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                selectedImageUri = data.getData();
                imageViewPhoto.setImageURI(selectedImageUri);
            }
        }
    }
    //#endregion

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    String tagIdString = "";
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            byte[] tagId = tag.getId(); // Obtiene el código NFC de la tarjeta como un arreglo de bytes

            //En esta variable esta guardada la cadena del NFC
            tagIdString = convertBytesToHexString(tagId); // Convierte el arreglo de bytes a una cadena hexadecimal

            // Cambia la imagen a nfc_check.png
            nfcLogoImageView.setImageResource(R.drawable.nfc_check);

            // Muestra un mensaje de éxito
            Toast.makeText(this, "NFC detectado correctamente", Toast.LENGTH_SHORT).show();
        }
    }

    private String convertBytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
