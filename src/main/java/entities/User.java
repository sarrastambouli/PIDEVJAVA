package entities;

import java.time.LocalDate;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String mdp;
    private String role;
    private String typeEnfant;
    private String numTel;
    private LocalDate dateNaissance;
    private LocalDate dateInscription;
    private String region;
    private String etat;
    private String photoProfil;
    private String discr;
    private int resultatQuiz;

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMdp() { return mdp; }
    public void setMdp(String mdp) { this.mdp = mdp; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getTypeEnfant() { return typeEnfant; }
    public void setTypeEnfant(String typeEnfant) { this.typeEnfant = typeEnfant; }

    public String getNumTel() { return numTel; }
    public void setNumTel(String numTel) { this.numTel = numTel; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }

    public String getPhotoProfil() { return photoProfil; }
    public void setPhotoProfil(String photoProfil) { this.photoProfil = photoProfil; }

    public String getDiscr() { return discr; }
    public void setDiscr(String discr) { this.discr = discr; }

    public int getResultatQuiz() { return resultatQuiz; }
    public void setResultatQuiz(int resultatQuiz) { this.resultatQuiz = resultatQuiz; }
}
