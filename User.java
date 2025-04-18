package GesUsers.entities;

import java.sql.Date;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String mdp;
    private String role;
    private String typeEnfant;
    private String numTel;
    private Date dateNaissance;
    private int resultatQuiz;

    // Constructeur par défaut (obligatoire pour JDBC/ORM)
    public User() {}

    // Constructeur paramétré avec TOUS les attributs (utile pour créer un objet prêt à être inséré en base)
    public User(String nom, String prenom, String email, String mdp, String role, String typeEnfant, String numTel, Date dateNaissance, int resultatQuiz) {
        //this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.role = role;
        this.typeEnfant = typeEnfant;
        this.numTel = numTel;
        this.dateNaissance = dateNaissance;
        this.resultatQuiz = resultatQuiz;
    }


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
    public Date getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(Date date) {this.dateNaissance = date; }
    public int getResultatQuiz() { return resultatQuiz; }
    public void setResultatQuiz(int resultatQuiz) {this.resultatQuiz = resultatQuiz; }

}