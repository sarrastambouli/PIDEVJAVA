package entities;

import java.time.LocalDateTime; // taayatlek ll type datetimestamp

public class HistoriqueJeu {
    private int id;
    private int idJeu;
    private int codeIdJeu;
    private String nomJeu;
    private String themeJeu;
    private String demoJeu; // Chemin du fichier MP4
    private String codeJeu; // Chemin du fichier ZIP
    private LocalDateTime dateAjoutJeu;
    private LocalDateTime dateModifiJeu;
    private LocalDateTime dateAjoutHistorique;

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdJeu() { return idJeu; }
    public void setIdJeu(int idJeu) { this.idJeu = idJeu; }

    public int getCodeIdJeu() { return codeIdJeu; }
    public void setCodeIdJeu(int codeIdJeu) { this.codeIdJeu = codeIdJeu; }

    public String getNomJeu() { return nomJeu; }
    public void setNomJeu(String nomJeu) { this.nomJeu = nomJeu; }

    public String getThemeJeu() { return themeJeu; }
    public void setThemeJeu(String themeJeu) { this.themeJeu = themeJeu; }

    public String getDemoJeu() { return demoJeu; }
    public void setDemoJeu(String demoJeu) { this.demoJeu = demoJeu; }

    public String getCodeJeu() { return codeJeu; }
    public void setCodeJeu(String codeJeu) { this.codeJeu = codeJeu; }

    public LocalDateTime getDateAjoutJeu() { return dateAjoutJeu; }
    public void setDateAjoutJeu(LocalDateTime dateAjoutJeu) { this.dateAjoutJeu = dateAjoutJeu; }

    public LocalDateTime getDateModifiJeu() { return dateModifiJeu; }
    public void setDateModifiJeu(LocalDateTime dateModifiJeu) { this.dateModifiJeu = dateModifiJeu; }

    public LocalDateTime getDateAjoutHistorique() { return dateAjoutHistorique; }
    public void setDateAjoutHistorique(LocalDateTime dateAjoutHistorique) { this.dateAjoutHistorique = dateAjoutHistorique; }
}
