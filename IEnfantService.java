package GesUsers.interfaces;

import GesUsers.entities.Enfant;
import java.util.List;

public interface IEnfantService {
    void addEnfant(Enfant enfant);
    List<Enfant> getEnfantsByParent(int parentId);
    void updateEnfant(Enfant enfant);
    boolean deleteEnfant(int id);
}
