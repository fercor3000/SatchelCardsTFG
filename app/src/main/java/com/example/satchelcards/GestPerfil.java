package com.example.satchelcards;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class GestPerfil extends AppCompatActivity {

    ImageView gobackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestionar_perfil);

        gobackBtn = (ImageView)findViewById(R.id.goBack);

        gobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //REDIRIGE
                atras(0);
            }
        });
    }
    //#region FUNCIÓN PARA REDIRECCIONAR
    private void atras(int c) {
        Intent intent = new Intent(GestPerfil.this, Profile.class);
        if (c == 1) {
            intent = new Intent(GestPerfil.this, Login.class);
        }
        startActivity(intent);
    }
}