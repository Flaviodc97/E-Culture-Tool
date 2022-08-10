package Models;

public class Oggetti {
    private String id;
    private String nome;
    private String descrizione;
    private String author;
    private String zonaID;
    private String luogoID;
    private String photo;

    public Oggetti() {
    }

    public Oggetti(String id, String nome, String descrizione, String author, String zonaID, String luogoID, String photo) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.author = author;
        this.zonaID = zonaID;
        this.luogoID = luogoID;
        this.photo = photo;
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

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getZonaID() {
        return zonaID;
    }

    public void setZonaID(String zonaID) {
        this.zonaID = zonaID;
    }

    public String getLuogoID() {
        return luogoID;
    }

    public void setLuogoID(String luogoID) {
        this.luogoID = luogoID;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
