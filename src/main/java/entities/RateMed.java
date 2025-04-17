package entities;

public class RateMed {
    private int id;
    private int jeuId;
    private String description; // Chemin ou URL du fichier PDF
    private int nbreEtoiles;
    private int medecinId;

    public RateMed() {}

    public RateMed(int id, int jeuId, String description, int nbreEtoiles, int medecinId) {
        this.id = id;
        this.jeuId = jeuId;
        this.description = description;
        this.nbreEtoiles = nbreEtoiles;
        this.medecinId = medecinId;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getJeuId() { return jeuId; }
    public void setJeuId(int jeuId) { this.jeuId = jeuId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getNbreEtoiles() { return nbreEtoiles; }
    public void setNbreEtoiles(int nbreEtoiles) { this.nbreEtoiles = nbreEtoiles; }

    public int getMedecinId() { return medecinId; }
    public void setMedecinId(int medecinId) { this.medecinId = medecinId; }
}
