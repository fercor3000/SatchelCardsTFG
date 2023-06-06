package com.example.satchelcards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginBiometric extends ClassBlockOrientation {

    //#region VARIABLES
    Button btnAccederConUsuario, btnAccederConBiometria;
    //#endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_biometric);

        accederConBiometria();

        //#region OBTENER ELEMENTOS
        btnAccederConUsuario = (Button)findViewById(R.id.clave);
        btnAccederConBiometria = (Button)findViewById(R.id.accederConBiometria);
        //#endregion

        btnAccederConUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginBiometric.this, Login.class);
                intent.putExtra("loginWithEmailAndPassword", true);
                startActivity(intent);
            }
        });

        btnAccederConBiometria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accederConBiometria();
            }
        });
    }

    //#region HUELLA DACTILAR
    public void accederConBiometria() {
        Context context = getApplicationContext();
        BiometricManager biometricManager = BiometricManager.from(context);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Inicio de sesión biometrico")
                        .setSubtitle("Ponga su huella o cara para iniciar sesión")
                        .setNegativeButtonText("Cancelar")
                        .build();

                BiometricPrompt.AuthenticationCallback authenticationCallback = new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Intent intent = new Intent(LoginBiometric.this, HomeMenu.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(context, "ERROR! No se ha podido autentificar al usuario con biometría.", Toast.LENGTH_SHORT).show();
                    }
                };

                Executor executor = Executors.newSingleThreadExecutor();
                BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, authenticationCallback);
                biometricPrompt.authenticate(promptInfo);
                break;
        }
    }
    //#endregion

    @Override
    public void onBackPressed() {
        // Cierra la aplicación
        finishAffinity();
    }
}