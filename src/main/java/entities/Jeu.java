package entities;

import java.time.LocalDateTime;

public class Jeu {
    private int id;
    private int themeId;
    private String nomJeu;
    private String typeEnf;
    private LocalDateTime dateCreation;
    private String cheminJeu;
    private String donnees;
    private int codeJeu;
    private String image;
    private String DemoJeu;
    private int niveau;
    private String description;
    private int likes;
    private int favoris;
    private int deslikes;
    private boolean valide;
    private StatutJeu statut;


    // Getters & Setters
    public int getId() {  // Le nom doit exactement correspondre
        return this.id;
    }
    public void setId(int id) { this.id = id; }

    public int getThemeId() { return themeId; }
    public void setThemeId(int themeId) { this.themeId = themeId; }

    public String getNomJeu() { return nomJeu; }
    public void setNomJeu(String nomJeu) { this.nomJeu = nomJeu; }

    public String getTypeEnf() { return typeEnf; }
    public void setTypeEnf(String typeEnf) { this.typeEnf = typeEnf; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public String getCheminJeu() { return cheminJeu; }
    public void setCheminJeu(String cheminJeu) { this.cheminJeu = cheminJeu; }

    public String getDonnees() { return donnees; }
    public void setDonnees(String donnees) { this.donnees = donnees; }

    public int getCodeJeu() { return codeJeu; }
    public void setCodeJeu(int codeJeu) { this.codeJeu = codeJeu; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getNiveau() { return niveau; }
    public void setNiveau(int niveau) { this.niveau = niveau; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String  getDemoJeu() { return DemoJeu; }
    public void  getDemoJeu(String DemoJeu) { this.DemoJeu = DemoJeu; }
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public int getFavoris() { return favoris; }
    public void setFavoris(int favoris) { this.favoris = favoris; }

    public int getDeslikes() { return deslikes; }
    public void setDeslikes(int deslikes) { this.deslikes = deslikes; }

    public boolean isValide() { return valide; }
    public void setValide(boolean valide) { this.valide = valide; }

    public StatutJeu getStatut() {
        return statut;
    }

    public void setStatut(StatutJeu statut) {
        this.statut = statut;
    }

    public void setStatutFromString(String statut) {
        this.statut = StatutJeu.fromString(statut);
    }

}
