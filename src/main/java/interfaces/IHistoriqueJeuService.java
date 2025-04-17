package interfaces; // des methodes statiques naaytoulhom b @override f services

import entities.HistoriqueJeu;
import java.sql.SQLException;
import java.util.List;

public interface IHistoriqueJeuService {
    void addHistoriqueJeu(HistoriqueJeu h) throws SQLException;
    List<HistoriqueJeu> getAll();
    HistoriqueJeu getById(int id);
    List<HistoriqueJeu> getAllHistoriqueByJeu(int idJeu);
    void updateHistoriqueJeu(HistoriqueJeu h, int id);
    void deleteHistoriqueJeu(int id);
    int getLastInsertedId() throws SQLException;
}
