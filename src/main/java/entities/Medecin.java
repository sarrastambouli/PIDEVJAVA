package entities;

public class Medecin {
    private int id;
    private int userId;  // Référence à l'utilisateur
    private String type;

    public Medecin() {}

    public Medecin(int id, int userId, String type) {
        this.id = id;
        this.userId = userId;
        this.type = type;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
