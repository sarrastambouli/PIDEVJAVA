package frontend;

import entities.Option;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import services.OptionService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;

public class GestionJeu_OPTION extends Application{
    private final int questionId;
    private final Color PRIMARY_COLOR = Color.web("#3f51b5");

    public GestionJeu_OPTION(int questionId) {
        this.questionId = questionId;
    }

    @Override
    public void start(Stage primaryStage) {
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label("Gestion des Options - Question #" + questionId);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Conteneur pour les options
        VBox optionsContainer = new VBox(15);

        // Ajouter 4 options (peut être adapté)
        for (int i = 0; i < 4; i++) {
            optionsContainer.getChildren().add(createOptionForm(i + 1));
        }

        // Bouton d'enregistrement
        Button saveButton = new Button("Enregistrer toutes les options");
        saveButton.setStyle("-fx-background-color: " + toHex(PRIMARY_COLOR) + "; -fx-text-fill: white;");
        saveButton.setOnAction(e -> saveAllOptions(optionsContainer));

        mainContainer.getChildren().addAll(titleLabel, optionsContainer, saveButton);

        Scene scene = new Scene(mainContainer, 800, 600);
        primaryStage.setTitle("Gestion des Options");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createOptionForm(int optionNumber) {
        VBox optionForm = new VBox(10);
        optionForm.setPadding(new Insets(15));
        optionForm.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-width: 1;");

        Label optionTitle = new Label("Option " + optionNumber);
        optionTitle.setStyle("-fx-font-weight: bold;");

        // Contenu texte
        TextField contentField = new TextField();
        contentField.setPromptText("Contenu textuel de l'option");

        // Images (jusqu'à 4)
        HBox imageFields = new HBox(10);
        for (int i = 1; i <= 4; i++) {
            VBox imageBox = new VBox(5);
            Label imgLabel = new Label("Image " + i + ":");
            TextField imgPath = new TextField();
            imgPath.setPromptText("Optionnel");
            Button browseBtn = new Button("Parcourir");
            browseBtn.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
                );
                File file = fileChooser.showOpenDialog(null);
                if (file != null) {
                    imgPath.setText(file.getAbsolutePath());
                }
            });
            imageBox.getChildren().addAll(imgLabel, imgPath, browseBtn);
            imageFields.getChildren().add(imageBox);
        }

        // Case à cocher "Correcte"
        CheckBox isCorrect = new CheckBox("Cette option est correcte");

        optionForm.getChildren().addAll(optionTitle, contentField, new Label("Images:"), imageFields, isCorrect);
        return optionForm;
    }

    private void saveAllOptions(VBox optionsContainer) {
        OptionService optionService = new OptionService();

        for (Node node : optionsContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox optionForm = (VBox) node;

                Option option = new Option();
                option.setQuestionId(questionId);

                // Récupérer les valeurs du formulaire
                TextField contentField = (TextField) optionForm.getChildren().get(1);
                option.setContenuOption(contentField.getText());

                // Récupérer les images
                HBox imageFields = (HBox) optionForm.getChildren().get(3);
                for (int i = 0; i < 4; i++) {
                    VBox imageBox = (VBox) imageFields.getChildren().get(i);
                    TextField imgPath = (TextField) imageBox.getChildren().get(1);
                    switch (i) {
                        case 0: option.setImage1(imgPath.getText()); break;
                        case 1: option.setImage2(imgPath.getText()); break;
                        case 2: option.setImage3(imgPath.getText()); break;
                        case 3: option.setImage4(imgPath.getText()); break;
                    }
                }

                CheckBox isCorrect = (CheckBox) optionForm.getChildren().get(4);
                option.setEstCorrecte(isCorrect.isSelected());

                // Enregistrer l'option
                optionService.addOption(option);
            }
        }

        showAlert("Succès", "Toutes les options ont été enregistrées!", Alert.AlertType.INFORMATION);
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
}
