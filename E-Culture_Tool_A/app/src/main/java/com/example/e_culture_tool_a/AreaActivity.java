package com.example.e_culture_tool_a;

public class AreaActivity {

    Integer ID;
    String Nome;

    public AreaActivity(Integer ID, String nome) {
        this.ID = ID;
        Nome = nome;
    }

    public Integer getID() {
        return ID;
    }

    public String getNome() {
        return Nome;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public void setNome(String nome) {
        Nome = nome;
    }
}
