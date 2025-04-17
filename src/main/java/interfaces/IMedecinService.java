package interfaces;

import entities.Medecin;
import java.sql.SQLException;
import java.util.List;

public interface IMedecinService {
    void addMedecin(Medecin m) throws SQLException;
    void updateMedecin(Medecin m, int id) throws SQLException;
    void deleteMedecin(int id) throws SQLException;
    List<Medecin> getAllMedecins() throws SQLException;
    Medecin getMedecinById(int id) throws SQLException;
    Medecin getMedecinByUserId(int userId) throws SQLException;
}
