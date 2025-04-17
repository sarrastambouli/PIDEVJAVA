package services;

import entities.Medecin;
import interfaces.IMedecinService;
import tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedecinService implements IMedecinService {

    @Override
    public void addMedecin(Medecin m) throws SQLException {
        String sql = "INSERT INTO medecin (user_id, type) VALUES (?, ?)";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setInt(1, m.getUserId());
        pst.setString(2, m.getType());
        pst.executeUpdate();
        System.out.println("Médecin ajouté !");
    }

    @Override
    public void updateMedecin(Medecin m, int id) throws SQLException {
        String sql = "UPDATE medecin SET user_id = ?, type = ? WHERE id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setInt(1, m.getUserId());
        pst.setString(2, m.getType());
        pst.setInt(3, id);
        pst.executeUpdate();
        System.out.println("Médecin mis à jour !");
    }

    @Override
    public void deleteMedecin(int id) throws SQLException {
        String sql = "DELETE FROM medecin WHERE id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setInt(1, id);
        pst.executeUpdate();
        System.out.println("Médecin supprimé !");
    }

    @Override
    public List<Medecin> getAllMedecins() throws SQLException {
        List<Medecin> list = new ArrayList<>();
        String sql = "SELECT * FROM medecin";
        Statement st = MyConnection.getInstance().getCnx().createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(resultToMedecin(rs));
        }
        return list;
    }

    @Override
    public Medecin getMedecinById(int id) throws SQLException {
        String sql = "SELECT * FROM medecin WHERE id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return resultToMedecin(rs);
        }
        return null;
    }

    @Override
    public Medecin getMedecinByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM medecin WHERE user_id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setInt(1, userId);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return resultToMedecin(rs);
        }
        return null;
    }

    private Medecin resultToMedecin(ResultSet rs) throws SQLException {
        Medecin m = new Medecin();
        m.setId(rs.getInt("id"));
        m.setUserId(rs.getInt("user_id"));
        m.setType(rs.getString("type"));
        return m;
    }
}
