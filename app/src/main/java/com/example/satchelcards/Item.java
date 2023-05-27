package com.example.satchelcards;

public class Item {
    private int imageResId;
    private String titulo;
    private String subtitulo;
    private String propietario;
    private String cvv;

    public Item(int imageResId, String titulo, String subtitulo, String propietario, String cvv) {
        this.imageResId = imageResId;
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.propietario = propietario;
        this.cvv = cvv;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public String getPropietario() {
        return propietario;
    }

    public String getCVV() {
        return cvv;
    }
}


