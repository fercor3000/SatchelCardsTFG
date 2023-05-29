package com.example.satchelcards;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddCredit extends AppCompatActivity {

    ImageView gobackBtn;
    Button btn_guardar;

    private NfcAdapter nfcAdapter;
    private ImageView nfcLogoImageView;
    private PendingIntent nfcPendingIntent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_credit_card);

        gobackBtn = (ImageView) findViewById(R.id.go_back);
        gobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCredit.this, AddCards.class);
                startActivity(intent);
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
                EditText CVVEditText = findViewById(R.id.CVV);
                DatePicker expireDateDatePicker = findViewById(R.id.expirationDate);

                // Obtener los valores ingresados en los campos
                String cardName = cardNameEditText.getText().toString();
                String cardHolderName = cardHolderNameEditText.getText().toString();
                long cardNumber = Long.parseLong(cardNumberEditText.getText().toString());
                int CVV = Integer.parseInt(CVVEditText.getText().toString());


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
                                String cardNextId = String.valueOf(document.getLong("cardNextId"));
                                //Si los recojo con exito, los utilizo para crear la nueva tarjeta
                                if (userId != null && cardNextId != null) {
                                    String userCardID = "user" + userId + "card" + cardNextId;
                                    //llamo al método de insertar en la base de datos enviandole los datos que quiero insertar
                                    insertInDDBB(cardName, cardHolderName,cardNumber, CVV, DExpire, userCardID, cardNextId);
                                } else {
                                    // Handle missing or invalid data
                                    // Show an error message or take appropriate action
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
            private void insertInDDBB(String cardName, String cardHolderName,long cardNumber, int CVV, Date DExpire, String userCardID, String cardNextId) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String email = currentUser.getEmail();
                //Me situo en una coleccion inexistente en user/email/payment/userCardID y se crea automaticamente cuando intento insertar datos ahí.
                DocumentReference docRef = db.collection("user").document(email).collection("payment").document(userCardID);

                //Mapeo los datos con el nombre del campo de la base de datos: userMap.put("nombre campo en bbdd", valor a introducir);
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("cardName", cardName);
                userMap.put("cardHolderName", cardHolderName);
                userMap.put("cardNumber", cardNumber);
                userMap.put("CVV", CVV);
                userMap.put("expirationDate", DExpire);
                userMap.put("cardId", cardNextId);
                userMap.put("cardType","payment");

                docRef.set(userMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Increase cardNextId field by 1
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                String email = mAuth.getCurrentUser().getEmail();
                                //Recibo el valor de la base de datos para aumentarle el valor en 1
                                DocumentReference userRef = db.collection("user").document(email);

                                db.runTransaction(new Transaction.Function<Void>() {
                                    @Nullable
                                    @Override
                                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                        DocumentSnapshot userSnapshot = transaction.get(userRef);
                                        Long cardNextId = userSnapshot.getLong("cardNextId");

                                        // Increment the cardNextId field by 1
                                        transaction.update(userRef, "cardNextId", cardNextId + 1);

                                        return null;
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Transaction success
                                        Context context = getApplicationContext();
                                        Toast.makeText(context, "Tarjeta insertada!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(AddCredit.this, LogIn.class);
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

        nfcLogoImageView = findViewById(R.id.nfc_logo);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Crear PendingIntent para la detección de NFC
        Intent nfcIntent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, PendingIntent.FLAG_MUTABLE);
    }

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

            // Iniciar la actividad WriteNfcActivity y pasar el valor del NFC como extra
            /*Intent writeNfcIntent = new Intent(this, WriteNfcActivity.class);
            writeNfcIntent.putExtra("nfcData", tagIdString);
            startActivity(writeNfcIntent);*/
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
