package com.example.e_culture_tool_a;

import java.net.IDN;

public class Object {

    Integer ID;
    String Titolo;
    String Descrizione;
    String Foto;
    String QR;
    String Tipo;

    public Object(Integer ID, String titolo, String descrizione, String foto, String QR, String tipo) {
        this.ID = ID;
        Titolo = titolo;
        Descrizione = descrizione;
        Foto = foto;
        this.QR = QR;
        Tipo = tipo;
    }

    public Integer getID() {
        return ID;
    }

    public String getTitolo() {
        return Titolo;
    }

    public String getDescrizione() {
        return Descrizione;
    }

    public String getFoto() {
        return Foto;
    }

    public String getQR() {
        return QR;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public void setTitolo(String titolo) {
        Titolo = titolo;
    }

    public void setDescrizione(String descrizione) {
        Descrizione = descrizione;
    }

    public void setFoto(String foto) {
        Foto = foto;
    }

    public void setQR(String QR) {
        this.QR = QR;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }
}
