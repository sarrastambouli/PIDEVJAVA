package interfaces;
import entities.Question;
import java.util.List;

public interface IQuestionService {

    void addQuestion(Question question);
    void updateQuestion(Question question, int id);
    void deleteQuestion(int id);
    List<Question> getAllQuestions();
    Question getQuestionById(int id);
}
