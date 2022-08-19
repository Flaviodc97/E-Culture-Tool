package com.example.e_culture_tool_a.Models;

public class Visita {
    private String id;
    private String nome;
    private String author;
    private String luogoID;
    private String file;
    private String message;
    public Visita(){}

    public Visita(String id, String nome, String author, String luogoID, String file, String message) {
        this.id = id;
        this.nome = nome;
        this.author = author;
        this.luogoID = luogoID;
        this.file = file;
        this.message = message;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLuogoID() {
        return luogoID;
    }

    public void setLuogoID(String luogoID) {
        this.luogoID = luogoID;
    }
}
