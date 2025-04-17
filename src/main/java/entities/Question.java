package entities;
import java.util.List;

public class Question {
    private int id;
    private int quizId;
    private String contenuQuestion;
    private int nombreOptions;
    private String image;
    private List<Option> options;

    public Question(int id, int quizId, String contenuQuestion, int nombreOptions, String image) {
        this.id = id;
        this.quizId = quizId;
        this.contenuQuestion = contenuQuestion;
        this.nombreOptions = nombreOptions;
        this.image = image;
    }

    public Question() {

    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuizId() { return quizId; }
    public void setQuizId(int quizId) { this.quizId = quizId; }

    public String getContenuQuestion() { return contenuQuestion; }
    public void setContenuQuestion(String contenuQuestion) { this.contenuQuestion = contenuQuestion; }

    public int getNombreOptions() { return nombreOptions; }
    public void setNombreOptions(int nombreOptions) { this.nombreOptions = nombreOptions; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public List<Option> getOptions() { return options; }
    public void setOptions(List<Option> options) { this.options = options; }
}
