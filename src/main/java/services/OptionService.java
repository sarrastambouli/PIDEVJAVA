package services;

// les bibliothèques :
import entities.Option;
import entities.Question;
import tools.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import interfaces.IOptionService;

public class OptionService implements IOptionService {

    @Override
    public void addOption(Option option) {
        try {
            String requete = "INSERT INTO options (question_id, contenu_option, image1, image2, image3, image4, est_correcte) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, option.getQuestionId());
            pst.setString(2, option.getContenuOption());
            pst.setString(3, option.getImage1());
            pst.setString(4, option.getImage2());
            pst.setString(5, option.getImage3());
            pst.setString(6, option.getImage4());
            pst.setBoolean(7, option.isEstCorrecte());
            pst.executeUpdate();
            System.out.println("Option ajoutée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateOption(Option option, int id) {
        try {
            String requete = "UPDATE options SET question_id = ?, contenu_option = ?, image1 = ?, image2 = ?, image3 = ?, image4 = ?, est_correcte = ? WHERE id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, option.getQuestionId());
            pst.setString(2, option.getContenuOption());
            pst.setString(3, option.getImage1());
            pst.setString(4, option.getImage2());
            pst.setString(5, option.getImage3());
            pst.setString(6, option.getImage4());
            pst.setBoolean(7, option.isEstCorrecte());
            pst.setInt(8, id);
            pst.executeUpdate();
            System.out.println("Option mise à jour");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteOption(int id) {
        try {
            String requete = "DELETE FROM options WHERE id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Option supprimée");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Option> getAllOptions() {
        List<Option> options = new ArrayList<>();
        try {
            String requete = "SELECT * FROM options";
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs = st.executeQuery(requete);
            while (rs.next()) {
                Option option = new Option();
                option.setId(rs.getInt("id"));
                option.setQuestionId(rs.getInt("question_id"));
                option.setContenuOption(rs.getString("contenu_option"));
                option.setImage1(rs.getString("image1"));
                option.setImage2(rs.getString("image2"));
                option.setImage3(rs.getString("image3"));
                option.setImage4(rs.getString("image4"));
                option.setEstCorrecte(rs.getBoolean("est_correcte"));
                options.add(option);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return options;
    }
    public List<Option> getAllOptionsByQuestionId(int questionId) throws SQLException {
        List<Option> options = new ArrayList<>();
        String query = "SELECT * FROM options WHERE question_id = ?";

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = MyConnection.getInstance().getCnx(); // Get a new connection
            pst = conn.prepareStatement(query);
            pst.setInt(1, questionId);
            rs = pst.executeQuery();

            while (rs.next()) {
                Option option = new Option();
                option.setId(rs.getInt("id"));
                option.setQuestionId(rs.getInt("question_id"));
                option.setContenuOption(rs.getString("contenu_option"));
                option.setImage1(rs.getString("image1"));
                option.setImage2(rs.getString("image2"));
                option.setImage3(rs.getString("image3"));
                option.setImage4(rs.getString("image4"));
                option.setEstCorrecte(rs.getBoolean("est_correcte"));
                options.add(option);
            }
        } finally {
            // Close resources in reverse order
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            // Don't close the connection here if you're using a connection pool
            // The pool will handle connection management
        }

        return options;
    }

    public void deleteOptionsByQuestionId(int questionId) throws SQLException {
        String requete = "DELETE FROM options WHERE question_id = ?";
        PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
        pst.setInt(1, questionId);
        pst.executeUpdate();
    }

    @Override
    public Option getOptionById(int id) {
        Option option = null;
        try {
            String requete = "SELECT * FROM options WHERE id = ?";
            PreparedStatement pst = MyConnection.getInstance().getCnx().prepareStatement(requete);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                option = new Option();
                option.setId(rs.getInt("id"));
                option.setQuestionId(rs.getInt("question_id"));
                option.setContenuOption(rs.getString("contenu_option"));
                option.setImage1(rs.getString("image1"));
                option.setImage2(rs.getString("image2"));
                option.setImage3(rs.getString("image3"));
                option.setImage4(rs.getString("image4"));
                option.setEstCorrecte(rs.getBoolean("est_correcte"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return option;
    }
}
