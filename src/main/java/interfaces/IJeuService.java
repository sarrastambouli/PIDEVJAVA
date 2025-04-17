package interfaces;
import entities.Jeu;

import java.sql.SQLException;
import java.util.List;
public interface IJeuService {
    void addJeu(Jeu jeu) throws SQLException;
    void updateJeu(Jeu jeu, int id);
    void deleteJeu(int id);
    Jeu getJeuById(int id);
    List<Jeu> getAllJeux();
    List<Jeu> getAllJeuxByThemeId(int themeId);
}
