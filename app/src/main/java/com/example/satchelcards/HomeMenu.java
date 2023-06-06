package com.example.satchelcards;

import android.content.Context;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeMenu extends ClassBlockOrientation {

    //#region VARIABLES
    ImageView profileBtn, addCardBtn, dnisList, creditsList, transportsList, giftsList, accessList, customList;
    ImageView displayMenuButton;
    private DrawerLayout drawerLayout;
    private TextView menuItem1;
    private TextView menuItem2;
    private TextView menuItem3;
    private ImageView helpBtn;
    boolean guardarHuella;
    //#endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_menu);

        FirebaseAuth mAuthVerify = FirebaseAuth.getInstance();
        FirebaseUser currentUserVerify = mAuthVerify.getCurrentUser();
        if (currentUserVerify != null && !currentUserVerify.isEmailVerified()) {
            Intent intent = new Intent(HomeMenu.this, WaitingToVerifyEmail.class);
            startActivity(intent);
        }

        try {
            boolean comesFromRegister = getIntent().getBooleanExtra("registro", false);
            if (comesFromRegister) {
                //#region HUELLA DACTILAR
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = mDatabase.getReference("user");
                Context context = getApplicationContext();
                BiometricManager biometricManager = BiometricManager.from(context);
                switch (biometricManager.canAuthenticate()) {
                    case BiometricManager.BIOMETRIC_SUCCESS:
                        //Toast.makeText(context, "Dispositivo habilitado para utilizar datos biométricos.", Toast.LENGTH_SHORT).show();
                        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                                .setTitle("Registrar inicio de sesión biometrico")
                                .setSubtitle("Ponga su huella o cara para activar el inicio de sesión biometrico")
                                .setNegativeButtonText("Cancelar")
                                .build();

                        BiometricPrompt.AuthenticationCallback authenticationCallback = new BiometricPrompt.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                                super.onAuthenticationSucceeded(result);
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference docRef = db.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("biometricAuth", true);
                                docRef.update(userMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Registrado inicio de sesión biometrico!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) { //SI HAY ALGÚN ERROR
                                                Context context = getApplicationContext();
                                                Toast.makeText(context, "Error al registrar datos de huella dactilar!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            @Override
                            public void onAuthenticationFailed() {
                                super.onAuthenticationFailed();
                                Toast.makeText(context, "ERROR! No se ha podido autentificar con biometría.", Toast.LENGTH_SHORT).show();
                            }
                        };

                        Executor executor = Executors.newSingleThreadExecutor();
                        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, authenticationCallback);
                        biometricPrompt.authenticate(promptInfo);
                        break;

                    case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                        Toast.makeText(context, "Este dispositivo no contiene lector de datos biométricos.", Toast.LENGTH_SHORT).show();
                        break;

                    case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                        Toast.makeText(context, "El lector de datos biométricos no se encuentra disponible.", Toast.LENGTH_SHORT).show();
                        break;

                    case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                        Toast.makeText(context, "El dispositivo no cuenta con datos biométricos cargados, por favor corrobore sus opciones de seguridad.", Toast.LENGTH_SHORT).show();
                        break;
                }
                //#endregion
            }
        } catch (Exception ex) {

        }

        helpBtn = (ImageView) findViewById(R.id.helpBtn);
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://drive.google.com/file/d/1QAMsVhlz7bGe04_j_qrhlYvHBaJQBigt/view?usp=sharing";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        dnisList = (ImageView) findViewById(R.id.DniCards);
        creditsList = (ImageView) findViewById(R.id.CreditCards);
        transportsList = (ImageView) findViewById(R.id.TransportCards);
        giftsList = (ImageView) findViewById(R.id.GiftCards);
        //accessList = (ImageView) findViewById(R.id.AccessCards);
        customList = (ImageView) findViewById(R.id.CustomCards);

        profileBtn = (ImageView) findViewById(R.id.profile);
        addCardBtn = (ImageButton) findViewById(R.id.addCard);
        displayMenuButton = (ImageView)findViewById(R.id.displayMenu);
        drawerLayout = findViewById(R.id.drawer_layout);

        menuItem1 = findViewById(R.id.menu_item1);
        menuItem2 = findViewById(R.id.menu_item2);
        menuItem3 = findViewById(R.id.menu_item3);

        //#region AL PULSAR COMPAR PREMIUM
        menuItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeMenu.this, BuyPremium.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        //#endregion

        //#region AL PULSAR LISTA DE DNIS
        dnisList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //REDIRIGE A LA PÁGINA DE LISTADO
                Intent intent = new Intent(HomeMenu.this, ListDNI.class);
                startActivity(intent);
            }
        });
        //#endregion

        //#region AL PULSAR LISTA DE CREDITS
        creditsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //REDIRIGE A LA PÁGINA DE LISTADO
                Intent intent = new Intent(HomeMenu.this, ListCreditCard.class);
                startActivity(intent);
            }
        });
        //#endregion

        //#region AL PULSAR LISTA DE TRANSPORTS
        transportsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //REDIRIGE A LA PÁGINA DE LISTADO
                Intent intent = new Intent(HomeMenu.this, ListTransport.class);
                startActivity(intent);
            }
        });
        //#endregion

        //#region AL PULSAR LISTA DE GIFTS
        giftsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //REDIRIGE A LA PÁGINA DE LISTADO
                Intent intent = new Intent(HomeMenu.this, ListGift.class);
                startActivity(intent);
            }
        });
        //#endregion

        /*//#region AL PULSAR LISTA DE ACCESS
        accessList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //REDIRIGE A LA PÁGINA DE LISTADO
                Intent intent = new Intent(HomeMenu.this, ListAccess.class);
                startActivity(intent);
            }
        });
        //#endregion*/

        //#region AL PULASR CUSTOM...
        customList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //REDIRIGE A LA PÁGINA DE LISTADO
                Intent intent = new Intent(HomeMenu.this, ListCustom.class);
                startActivity(intent);
            }
        });
        //#endregion

        //#region AL PULSAR PERFIL...
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //REDIRIGE A LA PÁGINA DEL PERFIL
                Intent intent = new Intent(HomeMenu.this, Profile.class);
                startActivity(intent);
            }
        });
        //#endregion

        //#region AL PULSAR AÑADIR TARJETA
        addCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(HomeMenu.this, addCardBtn);
                popup.getMenuInflater().inflate(R.menu.menu_aniadir_tarjetas, popup.getMenu());



                // Agrega el listener para el menú
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Obtener los valores enteros de los recursos
                        final int aniadirTarjetaPersonalizadaId = getResources().getIdentifier("aniadirtarjetapersonalizada", "id", getPackageName());
                        final int aniadirTarjetaExistenteId = getResources().getIdentifier("aniadirtarjetaexistente", "id", getPackageName());
                        int itemId = item.getItemId();
                        if (itemId == aniadirTarjetaPersonalizadaId) {
                            // Iniciar la actividad correspondiente a "Añadir tarjeta personalizada"
                            Intent intentPersonalizada = new Intent(HomeMenu.this, AddCustom.class);
                            startActivity(intentPersonalizada);
                            return true;
                        } else if (itemId == aniadirTarjetaExistenteId) {
                            // Iniciar la actividad correspondiente a "Añadir tarjeta existente"
                            Intent intentExistente = new Intent(HomeMenu.this, AddCards.class);
                            startActivity(intentExistente);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                popup.show();
            }
        });
        //#endregion

        // Configura el botón para abrir el menú lateral
        displayMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        //#endregion

        //#region AL PULSAR EL MENÚ & 1,2
        menuItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción de añadir tarjeta
                Intent intent = new Intent(HomeMenu.this, AddCards.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        menuItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeMenu.this, BuyPremium.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        menuItem3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeMenu.this, AboutUs.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    private boolean showConfirmationFingerPrintDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmación");
        builder.setMessage("¿Quieres iniciar sesión con tu huella dactilar?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                guardarHuella = true;
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                guardarHuella = false;
            }
        });
        builder.show();
        return guardarHuella;
    }

    @Override
    public void onBackPressed() {
        // No realiza ninguna acción para evitar volver atrás a la pantalla de inicio de sesión
    }
}
