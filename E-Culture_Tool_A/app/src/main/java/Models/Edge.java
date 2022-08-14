package Models;

public class Edge {
    private String id;
    private String zonaInizio;
    private String zonaFine;
    private String author;
    private String visitaID;

    public Edge(){}

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Edge(String id, String zonaInizio, String zonaFine, String author, String visitaID) {
        this.id = id;
        this.zonaInizio = zonaInizio;
        this.zonaFine = zonaFine;
        this.author = author;
        this.visitaID = visitaID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZonaInizio() {
        return zonaInizio;
    }

    public void setZonaInizio(String zonaInizio) {
        this.zonaInizio = zonaInizio;
    }

    public String getZonaFine() {
        return zonaFine;
    }

    public void setZonaFine(String zonaFine) {
        this.zonaFine = zonaFine;
    }

    public String getVisitaID() {
        return visitaID;
    }

    public void setVisitaID(String visitaID) {
        this.visitaID = visitaID;
    }
}
