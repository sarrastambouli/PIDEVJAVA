package interfaces;

import entities.Notification;
import java.sql.SQLException;
import java.util.List;

public interface INotificationService {
    void creerNotification(String message, int jeuId, int medecinId);
    List<Notification> getNotificationsPourMedecin(int medecinId);
    void marquerCommeLue(int notificationId);
    void updateNotification(Notification notification, int id) throws SQLException;
    void deleteNotification(int id) throws SQLException;
    List<Notification> getAllNotifications() throws SQLException;
    Notification getNotificationById(int id) throws SQLException;
    List<Notification> getNotificationsByDestinataireId(int destinataireId) throws SQLException;
    List<Notification> getUnreadNotificationsByDestinataireId(int destinataireId) throws SQLException;
}
