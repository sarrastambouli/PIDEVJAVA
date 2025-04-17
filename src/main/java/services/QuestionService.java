package services;


import entities.Question;
import tools.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import interfaces.IQuestionService;


public class QuestionService implements IQuestionService{
    @Override
    public void addQuestion(Question question) {
        try {
            String requete = "INSERT INTO question (quiz_id, contenu_question, nombre_optios, image) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, question.getQuizId());
            pst.setString(2, question.getContenuQuestion());
            pst.setInt(3, question.getNombreOptions());
            pst.setString(4, question.getImage());
            pst.executeUpdate();
            System.out.println("Question ajoutée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public int getLastInsertedId() throws SQLException {
        int id = -1;
        String requete = "SELECT LAST_INSERT_ID() as last_id";
        Statement st = MyConnection.getInstance().getCnx().createStatement();
        ResultSet rs = st.executeQuery(requete);
        if (rs.next()) {
            id = rs.getInt("last_id");
        }
        return id;
    }
    public List<Question> getAllQuestionsByQuizId(int quizId) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String requete = "SELECT * FROM question WHERE quiz_id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
        pst.setInt(1, quizId);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            Question question = new Question();
            question.setId(rs.getInt("id"));
            question.setQuizId(rs.getInt("quiz_id"));
            question.setContenuQuestion(rs.getString("contenu_question"));
            question.setNombreOptions(rs.getInt("nombre_optios"));
            question.setImage(rs.getString("image"));
            questions.add(question);
        }
        return questions;
    }

    @Override
    public void updateQuestion(Question question, int id) {
        try {
            String requete = "UPDATE question SET quiz_id = ?, contenu_question = ?, nombre_optios = ?, image = ? WHERE id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, question.getQuizId());
            pst.setString(2, question.getContenuQuestion());
            pst.setInt(3, question.getNombreOptions());
            pst.setString(4, question.getImage());
            pst.setInt(5, id);
            pst.executeUpdate();
            System.out.println("Question mise à jour");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteQuestion(int id) {
        try {
            String requete = "DELETE FROM question WHERE id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Question supprimée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();
        try {
            String requete = "SELECT * FROM question";
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(requete);
            while (rs.next()) {
                Question question = new Question();
                question.setId(rs.getInt("id"));
                question.setQuizId(rs.getInt("quiz_id"));
                question.setContenuQuestion(rs.getString("contenu_question"));
                question.setNombreOptions(rs.getInt("nombre_optios"));
                question.setImage(rs.getString("image"));
                questions.add(question);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return questions;
    }

    @Override
    public Question getQuestionById(int id) {
        Question question = null;
        try {
            String requete = "SELECT * FROM question WHERE id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                question = new Question();
                question.setId(rs.getInt("id"));
                question.setQuizId(rs.getInt("quiz_id"));
                question.setContenuQuestion(rs.getString("contenu_question"));
                question.setNombreOptions(rs.getInt("nombre_optios"));
                question.setImage(rs.getString("image"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return question;
    }

}
