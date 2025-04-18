package GesUsers.services;

import GesUsers.entities.User;
import GesUsers.interfaces.IUserService;

import java.util.Date;

import GesUsers.tools.Connexion;
import GesUsers.tools.PasswordHasher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IUserService {

    // Ajoutez la connexion comme attribut de classe
    private Connection cnx;

    // Initialisez la connexion dans le constructeur
    public UserService() {
        this.cnx = Connexion.getConnection();
    }

    public void addUser(User user) {

        String query = "INSERT INTO user (nom, prenom, email, mdp, role, typeenfant, numtel, datenaissance, resultatquiz) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            String hashedPassword = PasswordHasher.hashPassword(user.getMdp());
            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, hashedPassword);
            stmt.setString(5, user.getRole());
            stmt.setString(6, user.getTypeEnfant());
            stmt.setString(7, user.getNumTel());
            // Conversion de java.util.Date en java.sql.Date
            stmt.setDate(8, new java.sql.Date(user.getDateNaissance().getTime()));
            stmt.setInt(9, user.getResultatQuiz());



            stmt.executeUpdate();

            // Récupérer l'ID auto-généré
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) user.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout d'un utilisateur : " + e.getMessage());
        }
    }


    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, nom, prenom, email, role, typeenfant, numtel, datenaissance, resultatquiz FROM user";
        // Note: On exclut volontairement le mot de passe

        try (PreparedStatement stmt = cnx.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                //user.setMdp(rs.getString("mdp"));
                user.setRole(rs.getString("role"));
                user.setTypeEnfant(rs.getString("typeenfant"));
                user.setNumTel(rs.getString("numtel"));

                // CORRECTION ICI : Gestion propre de la date
                //java.sql.Date sqlDate = rs.getDate("datenaissance");
                user.setDateNaissance(rs.getDate("datenaissance"));

                user.setResultatQuiz(rs.getInt("resultatquiz"));


                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }

        return users;
    }


    public boolean deleteUser(int id) {
        try {
            // Vérifier les dépendances
            String checkQuery = "SELECT COUNT(*) FROM enfant WHERE parent_id = ?";
            try (PreparedStatement checkStmt = cnx.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, id);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.err.println("Impossible de supprimer : utilisateur a des enfants associés");
                    return false;
                }
            }

            // Suppression
            String deleteQuery = "DELETE FROM user WHERE id = ?";
            try (PreparedStatement deleteStmt = cnx.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, id);
                int rows = deleteStmt.executeUpdate();
                return rows > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur suppression: " + e.getMessage());
            return false;
        }
    }


    /*
    * public void deleteUserWithChildren(int id) {
    try {
        // 1. Vérification des enfants associés
        String checkChildrenQuery = "SELECT COUNT(*) FROM enfant WHERE parent_id = ?";
        try (PreparedStatement checkStmt = cnx.prepareStatement(checkChildrenQuery)) {
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("⚠️ Attention : L'utilisateur ID " + id + " a " + rs.getInt(1) + " enfant(s) associé(s)");

                // 2. Suppression des enfants d'abord
                String deleteChildrenQuery = "DELETE FROM enfant WHERE parent_id = ?";
                try (PreparedStatement deleteChildrenStmt = cnx.prepareStatement(deleteChildrenQuery)) {
                    deleteChildrenStmt.setInt(1, id);
                    int childrenDeleted = deleteChildrenStmt.executeUpdate();
                    System.out.println("🗑️ " + childrenDeleted + " enfant(s) supprimé(s)");
                }
            }
        }

        // 3. Suppression du parent
        String deleteUserQuery = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement deleteUserStmt = cnx.prepareStatement(deleteUserQuery)) {
            deleteUserStmt.setInt(1, id);
            int rowsAffected = deleteUserStmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Utilisateur ID " + id + " supprimé avec succès");
            } else {
                System.out.println("⚠️ Aucun utilisateur trouvé avec l'ID " + id);
            }
        }

    } catch (SQLException e) {
        System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
        e.printStackTrace();
    }
}
    *
    * */


    public User getUserById(int id) {
        String query = "SELECT id, nom, prenom, email, role, typeenfant, numtel, datenaissance FROM user WHERE id = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setTypeEnfant(rs.getString("typeenfant"));
                user.setNumTel(rs.getString("numtel"));

                java.sql.Date sqlDate = rs.getDate("datenaissance");
                /*if (sqlDate != null) {
                    user.setDateNaissance(new java.util.Date(sqlDate.getTime()));
                }*/

                return user;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération: " + e.getMessage());
        }
        return null;
    }

    public boolean updateUser(User user) {
        String query = "UPDATE user SET nom = ?, prenom = ?, email = ?, role = ?, "
                + "typeenfant = ?, numtel = ?, datenaissance = ? "
                + "WHERE id = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getTypeEnfant());
            stmt.setString(6, user.getNumTel());

            if (user.getDateNaissance() != null) {
                stmt.setDate(7, new java.sql.Date(user.getDateNaissance().getTime()));
            } else {
                stmt.setNull(7, java.sql.Types.DATE);
            }

            stmt.setInt(8, user.getId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.err.println("Erreur mise à jour: " + e.getMessage());
            return false;
        }
    }

    public User authenticate(String email, String plainPassword) throws SQLException {
        String query = "SELECT * FROM user WHERE email = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("mdp");
                    if (PasswordHasher.checkPassword(plainPassword, hashedPassword)) {
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setNom(rs.getString("nom"));
                        user.setPrenom(rs.getString("prenom"));
                        user.setEmail(rs.getString("email"));
                        user.setRole(rs.getString("role"));
                        user.setTypeEnfant(rs.getString("typeenfant"));
                        user.setNumTel(rs.getString("numtel"));
                        user.setDateNaissance(rs.getDate("datenaissance"));
                        user.setResultatQuiz(rs.getInt("resultatquiz"));
                        return user;
                    } else {
                        System.out.println("Mot de passe incorrect pour l'email: " + email);
                    }
                } else {
                    System.out.println("Aucun utilisateur trouvé avec l'email: " + email);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur d'authentification pour " + email + ": " + e.getMessage());
            throw e;
        }
        return null;
    }


}