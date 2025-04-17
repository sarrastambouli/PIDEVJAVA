package services;

import entities.User;
import interfaces.IUserService;
import tools.MyConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IUserService {

    @Override
    public void addUser(User u) throws SQLException {
        String sql = "INSERT INTO user (nom, prenom, email, mdp, role, typeenfant, numtel, datenaissance, date_inscription, region, etat, photo_profil, discr, resultatquiz) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setString(1, u.getNom());
        pst.setString(2, u.getPrenom());
        pst.setString(3, u.getEmail());
        pst.setString(4, u.getMdp());
        pst.setString(5, u.getRole());
        pst.setString(6, u.getTypeEnfant());
        pst.setString(7, u.getNumTel());
        pst.setDate(8, Date.valueOf(u.getDateNaissance()));
        pst.setDate(9, Date.valueOf(u.getDateInscription()));
        pst.setString(10, u.getRegion());
        pst.setString(11, u.getEtat());
        pst.setString(12, u.getPhotoProfil());
        pst.setString(13, u.getDiscr());
        pst.setInt(14, u.getResultatQuiz());
        pst.executeUpdate();
        System.out.println("User ajouté !");
    }

    @Override
    public void updateUser(User u, int id) throws SQLException {
        String sql = "UPDATE user SET nom = ?, prenom = ?, email = ?, mdp = ?, role = ?, typeenfant = ?, numtel = ?, datenaissance = ?, date_inscription = ?, region = ?, etat = ?, photo_profil = ?, discr = ?, resultatquiz = ? WHERE id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setString(1, u.getNom());
        pst.setString(2, u.getPrenom());
        pst.setString(3, u.getEmail());
        pst.setString(4, u.getMdp());
        pst.setString(5, u.getRole());
        pst.setString(6, u.getTypeEnfant());
        pst.setString(7, u.getNumTel());
        pst.setDate(8, Date.valueOf(u.getDateNaissance()));
        pst.setDate(9, Date.valueOf(u.getDateInscription()));
        pst.setString(10, u.getRegion());
        pst.setString(11, u.getEtat());
        pst.setString(12, u.getPhotoProfil());
        pst.setString(13, u.getDiscr());
        pst.setInt(14, u.getResultatQuiz());
        pst.setInt(15, id);
        pst.executeUpdate();
        System.out.println("User mis à jour !");
    }

    @Override
    public void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM user WHERE id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setInt(1, id);
        pst.executeUpdate();
        System.out.println("User supprimé !");
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";
        Statement st = MyConnection.getInstance().getCnx().createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            users.add(resultToUser(rs));
        }
        return users;
    }

    @Override
    public User getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM user WHERE id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return resultToUser(rs);
        }
        return null;
    }

    @Override
    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM user WHERE email = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setString(1, email);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return resultToUser(rs);
        }
        return null;
    }

    private User resultToUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
        u.setMdp(rs.getString("mdp"));
        u.setRole(rs.getString("role"));
        u.setTypeEnfant(rs.getString("typeenfant"));
        u.setNumTel(rs.getString("numtel"));
        u.setDateNaissance(rs.getDate("datenaissance").toLocalDate());
        u.setDateInscription(rs.getDate("date_inscription").toLocalDate());
        u.setRegion(rs.getString("region"));
        u.setEtat(rs.getString("etat"));
        u.setPhotoProfil(rs.getString("photo_profil"));
        u.setDiscr(rs.getString("discr"));
        u.setResultatQuiz(rs.getInt("resultatquiz"));
        return u;
    }
}
