package Models;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class DomandeTempo {
    String id;
    String author;
    String luogoID;
    String zonaID;
    String oggettoID;
    String nome;
    Integer tempo;
    ArrayList<DomandeMultiple> dm = new ArrayList<>();
    public DomandeTempo(){}

    public DomandeTempo(String id, String author, String luogoID, String zonaID, String oggettoID, String nome, Integer tempo, ArrayList<DomandeMultiple> dm) {
        this.id = id;
        this.author = author;
        this.luogoID = luogoID;
        this.zonaID = zonaID;
        this.oggettoID = oggettoID;
        this.nome = nome;
        this.tempo = tempo;
        this.dm = dm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getZonaID() {
        return zonaID;
    }

    public void setZonaID(String zonaID) {
        this.zonaID = zonaID;
    }

    public String getOggettoID() {
        return oggettoID;
    }

    public void setOggettoID(String oggettoID) {
        this.oggettoID = oggettoID;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getTempo() {
        return tempo;
    }

    public void setTempo(Integer tempo) {
        this.tempo = tempo;
    }

    public ArrayList<DomandeMultiple> getDm() {
        return dm;
    }

    public void setDm(ArrayList<DomandeMultiple> dm) {
        this.dm = dm;
    }
}
