package services;

import entities.Feedback;
import interfaces.IFeedbackService;
import tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackService implements IFeedbackService {

    @Override
    public void addFeedback(Feedback feedback) {
        String req = "INSERT INTO feedback (reclamation_parent_id, title, message, date,lu) VALUES (?, ?, ?, ?,?)";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, feedback.getReclamationParentId());
            pst.setString(2, feedback.getTitle());
            pst.setString(3, feedback.getMessage());
            pst.setDate(4, feedback.getDate());
            pst.setBoolean(5, feedback.getLu());
            pst.executeUpdate();
            System.out.println("Feedback ajouté !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du feedback : " + e.getMessage());
        }
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        List<Feedback> list = new ArrayList<>();
        String req = "SELECT * FROM feedback";
        try {
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Feedback f = new Feedback(
                        rs.getInt("id"),
                        rs.getInt("reclamation_parent_id"),
                        rs.getString("title"),
                        rs.getString("message"),
                        rs.getDate("date"),
                        rs.getBoolean("lu")

                );
                list.add(f);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des feedbacks : " + e.getMessage());
        }
        return list;
    }

    @Override
    public Feedback getFeedbackById(int id) {
        String req = "SELECT * FROM feedback WHERE id = ?";
        Feedback f = null;
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                f = new Feedback(
                        rs.getInt("id"),
                        rs.getInt("reclamation_parent_id"),
                        rs.getString("title"),
                        rs.getString("message"),
                        rs.getDate("date"),
                        rs.getBoolean("lu")

                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du feedback : " + e.getMessage());
        }
        return f;
    }

    @Override
    public void supprimerFeedback(int id) {
        String req = "DELETE FROM feedback WHERE id = ?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Feedback supprimé !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du feedback : " + e.getMessage());
        }
    }

    @Override
    public void modifierFeedback(Feedback feedback) {
        String req = "UPDATE feedback SET reclamation_parent_id=?, title=?, message=?, date=?, lu=? WHERE id=?";
        try {
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(req);
            pst.setInt(1, feedback.getReclamationParentId());
            pst.setString(2, feedback.getTitle());
            pst.setString(3, feedback.getMessage());
            pst.setDate(4, feedback.getDate());
            pst.setInt(5, feedback.getId());
            pst.setBoolean(6, feedback.getLu());

            pst.executeUpdate();
            System.out.println("Feedback modifié !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du feedback : " + e.getMessage());
        }
    }
}
