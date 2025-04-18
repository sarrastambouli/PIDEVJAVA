package GesUsers.services;

import GesUsers.interfaces.IEnfantService;
import GesUsers.entities.Enfant;
import GesUsers.tools.Connexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnfantService implements IEnfantService {

    private Connection cnx;

    public EnfantService() {
        this.cnx = Connexion.getConnection();
    }

    @Override
    public List<Enfant> getEnfantsByParent(int parentId) {
        List<Enfant> enfants = new ArrayList<>();
        String query = "SELECT * FROM enfant WHERE parent_id = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, parentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Enfant enfant = new Enfant();
                    enfant.setId(rs.getInt("id"));
                    enfant.setParentId(rs.getInt("parent_id"));
                    enfant.setUsername(rs.getString("username"));
                    enfant.setCode(rs.getString("code"));
                    enfant.setType(rs.getString("type"));
                    enfant.setResQuiz(rs.getInt("resquiz"));

                    enfants.add(enfant);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des enfants: " + e.getMessage());
        }

        return enfants;
    }


    @Override
    public void addEnfant(Enfant enfant) {
        String query = "INSERT INTO enfant (parent_id, username, code, type, resquiz) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, enfant.getParentId());
            stmt.setString(2, enfant.getUsername());
            stmt.setString(3, enfant.getCode());
            stmt.setString(4, enfant.getType());
            stmt.setInt(5, enfant.getResQuiz());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    enfant.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout d'un enfant: " + e.getMessage());
        }
    }

    // NOUVEAU: Récupération par ID
    public Enfant getEnfantById(int id) {
        String query = "SELECT * FROM enfant WHERE id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Enfant e = new Enfant();
                    e.setId(rs.getInt("id"));
                    e.setParentId(rs.getInt("parent_id"));
                    e.setUsername(rs.getString("username"));
                    e.setCode(rs.getString("code"));
                    e.setType(rs.getString("type"));
                    e.setResQuiz(rs.getInt("resquiz"));
                    return e;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getEnfantById: " + e.getMessage());
        }
        return null;
    }


    // Suppression CORRIGÉE
    @Override
    public boolean deleteEnfant(int id) {
        try {
            // 1. Supprimer les dépendances dans app_enf
            String deleteAppEnfQuery = "DELETE FROM app_enf WHERE enf_id = ?";
            try (PreparedStatement stmt = cnx.prepareStatement(deleteAppEnfQuery)) {
                stmt.setInt(1, id);
                stmt.executeUpdate(); // Supprime les entrées liées
            }

            // 2. Supprimer l'enfant
            String deleteEnfantQuery = "DELETE FROM enfant WHERE id = ?";
            try (PreparedStatement stmt = cnx.prepareStatement(deleteEnfantQuery)) {
                stmt.setInt(1, id);
                int rowsDeleted = stmt.executeUpdate();
                return rowsDeleted > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
            return false;
        }
    }





    // Lister TOUS les enfants (simplifié)
    public List<Enfant> getAllEnfants() {
        List<Enfant> enfants = new ArrayList<>();
        String query = "SELECT * FROM enfant";
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Enfant e = new Enfant();
                e.setId(rs.getInt("id"));
                e.setParentId(rs.getInt("parent_id"));
                e.setUsername(rs.getString("username"));
                e.setCode(rs.getString("code"));
                e.setType(rs.getString("type"));
                e.setResQuiz(rs.getInt("resquiz"));
                enfants.add(e);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getAllEnfants: " + e.getMessage());
        }
        return enfants;
    }

    @Override
    public void updateEnfant(Enfant enfant) {
        String query = "UPDATE enfant SET username=?, code=?, type=?, resquiz=? WHERE id=?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, enfant.getUsername());
            stmt.setString(2, enfant.getCode());
            stmt.setString(3, enfant.getType());
            stmt.setInt(4, enfant.getResQuiz());
            stmt.setInt(5, enfant.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur updateEnfant: " + e.getMessage());
        }
    }


    public Enfant authenticate(String username, String code) throws SQLException {
        String query = "SELECT * FROM enfant WHERE username = ? AND code = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, code);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Enfant enfant = new Enfant();
                    enfant.setId(rs.getInt("id"));
                    enfant.setParentId(rs.getInt("parent_id"));
                    enfant.setUsername(rs.getString("username"));
                    enfant.setCode(rs.getString("code"));
                    enfant.setType(rs.getString("type"));
                    enfant.setResQuiz(rs.getInt("resquiz"));
                    return enfant;
                }
            }
        }
        return null;
    }


}
