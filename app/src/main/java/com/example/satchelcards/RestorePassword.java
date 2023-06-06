package com.example.satchelcards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestorePassword extends ClassBlockOrientation {

    //#region VARIABLES

    //#endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restore_password);

        //#region OBTENER ELEMENTOS
        EditText editEmail = findViewById(R.id.email);
        Button btnRestablecer = findViewById(R.id.restablecerContraseña);
        ImageView btnBack = (ImageView) findViewById(R.id.goBack);
        //#endregion

        //#region BACK
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestorePassword.this, Login.class);
                startActivity(intent);
            }
        });
        //#endregion

        //#region RESTABLECER
        btnRestablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();
                if (validarEmail(email)) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    Context context = getApplicationContext();
                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Se ha enviado un correo electrónico para restablecer la contraseña", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "No se pudo enviar el correo electrónico de restablecimiento de contraseña", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(RestorePassword.this, "Email inválido", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //#endregion

    }

    @Override
    public void onBackPressed() {
        // Cierra la aplicación
        finishAffinity();
    }

    //#region FUNCIÓN PARA VALIDAR FORMATO DE EMAIL
    public boolean validarEmail(String email) {
        String regex = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    //#endregion
}