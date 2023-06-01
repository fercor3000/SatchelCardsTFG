package com.example.satchelcards;

import android.net.Uri;

public class Item {
    private Uri imageViewUri;
    private String titulo;
    private String id;
    private String tipo;

    public Item(Uri imageViewUri, String titulo, String id, String tipo) {
        this.imageViewUri = imageViewUri;
        this.titulo = titulo;
        this.id = id;
        this.tipo = tipo;
    }

    public Uri getImageViewUri() {
        return imageViewUri;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getId() {
        return id;
    }
    public String getTipo() {
        return tipo;
    }
}


