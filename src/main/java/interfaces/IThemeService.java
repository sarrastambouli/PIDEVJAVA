package interfaces;
import entities.Theme;
import java.util.List;
public interface IThemeService {
    void updateTheme(Theme theme, int id);
    void deleteTheme(int id);
    List<Theme> getAllThemes();
    Theme getThemeById(int id);
}
