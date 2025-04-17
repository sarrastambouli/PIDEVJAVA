package entities;

import java.time.LocalDateTime;

public class Theme {
    private int id;
    private String nomTheme;
    private String descriptionTheme;
    private String imageTheme;
    private LocalDateTime dateAjoutTheme;
    private LocalDateTime dateDerniereModification;

    public Theme() {}

    public Theme(int id, String nomTheme, String descriptionTheme, String imageTheme,
                 LocalDateTime dateAjoutTheme, LocalDateTime dateDerniereModification) {
        this.id = id;
        this.nomTheme = nomTheme;
        this.descriptionTheme = descriptionTheme;
        this.imageTheme = imageTheme;
        this.dateAjoutTheme = dateAjoutTheme;
        this.dateDerniereModification = dateDerniereModification;
    }

    // Getters & Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNomTheme() { return nomTheme; }
    public void setNomTheme(String nomTheme) { this.nomTheme = nomTheme; }

    public String getDescriptionTheme() { return descriptionTheme; }
    public void setDescriptionTheme(String descriptionTheme) { this.descriptionTheme = descriptionTheme; }

    public String getImageTheme() { return imageTheme; }
    public void setImageTheme(String imageTheme) { this.imageTheme = imageTheme; }

    public LocalDateTime getDateAjoutTheme() { return dateAjoutTheme; }
    public void setDateAjoutTheme(LocalDateTime dateAjoutTheme) { this.dateAjoutTheme = dateAjoutTheme; }

    public LocalDateTime getDateDerniereModification() { return dateDerniereModification; }
    public void setDateDerniereModification(LocalDateTime dateDerniereModification) { this.dateDerniereModification = dateDerniereModification; }

    @Override
    public String toString() {
        return "Theme{" +
                "id=" + id +
                ", nomTheme='" + nomTheme + '\'' +
                ", descriptionTheme='" + descriptionTheme + '\'' +
                ", imageTheme='" + imageTheme + '\'' +
                ", dateAjoutTheme=" + dateAjoutTheme +
                ", dateDerniereModification=" + dateDerniereModification +
                '}';
    }
}
