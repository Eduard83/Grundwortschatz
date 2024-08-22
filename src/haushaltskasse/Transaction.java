package haushaltskasse;

public class Transaction {
    private int id;
    private String datum;
    private double betrag;
    private String kategorie;
    private String beschreibung;

    public Transaction(int id, String datum, double betrag, String kategorie, String beschreibung) {
        this.id = id;
        this.datum = datum;
        this.betrag = betrag;
        this.kategorie = kategorie;
        this.beschreibung = beschreibung;
    }

    // Getter und Setter Methoden
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public double getBetrag() {
        return betrag;
    }

    public void setBetrag(double betrag) {
        this.betrag = betrag;
    }

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }
}
