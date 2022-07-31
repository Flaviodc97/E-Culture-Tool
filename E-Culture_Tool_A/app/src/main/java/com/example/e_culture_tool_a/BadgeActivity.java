package com.example.e_culture_tool_a;

public class BadgeActivity {

    Integer ID;
    String Titolo;
    Integer Punti;

    public BadgeActivity(Integer ID, String titolo, Integer punti) {
        this.ID = ID;
        Titolo = titolo;
        Punti = punti;
    }

    public Integer getID() {
        return ID;
    }

    public String getTitolo() {
        return Titolo;
    }

    public Integer getPunti() {
        return Punti;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public void setTitolo(String titolo) {
        Titolo = titolo;
    }

    public void setPunti(Integer punti) {
        Punti = punti;
    }
}
