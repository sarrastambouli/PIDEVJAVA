package services;

import entities.HistoriqueJeu; //taayet lel entité mteek
import interfaces.IHistoriqueJeuService; //
import tools.MyConnection; //appelle la méthode myconnection

import java.sql.*; // appelle les requêtes SQL
import java.time.LocalDateTime; // appelle datetimestamp
import java.util.ArrayList; // jadwel
import java.util.List;

public class HistoriqueJeuService implements IHistoriqueJeuService {

    @Override
    public void addHistoriqueJeu(HistoriqueJeu h) throws SQLException {
        String req = "INSERT INTO historquejeu (idjeu, codeidjeu, nomjeu, themejeu, demojeu, codejeu, dateajoutjeu, datemodifijeu, dateajouthistorique) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())"; // requete pst
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req); // instance de pst
        pst.setInt(1, h.getIdJeu());
        pst.setInt(2, h.getCodeIdJeu());
        pst.setString(3, h.getNomJeu());
        pst.setString(4, h.getThemeJeu());
        pst.setString(5, h.getDemoJeu());
        pst.setString(6, h.getCodeJeu());
        pst.setTimestamp(7, Timestamp.valueOf(h.getDateAjoutJeu()));
        pst.setTimestamp(8, Timestamp.valueOf(h.getDateModifiJeu()));
        pst.executeUpdate(); // execustion de pst
        System.out.println("Historique ajouté !");
    }

    @Override
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

    @Override
    public List<HistoriqueJeu> getAll() {
        List<HistoriqueJeu> list = new ArrayList<>();
        String req = "SELECT * FROM historquejeu";
        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                list.add(resultToHistorique(rs));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    @Override
    public HistoriqueJeu getById(int id) {
        HistoriqueJeu h = null;
        String req = "SELECT * FROM historquejeu WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                h = resultToHistorique(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return h;
    }

    @Override
    public List<HistoriqueJeu> getAllHistoriqueByJeu(int idJeu) {
        List<HistoriqueJeu> list = new ArrayList<>();
        String req = "SELECT * FROM historquejeu WHERE idjeu = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, idJeu);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(resultToHistorique(rs));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    @Override
    public void updateHistoriqueJeu(HistoriqueJeu h, int id) {
        String req = "UPDATE historquejeu SET nomjeu = ?, themejeu = ?, demojeu = ?, codejeu = ?, datemodifijeu = ? WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setString(1, h.getNomJeu());
            pst.setString(2, h.getThemeJeu());
            pst.setString(3, h.getDemoJeu());
            pst.setString(4, h.getCodeJeu());
            pst.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now())); // modif auto
            pst.setInt(6, id);
            pst.executeUpdate();
            System.out.println("Historique mis à jour !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteHistoriqueJeu(int id) {
        String req = "DELETE FROM historquejeu WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Historique supprimé !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private HistoriqueJeu resultToHistorique(ResultSet rs) throws SQLException {
        HistoriqueJeu h = new HistoriqueJeu();
        h.setId(rs.getInt("id"));
        h.setIdJeu(rs.getInt("idjeu"));
        h.setCodeIdJeu(rs.getInt("codeidjeu"));
        h.setNomJeu(rs.getString("nomjeu"));
        h.setThemeJeu(rs.getString("themejeu"));
        h.setDemoJeu(rs.getString("demojeu"));
        h.setCodeJeu(rs.getString("codejeu"));
        h.setDateAjoutJeu(rs.getTimestamp("dateajoutjeu").toLocalDateTime());
        h.setDateModifiJeu(rs.getTimestamp("datemodifijeu").toLocalDateTime());
        h.setDateAjoutHistorique(rs.getTimestamp("dateajouthistorique").toLocalDateTime());
        return h;
    }
}
