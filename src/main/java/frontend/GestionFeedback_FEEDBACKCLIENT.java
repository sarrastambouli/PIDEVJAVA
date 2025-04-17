package frontend;
import entities.Feedback;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import services.FeedbackService;

import java.sql.Date;
import java.time.LocalDate;
public class GestionFeedback_FEEDBACKCLIENT extends Application{

    private static final Color PRIMARY_COLOR = Color.web("#3f51b5");
    private static final Color SECONDARY_COLOR = Color.web("#f5f5f5");
    private static final Color ACCENT_COLOR = Color.web("#ff5722");

    @Override
    public void start(Stage primaryStage) {
        // Création du layout principal avec effet de flottaison
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(0,0,0,0.5);");

        // Conteneur flottant
        VBox floatingContainer = createFloatingForm();
        root.getChildren().add(floatingContainer);

        // Configuration de la scène
        Scene scene = new Scene(root, 800, 600);
        scene.setFill(Color.TRANSPARENT);

        // Configuration de la fenêtre
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Feedback - EDUCARE");
        primaryStage.show();
    }

    private VBox createFloatingForm() {
        VBox formContainer = new VBox(20);
        formContainer.setAlignment(Pos.TOP_CENTER);
        formContainer.setPadding(new Insets(30));
        formContainer.setMaxWidth(600);
        formContainer.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 15; " +
                "-fx-border-radius: 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 20, 0, 0, 0);");

        // Logo EDUCARE centré
        ImageView logo = new ImageView(new Image("/Images/LOGOEDUCARE.png"));
        logo.setFitHeight(80);
        logo.setFitWidth(80);
        HBox logoContainer = new HBox(logo);
        logoContainer.setAlignment(Pos.CENTER);

        // Titre
        Label title = new Label("VOTRE AVIS NOUS INTÉRESSE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(PRIMARY_COLOR);
        HBox titleContainer = new HBox(title);
        titleContainer.setAlignment(Pos.CENTER);

        // Séparateur décoratif
        Rectangle separator = new Rectangle(150, 3);
        separator.setFill(ACCENT_COLOR);
        HBox separatorContainer = new HBox(separator);
        separatorContainer.setAlignment(Pos.CENTER);

        // Formulaire
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(20);
        form.setPadding(new Insets(20, 40, 20, 40));

        // Titre du feedback (fill in the blank)
        Label titleLabel = new Label("Je souhaite donner mon avis sur :");
        titleLabel.setStyle("-fx-font-weight: bold;");

        ComboBox<String> titleCombo = new ComboBox<>();
        titleCombo.getItems().addAll(
                "L'expérience globale de l'application",
                "La qualité des jeux éducatifs",
                "La facilité d'utilisation",
                "Le design de l'interface",
                "Les fonctionnalités proposées"
        );
        titleCombo.setPromptText("Sélectionnez un sujet");
        titleCombo.setStyle("-fx-background-radius: 5; -fx-border-radius: 5;");

        // Message libre
        Label messageLabel = new Label("Votre message :");
        messageLabel.setStyle("-fx-font-weight: bold;");

        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Décrivez votre expérience en détail...");
        messageArea.setWrapText(true);
        messageArea.setStyle("-fx-background-radius: 5; -fx-border-radius: 5;");

        // Boutons
        Button submitBtn = new Button("Envoyer le feedback");
        submitBtn.setStyle("-fx-background-color: " + toHex(PRIMARY_COLOR) + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 10 20;");

        Button cancelBtn = new Button("Annuler");
        cancelBtn.setStyle("-fx-background-color: " + toHex(SECONDARY_COLOR) + "; " +
                "-fx-text-fill: #333; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 10 20;");

        HBox buttons = new HBox(20, submitBtn, cancelBtn);
        buttons.setAlignment(Pos.CENTER);

        // Ajout des éléments au formulaire
        form.add(titleLabel, 0, 0);
        form.add(titleCombo, 0, 1);
        form.add(messageLabel, 0, 2);
        form.add(messageArea, 0, 3);
        form.add(buttons, 0, 4);

        // Gestion des événements
        submitBtn.setOnAction(e -> {
            if (validateAndSaveFeedback(titleCombo.getValue(), messageArea.getText())) {
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Merci !");
                success.setHeaderText(null);
                success.setContentText("Votre feedback a été enregistré avec succès.");
                success.showAndWait();
                ((Stage) submitBtn.getScene().getWindow()).close();
            }
        });

        cancelBtn.setOnAction(e -> ((Stage) cancelBtn.getScene().getWindow()).close());

        // Assemblage final
        formContainer.getChildren().addAll(logoContainer, titleContainer, separatorContainer, form);

        return formContainer;
    }

    private boolean validateAndSaveFeedback(String title, String message) {
        if (title == null || title.isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner un sujet", Alert.AlertType.ERROR);
            return false;
        }

        if (message == null || message.trim().isEmpty()) {
            showAlert("Erreur", "Veuillez saisir votre message", Alert.AlertType.ERROR);
            return false;
        }

        try {
            Feedback feedback = new Feedback();
            feedback.setTitle(title);
            feedback.setMessage(message);
            feedback.setDate(Date.valueOf(LocalDate.now()));

            // Ici vous devriez définir reclamationParentId et claimId si nécessaire
            feedback.setReclamationParentId(1); // À adapter selon votre logique
            feedback.setLu(Boolean.FALSE); // À adapter selon votre logique


            FeedbackService service = new FeedbackService();
            service.addFeedback(feedback);

            return true;
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public static void main(String[] args) {
        launch(args);
    }

}
