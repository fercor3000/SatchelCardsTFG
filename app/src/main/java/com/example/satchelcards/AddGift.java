package com.example.satchelcards;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AddGift extends AppCompatActivity {

    ImageView gobackBtn;
    Button btn_guardar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_gift);

        gobackBtn = (ImageView) findViewById(R.id.go_back);
        gobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddGift.this, AddCards.class);
                startActivity(intent);
            }
        });
        btn_guardar = (Button) findViewById(R.id.btn_save);

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Guardar en la BBDD la tarjeta de regalo

            }
        });
    }
}
