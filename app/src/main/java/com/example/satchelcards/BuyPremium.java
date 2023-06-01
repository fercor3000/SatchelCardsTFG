package com.example.satchelcards;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class BuyPremium extends AppCompatActivity {

    ImageView goBackBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_premium);

        goBackBtn = (ImageView) findViewById(R.id.go_Back);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuyPremium.this, HomeMenu.class);
                startActivity(intent);
            }
        });
    }
}
