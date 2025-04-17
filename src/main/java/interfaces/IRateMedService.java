package interfaces;

import entities.RateMed;
import java.sql.SQLException;
import java.util.List;

public interface IRateMedService {
    void addRateMed(RateMed r) throws SQLException;
    void updateRateMed(RateMed r, int id) throws SQLException;
    void deleteRateMed(int id) throws SQLException;
    List<RateMed> getAllRateMed() throws SQLException;
    RateMed getRateMedById(int id) throws SQLException;
    List<RateMed> getRateMedByJeuId(int jeuId) throws SQLException;
    List<RateMed> getRateMedByMedecinId(int medecinId) throws SQLException;
}
