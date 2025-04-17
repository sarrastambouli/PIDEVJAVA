package entities;
import java.util.List;

public class Quiz {
    private int id;
    private String nomQuiz;
    private String descriptionQuiz;
    private int nombreQuestions;
    private List<Question> questions;

    public Quiz( String nomQuiz, String descriptionQuiz, int nombreQuestions) {
        this.nomQuiz = nomQuiz;
        this.descriptionQuiz = descriptionQuiz;
        this.nombreQuestions = nombreQuestions;
    }

    public Quiz() {

    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNomQuiz() { return nomQuiz; }
    public void setNomQuiz(String nomQuiz) { this.nomQuiz = nomQuiz; }

    public String getDescriptionQuiz() { return descriptionQuiz; }
    public void setDescriptionQuiz(String descriptionQuiz) { this.descriptionQuiz = descriptionQuiz; }

    public int getNombreQuestions() { return nombreQuestions; }
    public void setNombreQuestions(int nombreQuestions) { this.nombreQuestions = nombreQuestions; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
}
