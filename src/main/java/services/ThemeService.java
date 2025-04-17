package services;

import entities.Theme;
import interfaces.IThemeService;
import tools.MyConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ThemeService implements IThemeService {

    public static void addTheme(Theme theme) {
        String query = "INSERT INTO theme (nom_theme, description_theme, image_theme, date_ajout_theme, date_derniere_modfication) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setString(1, theme.getNomTheme());
            pst.setString(2, theme.getDescriptionTheme());
            pst.setString(3, theme.getImageTheme());
            pst.setTimestamp(4, Timestamp.valueOf(theme.getDateAjoutTheme()));
            pst.setTimestamp(5, Timestamp.valueOf(theme.getDateDerniereModification()));
            pst.executeUpdate();
            System.out.println("Theme ajouté avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du thème : " + e.getMessage());
        }
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
    public void updateTheme(Theme theme, int id) {
        String query = "UPDATE theme SET nom_theme = ?, description_theme = ?, image_theme = ?, date_derniere_modfication = ? WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setString(1, theme.getNomTheme());
            pst.setString(2, theme.getDescriptionTheme());
            pst.setString(3, theme.getImageTheme());
            pst.setTimestamp(4, Timestamp.valueOf(theme.getDateDerniereModification()));
            pst.setInt(5, id);
            pst.executeUpdate();
            System.out.println("Thème mis à jour.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du thème : " + e.getMessage());
        }
    }

    public void deleteTheme(int id) {
        String query = "DELETE FROM theme WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Thème supprimé.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du thème : " + e.getMessage());
        }
    }

    public List<Theme> getAllThemes() {
        List<Theme> themes = new ArrayList<>();
        String query = "SELECT * FROM theme";
        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                Theme theme = new Theme();
                theme.setId(rs.getInt("id"));
                theme.setNomTheme(rs.getString("nom_theme"));
                theme.setDescriptionTheme(rs.getString("description_theme"));
                theme.setImageTheme(rs.getString("image_theme"));
                theme.setDateAjoutTheme(rs.getTimestamp("date_ajout_theme").toLocalDateTime());
                theme.setDateDerniereModification(rs.getTimestamp("date_derniere_modfication").toLocalDateTime());
                themes.add(theme);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des thèmes : " + e.getMessage());
        }
        return themes;
    }

    public Theme getThemeById(int id) {
        Theme theme = null;
        String query = "SELECT * FROM theme WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                theme = new Theme();
                theme.setId(rs.getInt("id"));
                theme.setNomTheme(rs.getString("nom_theme"));
                theme.setDescriptionTheme(rs.getString("description_theme"));
                theme.setImageTheme(rs.getString("image_theme"));
                theme.setDateAjoutTheme(rs.getTimestamp("date_ajout_theme").toLocalDateTime());
                theme.setDateDerniereModification(rs.getTimestamp("date_derniere_modfication").toLocalDateTime());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du thème : " + e.getMessage());
        }
        return theme;
    }

}
