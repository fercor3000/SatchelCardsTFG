package com.example.satchelcards;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddCustom extends ClassBlockOrientation {

    ImageView gobackBtn;
    Button btn_guardar;
    Button btnCambiarImg;
    private static final int REQUEST_CODE_SELECT_IMAGE = 100;
    ImageView imageViewPhoto;
    Uri selectedImageUri;
    String finalUserCardID, itemId;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_custom);
        boolean comesFromList = getIntent().getBooleanExtra("lista",false);

        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        btnCambiarImg = (Button) findViewById(R.id.custom_btn_add_image);
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
            fillInterface();
        }

        gobackBtn = (ImageView) findViewById(R.id.go_back);
        gobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("edit".equals(operation)) {
                    Intent intent = new Intent(AddCustom.this, SeleccionarCustom.class);
                    intent.putExtra("itemId", itemId);
                    startActivity(intent);
                } else if(comesFromList){
                    Intent intent = new Intent(AddCustom.this, ListCustom.class);
                    startActivity(intent);
                } else{
                    Intent intent = new Intent(AddCustom.this, HomeMenu.class);
                    startActivity(intent);
                }
            }
        });

        btn_guardar = (Button) findViewById(R.id.btn_save);

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //#region DATOS
                EditText cardNameEditText = findViewById(R.id.cardName);
                EditText cardHolderNameEditText = findViewById(R.id.cardHolderName);
                EditText cardNumberEditText = findViewById(R.id.cardNumber);
                DatePicker expireDateDatePicker = findViewById(R.id.expirationDate);

                // Obtener los valores ingresados en los campos
                String cardName = cardNameEditText.getText().toString();
                String cardHolderName = cardHolderNameEditText.getText().toString();
                long cardNumber;
                if(!cardNumberEditText.getText().toString().isEmpty()) {
                    cardNumber = Long.parseLong(cardNumberEditText.getText().toString());
                }else{
                    cardNumber = 0;
                }
                // Obtener la fecha de vencimiento seleccionada
                int expireYear = expireDateDatePicker.getYear();
                int expireMonth = expireDateDatePicker.getMonth();
                int expireDay = expireDateDatePicker.getDayOfMonth();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, expireYear);
                calendar.set(Calendar.MONTH, expireMonth - 1); //
                calendar.set(Calendar.DAY_OF_MONTH, expireDay);
                Date DExpire = calendar.getTime();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth auth = FirebaseAuth.getInstance();

                String email = auth.getCurrentUser().getEmail();
                //Recoger los datos del usuario actual
                DocumentReference userRef = db.collection("user").document(email);
                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //Recogo el userId y el cardNextId del usuario (getLong para numero, getString para cadena)
                                String userId = String.valueOf(document.getLong("userId"));
                                if ("edit".equals(operation)) {
                                    editCard(cardName, cardHolderName, DExpire, cardNumber);
                                } else {
                                    String cardNextId = String.valueOf(document.getLong("cardNextId"));
                                    //Si los recojo con exito, los utilizo para crear la nueva tarjeta
                                    if (userId != null && cardNextId != null) {
                                        String userCardID = "user" + userId + "card" + cardNextId;
                                        finalUserCardID = userCardID;
                                    //llamo al método de insertar en la base de datos enviandole los datos que quiero insertar
                                    insertInDDBB(cardName, cardHolderName,cardNumber, DExpire, userCardID, cardNextId);
                                } else {
                                    // Handle missing or invalid data
                                    // Show an error message or take appropriate action

                                    }
                                }
                            } else {
                                // Handle non-existent document
                                // Show an error message or take appropriate action
                            }
                        } else {
                            // Handle task exception
                            // Show an error message or take appropriate action
                        }
                    }
                });
                //#endregion
            }

            //Método para insertar datos en la base de datos según los parámetros recibidos
            private void insertInDDBB(String cardName, String cardHolderName,long cardNumber, Date DExpire, String userCardID, String cardNextId) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String email = currentUser.getEmail();
                //Me situo en una coleccion inexistente en user/email/custom/userCardID y se crea automaticamente cuando intento insertar datos ahí.
                DocumentReference docRef = db.collection("user").document(email).collection("custom").document(userCardID);

                //Mapeo los datos con el nombre del campo de la base de datos: userMap.put("nombre campo en bbdd", valor a introducir);
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("cardName", cardName);
                userMap.put("cardHolderName", cardHolderName);
                userMap.put("cardNumber", cardNumber);
                userMap.put("expirationDate", DExpire);
                userMap.put("cardId", cardNextId);
                userMap.put("cardType","custom");

                docRef.set(userMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // PARA INCREMENTAR cardNextId EN 1
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                String email = mAuth.getCurrentUser().getEmail();
                                //RECIBE EL VALOR DE LA BASE DE DATOS PARA AUMENTARLO EN 1
                                DocumentReference userRef = db.collection("user").document(email);

                                db.runTransaction(new Transaction.Function<Void>() {
                                    @Nullable
                                    @Override
                                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                        DocumentSnapshot userSnapshot = transaction.get(userRef);
                                        Long cardNextId = userSnapshot.getLong("cardNextId");

                                        // INCREMENTA cardNextId EN 1
                                        transaction.update(userRef, "cardNextId", cardNextId + 1);

                                        return null;
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Transaction success
                                        if (selectedImageUri != null) {
                                            FirebaseStorage storage = FirebaseStorage.getInstance();
                                            StorageReference storageRef = storage.getReference();
                                            StorageReference imagenRef = storageRef.child("cardImages/" + currentUser.getUid() + "_custom_" + finalUserCardID);

                                            UploadTask uploadTask = imagenRef.putFile(selectedImageUri);

                                            uploadTask.addOnSuccessListener(taskSnapshot -> {

                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Context context = getApplicationContext();
                                                    Toast.makeText(context, "Error al cargar la imagen de perfil!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }


                                        Context context = getApplicationContext();
                                        Toast.makeText(context, "Tarjeta insertada!", Toast.LENGTH_SHORT).show();
                                        Intent intent;
                                        intent = new Intent(AddCustom.this, HomeMenu.class);
                                        startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Transaction failure
                                        Context context = getApplicationContext();
                                        Toast.makeText(context, "Error al registrar los datos de la tarjeta!" + e, Toast.LENGTH_SHORT).show();
                                        Log.e("AddGift", "Error updating cardNextId in Firestore", e);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Firestore set operation failure
                                Context context = getApplicationContext();
                                Toast.makeText(context, "Error al registrar los datos de la tarjeta!" + e, Toast.LENGTH_SHORT).show();
                                Log.e("AddGift", "Error inserting card data to Firestore", e);
                            }
                        });

            }

            //#endregion
        });

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

    private void fillInterface() {
        // RECOGE LOS DATOS DEL INTENT
        Intent intent = getIntent();
        String cardName = intent.getStringExtra("cardName");
        String cardHolderName = intent.getStringExtra("cardHolderName");
        String cardNumber = intent.getStringExtra("cardNumber");
        Date expirationDate = (Date) intent.getSerializableExtra("expirationDate");

        // RELLENA LA INTERFAZ CON DATOS
        EditText cardNameEditText = findViewById(R.id.cardName);
        EditText cardHolderNameEditText = findViewById(R.id.cardHolderName);
        DatePicker expireDateDatePicker = findViewById(R.id.expirationDate);
        EditText cardNumberEditText = findViewById(R.id.cardNumber);

        cardNameEditText.setText(cardName);
        cardHolderNameEditText.setText(cardHolderName);
        if(cardNumber != null) {
            cardNumberEditText.setText(cardNumber + "");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(expirationDate);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        expireDateDatePicker.updateDate(year, month, day);
    }


        private void editCard(String cardName, String cardHolderName, Date DExpire, long cardNumber) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String email = currentUser.getEmail();

            // Actualizar la tarjeta con nuevos valores
            DocumentReference docRef = db.collection("user").document(email).collection("custom").document(itemId);

            Map<String, Object> cardMap = new HashMap<>();
            cardMap.put("cardName", cardName);
            cardMap.put("cardHolderName", cardHolderName);
            cardMap.put("expirationDate", DExpire);
            cardMap.put("cardNumber",cardNumber);

            docRef.update(cardMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Context context = getApplicationContext();
                            Toast.makeText(context, "Tarjeta actualizada!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddCustom.this, SeleccionarCustom.class);
                            intent.putExtra("itemId",itemId);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Context context = getApplicationContext();
                            Toast.makeText(context, "Error al actualizar la tarjeta!" + e, Toast.LENGTH_SHORT).show();
                            Log.e("AddGift", "Error updating card data in Firestore", e);
                        }
                    });
        }
}