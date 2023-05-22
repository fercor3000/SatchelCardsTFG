package com.example.satchelcards;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class AddCards extends AppCompatActivity {

    //#region VARIABLES
    ImageView gobackBtn;
    LinearLayout addDniBtn;
    //#endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_card_select);

        //#region OBTENER ELEMENTOS
        gobackBtn = (ImageView)findViewById(R.id.goBack);
        addDniBtn = (LinearLayout) findViewById(R.id.addDni);
        //#endregion

        //#region AL PULSAR VOLVER...
        gobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCards.this, LogIn.class);
                startActivity(intent);
            }
        });
        //#endregion

        //#region AL PULSAR ADDDNI
        addDniBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCards.this, AddDNI.class);
                startActivity(intent);
            }
        });
        //#endregion
    }
}
