package com.example.satchelcards;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class LoadingData extends ClassBlockOrientation {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_data);

        ImageView gifImageView = findViewById(R.id.gifImageView);
        Glide.with(this)
                .asGif()
                .load("https://i.ibb.co/gdXmyDY/logoGIF.gif")
                .apply(RequestOptions.overrideOf(450, 450))
                .into(gifImageView);
    }
}

