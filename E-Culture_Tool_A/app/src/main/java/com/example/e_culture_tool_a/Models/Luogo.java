package com.example.e_culture_tool_a.Models;

public class Luogo {
    private String id;
    private String nome;
    private String descrizione;
    private String photo;
    private String author;
    public Luogo(){
    }



    public Luogo(String id, String nome, String descrizione, String photo, String author) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.photo = photo;
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

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
