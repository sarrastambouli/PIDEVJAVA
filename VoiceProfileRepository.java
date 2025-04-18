package GesUsers.tools;

import java.sql.*;

public class VoiceProfileRepository {
    private final Connection cnx;

    public VoiceProfileRepository() {
        this.cnx = Connexion.getConnection(); // Votre classe de connexion existante
    }

    // Sauvegarde dans voiceit_child_profiles
    public void saveVoiceProfile(int childId, String voiceitId) throws SQLException {
        String query = "INSERT INTO voiceit_child_profiles (child_id, voiceit_id, enrollment_status) VALUES (?, ?, 'PENDING')";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, childId);
            stmt.setString(2, voiceitId);
            stmt.executeUpdate();
        }
    }

    // Vérifie si un profil vocal existe
    public boolean hasVoiceProfile(int childId) throws SQLException {
        String query = "SELECT 1 FROM voiceit_child_profiles WHERE child_id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, childId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}