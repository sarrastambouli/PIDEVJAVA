package services;

import entities.RateMed;
import interfaces.IRateMedService;
import tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RateMedService implements IRateMedService {

    @Override
    public void addRateMed(RateMed r) throws SQLException {
        String sql = "INSERT INTO rate_med (jeu_id, description, nbre_etoiles, medecin_id) VALUES (?, ?, ?, ?)";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setInt(1, r.getJeuId());
        pst.setString(2, r.getDescription()); // Chemin du PDF
        pst.setInt(3, r.getNbreEtoiles());
        pst.setInt(4, r.getMedecinId());
        pst.executeUpdate();
        System.out.println("RateMed ajouté !");
    }
    public List<RateMed> getRateMedByJeuIdAndMedecinId(int jeuId, int medecinId) throws SQLException {
        List<RateMed> list = new ArrayList<>();
        String sql = "SELECT * FROM rate_med WHERE jeu_id = ? AND medecin_id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setInt(1, jeuId);
        pst.setInt(2, medecinId);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            list.add(resultToRateMed(rs));
        }
        return list;
    }
    @Override
    public void updateRateMed(RateMed r, int id) throws SQLException {
        String sql = "UPDATE rate_med SET jeu_id = ?, description = ?, nbre_etoiles = ?, medecin_id = ? WHERE id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setInt(1, r.getJeuId());
        pst.setString(2, r.getDescription());
        pst.setInt(3, r.getNbreEtoiles());
        pst.setInt(4, r.getMedecinId());
        pst.setInt(5, id);
        pst.executeUpdate();
        System.out.println("RateMed modifié !");
    }

    @Override
    public void deleteRateMed(int id) throws SQLException {
        String sql = "DELETE FROM rate_med WHERE id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setInt(1, id);
        pst.executeUpdate();
        System.out.println("RateMed supprimé !");
    }

    @Override
    public List<RateMed> getAllRateMed() throws SQLException {
        List<RateMed> list = new ArrayList<>();
        String sql = "SELECT * FROM rate_med";
        Statement st = MyConnection.getInstance().getCnx().createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(resultToRateMed(rs));
        }
        return list;
    }

    @Override
    public RateMed getRateMedById(int id) throws SQLException {
        String sql = "SELECT * FROM rate_med WHERE id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return resultToRateMed(rs);
        }
        return null;
    }

    @Override
    public List<RateMed> getRateMedByJeuId(int jeuId) throws SQLException {
        List<RateMed> list = new ArrayList<>();
        String sql = "SELECT * FROM rate_med WHERE jeu_id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setInt(1, jeuId);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            list.add(resultToRateMed(rs));
        }
        return list;
    }

    @Override
    public List<RateMed> getRateMedByMedecinId(int medecinId) throws SQLException {
        List<RateMed> list = new ArrayList<>();
        String sql = "SELECT * FROM rate_med WHERE medecin_id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setInt(1, medecinId);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            list.add(resultToRateMed(rs));
        }
        return list;
    }

    private RateMed resultToRateMed(ResultSet rs) throws SQLException {
        RateMed r = new RateMed();
        r.setId(rs.getInt("id"));
        r.setJeuId(rs.getInt("jeu_id"));
        r.setDescription(rs.getString("description"));
        r.setNbreEtoiles(rs.getInt("nbre_etoiles"));
        r.setMedecinId(rs.getInt("medecin_id"));
        return r;
    }
}
