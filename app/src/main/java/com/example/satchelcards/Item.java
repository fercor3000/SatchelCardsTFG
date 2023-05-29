package com.example.satchelcards;

import android.widget.ImageView;

public class Item {
    private ImageView imageView;
    private String titulo;
    private String id;

    public Item(ImageView imageView, String titulo, String id) {
        this.imageView = imageView;
        this.titulo = titulo;
        this.id = id;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getId() {
        return id;
    }
}


