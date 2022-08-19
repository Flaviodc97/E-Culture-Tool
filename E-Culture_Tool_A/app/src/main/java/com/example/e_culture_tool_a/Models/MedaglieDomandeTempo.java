package com.example.e_culture_tool_a.Models;

public class MedaglieDomandeTempo {
    String id;
    String nome;
    Double punti;
    String oggettoID;
    String author;
    String tipo;

    public MedaglieDomandeTempo(){}

    public MedaglieDomandeTempo(String id, String nome, Double punti, String oggettoID, String author, String tipo) {
        this.id = id;
        this.nome = nome;
        this.punti=punti;
        this.oggettoID = oggettoID;
        this.author = author;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPunti() {
        return punti;
    }

    public void setPunti(Double punti) {
        this.punti = punti;
    }

    public String getOggettoID() {
        return oggettoID;
    }

    public void setOggettoID(String oggettoID) {
        this.oggettoID = oggettoID;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
