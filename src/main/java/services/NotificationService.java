package services;

import entities.Notification;
import interfaces.INotificationService;
import tools.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationService implements INotificationService {

    @Override
    public void creerNotification(String message, int jeuId, int medecinId) {
        try {
            // Préparer la requête d'insertion
            String sql = "INSERT INTO notification (message, type, entite_type, entite_id, destinataire_id) " +
                    "VALUES (?, 'AUTRE', 'JEU', ?, ?)";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
            pst.setString(1, message); // Message de la notification
            pst.setInt(2, jeuId); // ID du jeu concerné
            pst.setInt(3, medecinId); // ID du médecin destinataire

            // Exécuter la requête
            pst.executeUpdate();
            System.out.println("Notification créée avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la notification : " + e.getMessage());
        }
    }

    // Méthode pour récupérer les notifications non lues pour un médecin
    public List<Notification> getNotificationsPourMedecin(int medecinId) {
        List<Notification> notifications = new ArrayList<>();
        try {
            // Préparer la requête de récupération des notifications non lues pour un médecin
            String sql = "SELECT * FROM notification WHERE destinataire_id = ? AND lue = FALSE";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
            pst.setInt(1, medecinId);

            // Exécuter la requête et récupérer les résultats
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Notification notification = new Notification();
                notification.setId(rs.getInt("id"));
                notification.setMessage(rs.getString("message"));
                notification.setDateCreation(rs.getTimestamp("date_creation"));
                notification.setLue(rs.getBoolean("lue"));
                notification.setType(rs.getString("type"));
                notification.setEntiteType(rs.getString("entite_type"));
                notification.setEntiteId(rs.getInt("entite_id"));
                notification.setDestinataireId(rs.getInt("destinataire_id"));
                notification.setEmetteurId(rs.getInt("emetteur_id"));
                notifications.add(notification);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des notifications : " + e.getMessage());
        }
        return notifications;
    }

    // Méthode pour marquer une notification comme lue
    public void marquerCommeLue(int notificationId) {
        try {
            // Préparer la requête de mise à jour
            String sql = "UPDATE notification SET lue = TRUE WHERE id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
            pst.setInt(1, notificationId);

            // Exécuter la requête de mise à jour
            pst.executeUpdate();
            System.out.println("Notification marquée comme lue avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la notification : " + e.getMessage());
        }
    }

    @Override
    public void updateNotification(Notification notification, int id) throws SQLException {
        String sql = "UPDATE notification SET message = ?, date_creation = ?, lue = ?, type = ?, entite_type = ?, entite_id = ?, " +
                "destinataire_id = ?, emetteur_id = ? WHERE id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setString(1, notification.getMessage());
        pst.setTimestamp(2, new Timestamp(notification.getDateCreation().getTime()));
        pst.setBoolean(3, notification.isLue());
        pst.setString(4, notification.getType());
        pst.setString(5, notification.getEntiteType());
        pst.setInt(6, notification.getEntiteId());
        pst.setInt(7, notification.getDestinataireId());
        if (notification.getEmetteurId() != null) {
            pst.setInt(8, notification.getEmetteurId());
        } else {
            pst.setNull(8, Types.INTEGER);
        }
        pst.setInt(9, id);
        pst.executeUpdate();
        System.out.println("Notification modifiée !");
    }

    @Override
    public void deleteNotification(int id) throws SQLException {
        String sql = "DELETE FROM notification WHERE id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setInt(1, id);
        pst.executeUpdate();
        System.out.println("Notification supprimée !");
    }

    @Override
    public List<Notification> getAllNotifications() throws SQLException {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notification";
        Statement st = MyConnection.getInstance().getCnx().createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            list.add(resultToNotification(rs));
        }
        return list;
    }

    @Override
    public Notification getNotificationById(int id) throws SQLException {
        String sql = "SELECT * FROM notification WHERE id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return resultToNotification(rs);
        }
        return null;
    }

    @Override
    public List<Notification> getNotificationsByDestinataireId(int destinataireId) throws SQLException {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE destinataire_id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setInt(1, destinataireId);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            list.add(resultToNotification(rs));
        }
        return list;
    }

    @Override
    public List<Notification> getUnreadNotificationsByDestinataireId(int destinataireId) throws SQLException {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE destinataire_id = ? AND lue = FALSE";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(sql);
        pst.setInt(1, destinataireId);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            list.add(resultToNotification(rs));
        }
        return list;
    }

    private Notification resultToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getInt("id"));
        notification.setMessage(rs.getString("message"));
        notification.setDateCreation(rs.getTimestamp("date_creation"));
        notification.setLue(rs.getBoolean("lue"));
        notification.setType(rs.getString("type"));
        notification.setEntiteType(rs.getString("entite_type"));
        notification.setEntiteId(rs.getInt("entite_id"));
        notification.setDestinataireId(rs.getInt("destinataire_id"));
        notification.setEmetteurId(rs.getInt("emetteur_id"));
        return notification;
    }
}
