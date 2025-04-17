package entities;

import java.util.Date;

public class Notification {
    private int id;
    private String message;
    private Date dateCreation;
    private boolean lue;
    private String type; // Type: 'VALIDATION_DEMANDE', 'VALIDATION_REPONSE', 'AUTRE'
    private String entiteType; // Entité concernée: 'JEU', 'USER', 'AUTRE'
    private int entiteId;
    private int destinataireId;
    private Integer emetteurId;

    public Notification() {}

    public Notification(int id, String message, Date dateCreation, boolean lue, String type,
                        String entiteType, int entiteId, int destinataireId, Integer emetteurId) {
        this.id = id;
        this.message = message;
        this.dateCreation = dateCreation;
        this.lue = lue;
        this.type = type;
        this.entiteType = entiteType;
        this.entiteId = entiteId;
        this.destinataireId = destinataireId;
        this.emetteurId = emetteurId;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public boolean isLue() { return lue; }
    public void setLue(boolean lue) { this.lue = lue; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getEntiteType() { return entiteType; }
    public void setEntiteType(String entiteType) { this.entiteType = entiteType; }

    public int getEntiteId() { return entiteId; }
    public void setEntiteId(int entiteId) { this.entiteId = entiteId; }

    public int getDestinataireId() { return destinataireId; }
    public void setDestinataireId(int destinataireId) { this.destinataireId = destinataireId; }

    public Integer getEmetteurId() { return emetteurId; }
    public void setEmetteurId(Integer emetteurId) { this.emetteurId = emetteurId; }
}
