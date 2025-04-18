package GesUsers.ControllersGesUsers;

import GesUsers.entities.Enfant;
import GesUsers.entities.User;
import GesUsers.services.EnfantService;
import GesUsers.services.UserService;
import GesUsers.tools.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML private TabPane userTabPane;
    @FXML private Tab parentTab;
    @FXML private Tab enfantTab;
    @FXML private TextField emailField;
    @FXML private PasswordField passwrdField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink signupLink;

    private UserService userService = new UserService();
    private EnfantService enfantService = new EnfantService();

    @FXML
    private void initialize() {
        // Gestionnaire d'événement pour le bouton de connexion
        loginButton.setOnAction(event -> handleLogin());

        // Gestionnaire pour le lien d'inscription
        signupLink.setOnAction(event -> {
            try {
                loadSignupPage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleLogin() {
        if (userTabPane.getSelectionModel().getSelectedItem() == parentTab) {
            // Authentification Parent/User
            authenticateUser();
        } else {
            // Authentification Enfant
            authenticateEnfant();
        }
    }

    private void authenticateUser() {
        String email = emailField.getText().trim();
        String password = passwrdField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
            return;
        }

        try {
            User user = userService.authenticate(email, password);
            if (user != null) {
                // Démarrer la session
                UserSession.getInstance().startUserSession(user);
                redirectBasedOnRole(user.getRole());
            } else {
                showAlert("Erreur", "Email ou mot de passe incorrect", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void authenticateEnfant() {
        String username = usernameField.getText().trim();
        String code = passwordField.getText().trim();

        if (username.isEmpty() || code.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
            return;
        }

        try {
            Enfant enfant = enfantService.authenticate(username, code);
            if (enfant != null) {
                // Démarrer la session
                UserSession.getInstance().startEnfantSession(enfant);
                redirectToEnfantPage(enfant);
            } else {
                showAlert("Erreur", "Nom d'utilisateur ou code incorrect", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void redirectBasedOnRole(String role) throws IOException {
        String fxmlFile;
        switch (role.toLowerCase()) {
            case "parent":
                fxmlFile = "/GesEnfParent.fxml";
                break;
            case "medecin":
                fxmlFile = "/MedecinDashboard.fxml";
                break;
            case "admin":
                fxmlFile = "/GesUsersAdmin.fxml";
                break;
            default:
                throw new IOException("Rôle non reconnu: " + role);
        }

        loadFXMLFile(fxmlFile);
    }

    private void redirectToEnfantPage(Enfant enfant) throws IOException {
        // Vous pouvez passer les données de l'enfant à la page suivante si nécessaire
        //loadFXMLFile("/EnfantDashboard.fxml");
        loadFXMLFile("/signup.fxml");
    }

    private void loadSignupPage() throws IOException {
        loadFXMLFile("/signup.fxml");
    }

    private void loadFXMLFile(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}