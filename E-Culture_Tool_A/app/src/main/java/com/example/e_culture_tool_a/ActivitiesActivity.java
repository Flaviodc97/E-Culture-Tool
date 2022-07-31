package com.example.e_culture_tool_a;

public class ActivitiesActivity {

    Integer ID;
    String Titolo;

    public ActivitiesActivity(Integer ID, String titolo) {
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
