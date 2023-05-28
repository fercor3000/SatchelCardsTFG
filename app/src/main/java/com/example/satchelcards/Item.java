package com.example.satchelcards;

public class Item {
    private int imageResId;
    private String titulo;

    public Item(int imageResId, String titulo) {
        this.imageResId = imageResId;
        this.titulo = titulo;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTitulo() {
        return titulo;
    }
}


