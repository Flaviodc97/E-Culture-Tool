package Models;

public class Visita {
    private String id;
    private String nome;
    private String author;
    private String luogoID;
    public Visita(){}


    public Visita(String id, String nome, String author, String luogoID) {
        this.id = id;
        this.nome = nome;
        this.author = author;
        this.luogoID = luogoID;
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
