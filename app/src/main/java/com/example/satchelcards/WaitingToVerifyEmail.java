package com.example.satchelcards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WaitingToVerifyEmail extends ClassBlockOrientation {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_to_verify_email);

        ImageView gifImageView = findViewById(R.id.gifImageView);
        Glide.with(this)
                .asGif()
                .load("https://i.ibb.co/gdXmyDY/logoGIF.gif")
                .apply(RequestOptions.overrideOf(450, 450))
                .into(gifImageView);

        Button yaVerificado = findViewById(R.id.yaVerificado);
        yaVerificado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprobarSiVerificado();
            }
        });

        comprobarSiVerificado();
    }

    public void comprobarSiVerificado() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null && currentUser.isEmailVerified()) {
            Intent intent = new Intent(WaitingToVerifyEmail.this, HomeMenu.class);
            intent.putExtra("registro", true);
            startActivity(intent);
            finish();
        } else {
            Context context = getApplicationContext();
            Toast.makeText(context, "Email no verificado todavía!!.", Toast.LENGTH_SHORT).show();
        }
    }
}

