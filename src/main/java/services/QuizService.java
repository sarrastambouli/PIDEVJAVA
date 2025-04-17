package services;


import entities.Quiz;
import tools.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import interfaces.IQuizService;

public class QuizService implements IQuizService{

    @Override
    public void addQuiz(Quiz quiz) {
        // Vérification si le quiz existe déjà par son nom
        try {
            String checkQuery = "SELECT COUNT(*) FROM quiz WHERE nom_quiz = ?";
            PreparedStatement checkStmt = MyConnection.getInstance().getCnx().prepareStatement(checkQuery);
            checkStmt.setString(1, quiz.getNomQuiz());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    System.out.println("Un quiz avec ce nom existe déjà. L'ajout est annulé.");
                    return;  // Ne pas ajouter si un quiz avec le même nom existe déjà
                }
            }

            // Ajout du quiz si aucun quiz avec ce nom n'existe
            String query = "INSERT INTO quiz (nom_quiz, description_quiz, nimbre_questions) VALUES (?, ?, ?)";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(query);
            pst.setString(1, quiz.getNomQuiz());
            pst.setString(2, quiz.getDescriptionQuiz());
            pst.setInt(3, quiz.getNombreQuestions());
            pst.executeUpdate();
            System.out.println("Quiz ajouté avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du quiz : " + e.getMessage());
        }
    }

    @Override
    public void updateQuiz(Quiz quiz, int id) {
        try {
            String requete = "UPDATE quiz SET nom_quiz = ?, description_quiz = ?, nimbre_questions = ? WHERE id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setString(1, quiz.getNomQuiz());
            pst.setString(2, quiz.getDescriptionQuiz());
            pst.setInt(3, quiz.getNombreQuestions());
            pst.setInt(4, id);
            pst.executeUpdate();
            System.out.println("Quiz mis à jour");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteQuiz(int id) {
        try {
            String requete = "DELETE FROM quiz WHERE id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Quiz supprimé");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        try {
            String requete = "SELECT * FROM quiz";
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(requete);
            while (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setId(rs.getInt("id"));
                quiz.setNomQuiz(rs.getString("nom_quiz"));
                quiz.setDescriptionQuiz(rs.getString("description_quiz"));
                quiz.setNombreQuestions(rs.getInt("nimbre_questions"));
                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return quizzes;
    }

    @Override
    public Quiz getQuizById(int id) {
        Quiz quiz = null;
        try {
            String requete = "SELECT * FROM quiz WHERE id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                quiz = new Quiz();
                quiz.setId(rs.getInt("id"));
                quiz.setNomQuiz(rs.getString("nom_quiz"));
                quiz.setDescriptionQuiz(rs.getString("description_quiz"));
                quiz.setNombreQuestions(rs.getInt("nimbre_questions"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return quiz;
    }
}
