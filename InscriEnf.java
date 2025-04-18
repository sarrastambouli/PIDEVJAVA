// InscriEnfController.java
package GesUsers.ControllersGesUsers;

import GesUsers.entities.Enfant;
import GesUsers.entities.User;
import GesUsers.services.EnfantService;
import GesUsers.services.SMSService;
import GesUsers.tools.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class InscriEnf {
    @FXML private TextField usernameField;

    @FXML private TextField parentIdField;
    @FXML private TextField codeField;
    @FXML private TextField resquizField;
    @FXML
    private ChoiceBox<String> typeField;
    @FXML private TableView<Enfant> childrenTable;

    private GesEnfParent parentController;



    @FXML
    private Button addChildBtn;

    private boolean isEditMode = false;
    private Enfant enfantToEdit;


    public void setParentController(GesEnfParent parentController) {
        this.parentController = parentController;
    }

    // Dans initialize()
    //typeField.setValue("Normal"); // Valeur par défaut

    private final EnfantService enfService = new EnfantService();

    private int parentId; // À définir lors de la connexion

    @FXML
    public void initializeFields(int parentId) {

        // Récupère l'ID du parent connecté depuis la session
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser != null) {
            parentIdField.setText(String.valueOf(currentUser.getId()));

        } else {
            showAlert("Erreur", "Aucun parent connecté");
            // Redirigez vers une page
        }
        codeField.setText("2"); // Code fixé à 2 comme exemple
        resquizField.setText("0"); // Initialisé à 0
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




/*
    @FXML
    private void addEnfant() {
        Enfant enfant = new Enfant();
        String selectedType = typeField.getValue();
        enfant.setParentId(Integer.parseInt(parentIdField.getText()));
        enfant.setUsername(usernameField.getText());
        enfant.setCode(codeField.getText());
        enfant.setType(selectedType);
        enfant.setResQuiz(Integer.parseInt(resquizField.getText()));

        enfService.addEnfant(enfant);

        // Rafraîchir la table parente
        if (parentController != null) {
            parentController.refreshChildrenTable();
        }

        usernameField.getScene().getWindow().hide();
    }
*/


    @FXML

    private void addEnfant() {
        try {
            // 1. Récupérer le numéro du parent connecté depuis la session
            User parent = UserSession.getInstance().getUser();
            String parentPhone = "+21623772614"; // Testez avec ce numéro en dur d'abord
            //String parentPhone = parent.getNumTel(); // Supposons que User a un champ numTel

            // 2. Générer et envoyer le code
            String verificationCode = SMSService.sendVerificationCode(parentPhone);

            if (verificationCode == null) {
                showAlert("Erreur", "Échec d'envoi du SMS au parent");
                return;
            }

            // 3. Créer l'enfant avec le code envoyé
            Enfant enfant = new Enfant();
            enfant.setParentId(parent.getId());
            enfant.setUsername(usernameField.getText());
            enfant.setCode(verificationCode);
            enfant.setType(typeField.getValue());
            enfant.setResQuiz(0); // Valeur par défaut

            enfService.addEnfant(enfant);

            // 4. Confirmation
            showAlert("Succès", "Code envoyé au parent " );
            closeWindow();

            // Rafraîchir la table parente
            if (parentController != null) {
                parentController.refreshChildrenTable();
            }

            usernameField.getScene().getWindow().hide();

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout: " + e.getMessage());
        }
    }



    @FXML
    private void gestionajoutEnfant() {
        if (isEditMode && enfantToEdit != null) {
            updateEnfant(); // Mode Édition
        } else {
            addEnfant(); // Mode Ajout
        }
    }

    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        addChildBtn.setText(editMode ? "Modifier" : "Ajouter");
    }

    public void initializeFieldsForEdit(Enfant enfant) {
        this.enfantToEdit = enfant;
        usernameField.setText(enfant.getUsername());
        typeField.setValue(enfant.getType());
        parentIdField.setText(String.valueOf(enfant.getParentId()));
        codeField.setText(enfant.getCode());
        resquizField.setText(String.valueOf(enfant.getResQuiz()));
    }

    private void updateEnfant() {
        enfantToEdit.setUsername(usernameField.getText());

        String selectedType = typeField.getValue(); // Au lieu de typeField.getText()
        enfantToEdit.setType(selectedType);
        enfantToEdit.setCode(codeField.getText());
        enfantToEdit.setResQuiz(Integer.parseInt(resquizField.getText()));

        enfService.updateEnfant(enfantToEdit);

        if (parentController != null) {
            parentController.refreshChildrenTable();
        }

        closeWindow();
    }

    private void closeWindow() {
        usernameField.getScene().getWindow().hide();
    }


}