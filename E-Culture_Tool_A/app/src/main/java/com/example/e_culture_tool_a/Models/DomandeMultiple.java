package Models;

import java.util.ArrayList;
import java.util.List;

public class DomandeMultiple {
    String id;
    String nome;
    String risposta_giusta;
    List<String> risposte_errate= new ArrayList<>();
    String author;
    String luogoID;
    String zonaID;
    String oggettoID;

    public DomandeMultiple(){}

    public DomandeMultiple(String id, String nome, String risposta_giusta, List<String> risposte_errate, String author, String luogoID, String zonaID, String oggettoID) {

        this.id = id;
        this.nome = nome;
        this.risposta_giusta = risposta_giusta;
        this.risposte_errate = risposte_errate;
        this.author = author;
        this.luogoID = luogoID;
        this.zonaID = zonaID;
        this.oggettoID = oggettoID;
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

    public String getRisposta_giusta() {
        return risposta_giusta;
    }

    public void setRisposta_giusta(String risposta_giusta) {
        this.risposta_giusta = risposta_giusta;
    }

    public List<String> getRisposte_errate() {
        return risposte_errate;
    }

    public void setRisposte_errate(List<String> risposte_errate) {
        this.risposte_errate = risposte_errate;
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
}
