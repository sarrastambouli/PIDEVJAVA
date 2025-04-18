package GesUsers.ControllersGesUsers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;

public class GesUsersController {

    // Méthode pour rediriger vers l'inscription Parent
    @FXML
    private void redirectToParentSignUp(MouseEvent event) {
        loadSignUpForm(event, "Parent");
    }

    // Méthode pour rediriger vers l'inscription Enfant
    @FXML
    private void redirectToChildSignUp(MouseEvent event) {
        loadSignUpForm(event, "Enfant");
    }

    // Méthode générique de chargement
    private void loadSignUpForm(MouseEvent event, String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParentSignUp.fxml")); // Même formulaire pour les deux
            Parent root = loader.load();

            // Passer le rôle au contrôleur du formulaire
            SignUpController controller = loader.getController();
            controller.setUserRole(role);

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}