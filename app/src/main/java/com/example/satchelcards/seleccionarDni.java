package com.example.satchelcards;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;



public class seleccionarDni extends AppCompatActivity {

    ImageView gobackBtn;

    protected void onCreate(Bundle savedInstanceState) {

        // ESTO LO HIZO JD PERO ESTA SIN TERMINAR
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seleccionar_credit_card);

        gobackBtn = (ImageView)findViewById(R.id.go_back);

        gobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(seleccionarDni.this, ListDNI.class);
                startActivity(intent);
            }
        });

    }

}
