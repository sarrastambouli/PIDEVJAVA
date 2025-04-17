package services;

import entities.Jeu;
import entities.StatutJeu;
import interfaces.IJeuService;
import tools.MyConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JeuService implements IJeuService {

    @Override
    public void addJeu(Jeu jeu) throws SQLException {
        String query = "INSERT INTO jeu (nom_jeu, description, theme_id, type_enf, niveau, image, donnees, code_jeu, date_creation, likes, favoris, deslikes, valide) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, jeu.getNomJeu());
            pst.setString(2, jeu.getDescription());
            pst.setInt(3, jeu.getThemeId());
            pst.setString(4, jeu.getTypeEnf());
            pst.setInt(5, jeu.getNiveau());
            pst.setString(6, jeu.getImage());
            pst.setString(7, jeu.getDonnees());
            pst.setInt(8, jeu.getCodeJeu());
            pst.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            pst.setInt(10, 0); // likes
            pst.setInt(11, 0); // favoris
            pst.setInt(12, 0); // dislikes
            pst.setBoolean(13, false); // valide

            pst.executeUpdate();

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    jeu.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void updateJeu(Jeu jeu, int id) {
        try {
            String requete = "UPDATE jeu SET theme_id=?, nom_jeu=?, type_enf=?, date_creation=?, cheminjeu=?, donnees=?, code_jeu=?, image=?, niveau=?, description=?, likes=?, favoris=?, deslikes=?, valide=? WHERE id=?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, jeu.getThemeId());
            pst.setString(2, jeu.getNomJeu());
            pst.setString(3, jeu.getTypeEnf());
            pst.setTimestamp(4, Timestamp.valueOf(jeu.getDateCreation()));
            pst.setString(5, jeu.getCheminJeu());
            pst.setString(6, jeu.getDonnees());
            pst.setInt(7, jeu.getCodeJeu());
            pst.setString(8, jeu.getImage());
            pst.setInt(9, jeu.getNiveau());
            pst.setString(10, jeu.getDescription());
            pst.setInt(11, jeu.getLikes());
            pst.setInt(12, jeu.getFavoris());
            pst.setInt(13, jeu.getDeslikes());
            pst.setBoolean(14, jeu.isValide());
            pst.setInt(15, id);
            pst.executeUpdate();
            System.out.println("Jeu mis à jour");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteJeu(int id) {
        try {
            String requete = "DELETE FROM jeu WHERE id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Jeu supprimé");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Jeu getJeuById(int id) {
        Jeu jeu = null;
        try {
            String requete = "SELECT * FROM jeu WHERE id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                jeu = mapResultSetToJeu(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return jeu;
    }
    public List<Jeu> getJeuxByStatut(StatutJeu statut) {
        List<Jeu> jeux = new ArrayList<>();
        try {
            String requete = "SELECT * FROM jeu WHERE statut = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setString(1, statut.name());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                jeux.add(mapResultSetToJeu(rs));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return jeux;
    }
    public List<Jeu> getAllJeux() {
        List<Jeu> jeux = new ArrayList<>();
        String requete = "SELECT * FROM jeu";

        try (Statement st = MyConnection.getInstance().getCnx().createStatement();
             ResultSet rs = st.executeQuery(requete)) {

            while (rs.next()) {
                jeux.add(mapResultSetToJeu(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des jeux: " + e.getMessage());
            e.printStackTrace();
        }
        return jeux;
    }

    @Override
    public List<Jeu> getAllJeuxByThemeId(int themeId) {
        List<Jeu> jeux = new ArrayList<>();
        try {
            String requete = "SELECT * FROM jeu WHERE theme_id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, themeId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                jeux.add(mapResultSetToJeu(rs));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return jeux;
    }

    public int getLastInsertedId() throws SQLException {
        int id = -1;
        String requete = "SELECT LAST_INSERT_ID() as last_id";
        Statement st = MyConnection.getInstance().getCnx().createStatement();
        ResultSet rs = st.executeQuery(requete);
        if (rs.next()) {
            id = rs.getInt("last_id");
        }
        return id;
    }

    private Jeu mapResultSetToJeu(ResultSet rs) throws SQLException {
        Jeu jeu = new Jeu();
        jeu.setId(rs.getInt("id"));
        jeu.setThemeId(rs.getInt("theme_id"));
        jeu.setNomJeu(rs.getString("nom_jeu"));
        jeu.setTypeEnf(rs.getString("type_enf"));
        jeu.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        jeu.setCheminJeu(rs.getString("cheminjeu"));
        jeu.setDonnees(rs.getString("donnees"));
        jeu.setCodeJeu(rs.getInt("code_jeu"));
        jeu.setImage(rs.getString("image"));
        jeu.setNiveau(rs.getInt("niveau"));
        jeu.setDescription(rs.getString("description"));
        jeu.setLikes(rs.getInt("likes"));
        jeu.setFavoris(rs.getInt("favoris"));
        jeu.setDeslikes(rs.getInt("deslikes"));
        jeu.setValide(rs.getBoolean("valide"));
        return jeu;
    }
}
