package com.example.satchelcards;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class AddCards extends AppCompatActivity {

    //#region VARIABLES
    ImageView gobackBtn;
    LinearLayout addDniBtn;
    LinearLayout addCreditBtn;
    LinearLayout addTransportBtn;
    LinearLayout addGiftBtn;
    LinearLayout addAccessBtn;

    private ImageView helpBtn;
    //#endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_card_select);

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

        //#region OBTENER ELEMENTOS
        gobackBtn = (ImageView)findViewById(R.id.goBack);
        addDniBtn = (LinearLayout) findViewById(R.id.addDni);
        addCreditBtn = (LinearLayout) findViewById(R.id.creditCard);
        addTransportBtn = (LinearLayout) findViewById(R.id.transportCard);
        addGiftBtn = (LinearLayout) findViewById(R.id.loyaltyCard);
        addAccessBtn = (LinearLayout) findViewById(R.id.accessCard);
        //#endregion

        //#region AL PULSAR VOLVER...
        gobackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCards.this, HomeMenu.class);
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

        //#region AL PULSAR ADDCREDIT
        addCreditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCards.this, AddCredit.class);
                startActivity(intent);
            }
        });

        //#region AL PULSAR ADDTRANSPORT
        addTransportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCards.this, AddTransport.class);
                startActivity(intent);
            }
        });

        //#region AL PULSAR ADDGIFT
        addGiftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCards.this, AddGift.class);
                startActivity(intent);
            }
        });
        //#endregion

        //#region AL PULSAR ADDACCESS
        addAccessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCards.this, AddAccess.class);
                startActivity(intent);
            }
        });
        //#endregion
    }
}
