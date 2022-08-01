package com.example.e_culture_tool_a;

public class Activity {

    Integer ID;
    String Titolo;

    public Activity(Integer ID, String titolo) {
        this.ID = ID;
        Titolo = titolo;
    }

    public Integer getID() {
        return ID;
    }

    public String getTitolo() {
        return Titolo;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public void setTitolo(String titolo) {
        Titolo = titolo;
    }
}
