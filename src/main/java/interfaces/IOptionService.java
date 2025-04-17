package interfaces;
import entities.Option;
import java.util.List;
public interface IOptionService {
    void addOption(Option option);
    void updateOption(Option option, int id);
    void deleteOption(int id);
    List<Option> getAllOptions();
    Option getOptionById(int id);
}
