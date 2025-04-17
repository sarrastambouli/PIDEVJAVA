package interfaces;
import entities.Quiz;
import java.util.List;
public interface IQuizService {
    void addQuiz(Quiz quiz);
    void updateQuiz(Quiz quiz, int id);
    void deleteQuiz(int id);
    List<Quiz> getAllQuizzes();
    Quiz getQuizById(int id);
}
