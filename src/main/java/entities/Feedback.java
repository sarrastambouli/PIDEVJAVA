package entities;

import java.sql.Date;

public class Feedback {
    private int id;
    private int reclamationParentId;
    private String title;
    private String message;
    private Date date;
    private Boolean lu;

    // Constructeurs
    public Feedback() {}

    public Feedback(int id, int reclamationParentId, String title, String message, Date date, Boolean lu) {
        this.id = id;
        this.reclamationParentId = reclamationParentId;
        this.title = title;
        this.message = message;
        this.date = date;
        this.lu = lu;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReclamationParentId() { return reclamationParentId; }
    public void setReclamationParentId(int reclamationParentId) { this.reclamationParentId = reclamationParentId; }

    public Boolean getLu() {
        return lu;
    }

    public void setLu(Boolean lu) {
        this.lu = lu;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", reclamationParentId=" + reclamationParentId +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", date=" + date +
                '}';
    }
}
