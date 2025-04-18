package GesUsers.entities;

import java.time.LocalDateTime;

public class VoiceItChildProfile {
    private int id;
    private int childId; // Référence à l'ID de l'enfant
    private String voiceitId;
    private String enrollmentStatus; // "PENDING" ou "COMPLETED"
    private LocalDateTime lastUpdated;

    // Constructeurs
    public VoiceItChildProfile() {}

    public VoiceItChildProfile(int childId, String voiceitId) {
        this.childId = childId;
        this.voiceitId = voiceitId;
        this.enrollmentStatus = "PENDING";
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters/Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getChildId() { return childId; }
    public void setChildId(int childId) { this.childId = childId; }
    public String getVoiceitId() { return voiceitId; }
    public void setVoiceitId(String voiceitId) {
        this.voiceitId = voiceitId;
    }
    public String getEnrollmentStatus() { return enrollmentStatus; }
    public void setEnrollmentStatus(String enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
    }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

}