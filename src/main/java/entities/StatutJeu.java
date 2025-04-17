package entities;

public enum StatutJeu {
    EN_ATTENTE("En attente"),
    VALIDE("Validé"),
    REFUSE("Refusé");

    private final String libelle;

    StatutJeu(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    public static StatutJeu fromString(String text) {
        for (StatutJeu statut : StatutJeu.values()) {
            if (statut.libelle.equalsIgnoreCase(text)) {
                return statut;
            }
        }
        return EN_ATTENTE; // Valeur par défaut
    }
}