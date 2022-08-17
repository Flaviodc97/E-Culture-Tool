package com.example.e_culture_tool_a.Models;

public class Zone {
    private String nome;
    private String descrizione;
    private String luogoID;
    private String id;

    public Zone() {
    }

    public Zone(String nome, String descrizione, String luogoID, String id) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.luogoID = luogoID;
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

    public String getLuogoID() {
        return luogoID;
    }

    public void setLuogoID(String luogoID) {
        this.luogoID = luogoID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}