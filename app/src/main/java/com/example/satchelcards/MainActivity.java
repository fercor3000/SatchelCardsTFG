package com.example.satchelcards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    //#region VARIABLES
    Button btnLogIn, btnRegister;
    EditText email, password;
    private FirebaseAuth mAuth;
    //#endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //#region OBTENER ELEMENTOS
        btnRegister = (Button)findViewById(R.id.register);
        btnLogIn = (Button)findViewById(R.id.login);
        email = (EditText)findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        //#endregion


        //#region AL PULSAR INICIAR SESIÓN...
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //OBTIENE LOS DATOS PARA INCIAR SESIÓN
                String emailSTR = email.getText().toString();
                String passwordSTR = password.getText().toString();

                if((!emailSTR.equals("")) && (!passwordSTR.equals(""))) { //SI NO ESTÁN VACÍOS...
                    if (validarEmail(emailSTR)) { //SI EL FORMATO ES VÁLIDO...
                        //OBTIENE INSTANCIA DE FIREBASEAUTH
                        mAuth = FirebaseAuth.getInstance();
                        //INICIA SESIÓN
                        mAuth.signInWithEmailAndPassword(emailSTR,passwordSTR).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Context context = getApplicationContext();
                                if (task.isSuccessful()) { //SI TODO HA IDO BIEN
                                    //REDIRIGE A LA PÁGINA PRINCIPAL
                                    Intent intent = new Intent(MainActivity.this, LogIn.class);
                                    startActivity(intent);
                                } else { //SI NO SE HA COMPLETADO...
                                    Toast.makeText(context, "Error!! Email o contraseña inválidos!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else { //SI EL FORMATO ES INVÁLIDO
                        Context context = getApplicationContext();
                        Toast.makeText(context, "Error!! Email inválido", Toast.LENGTH_SHORT).show();
                    }
                } else { //SI HAY ALGÚN DATO VACÍO...
                    Context context = getApplicationContext();
                    if (emailSTR.equals("") && passwordSTR.equals("")){
                        Toast.makeText(context, "Email y contraseña no pueden estar vacíos!", Toast.LENGTH_SHORT).show();
                    } else {
                        if((emailSTR.equals(""))) {
                            Toast.makeText(context, "Email no puede estar vacío!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Contraseña no puede estar vacía!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        //#endregion

        //#region AL PULSAR REGISTRARSE...
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
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