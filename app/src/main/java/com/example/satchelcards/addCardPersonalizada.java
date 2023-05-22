package com.example.satchelcards;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class addCardPersonalizada extends AppCompatActivity {

    ImageView gobackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rellenar_tarjeta_personalizada);

        gobackBtn = (ImageView)findViewById(R.id.go_back);

        gobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(addCardPersonalizada.this, LogIn.class);
                startActivity(intent);
            }
        });

    }
}