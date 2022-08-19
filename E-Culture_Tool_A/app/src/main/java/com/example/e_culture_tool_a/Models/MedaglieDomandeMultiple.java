package com.example.e_culture_tool_a.Models;

public class MedaglieDomandeMultiple {

    String id;
    String nome;
    Integer punti;
    String oggettoID;
    String author;

    public MedaglieDomandeMultiple(){}

    public MedaglieDomandeMultiple(String id, String nome, Integer punti, String oggettoID, String author) {
        this.id = id;
        this.nome = nome;
        this.punti = punti;
        this.oggettoID = oggettoID;
        this.author = author;
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

    public Integer getPunti() {
        return punti;
    }

    public void setPunti(Integer punti) {
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
}
