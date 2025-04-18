package GesUsers.ControllersGesUsers;

import GesUsers.entities.Enfant;
import GesUsers.entities.User;
import GesUsers.services.EnfantService;
import GesUsers.tools.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class GesEnfParent {


    // Table Enfants
    @FXML private TableView<Enfant> childrenTable;
    @FXML private TableColumn<Enfant, Integer> colEnfantId;
    @FXML private TableColumn<Enfant, String> colUsername;
    @FXML private TableColumn<Enfant, String> colCode;
    @FXML private TableColumn<Enfant, String> colType;
    @FXML private TableColumn<Enfant, Integer> colParentId;
    @FXML private TableColumn<Enfant, Integer> colResQuiz;

    @FXML private TextField usernameField;
    @FXML private TextField typeField;
    @FXML private TextField parentIdField;
    @FXML private TextField codeField;
    @FXML private TextField resquizField;

    private final EnfantService enfService = new EnfantService();
    private int parentId; // À définir lors de la connexion





    @FXML
    public void initialize() {
        // Récupère l'ID du parent connecté depuis la session
        User currentUser = UserSession.getInstance().getUser();
        if (currentUser != null) {
            parentId = currentUser.getId();
            configureTables();
            loadData();
            setupContextMenus();
        } else {
            showAlert("Erreur", "Aucun utilisateur connecté", Alert.AlertType.ERROR);
            // Redirigez vers la page de login si nécessaire
        }
    }



    private void configureTables() {


        // Configuration table Enfants
        colEnfantId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colParentId.setCellValueFactory(new PropertyValueFactory<>("parentId"));
        colResQuiz.setCellValueFactory(new PropertyValueFactory<>("resQuiz"));
    }

    private void loadData() {
        // Récupère uniquement les enfants du parent connecté
        List<Enfant> enfants = enfService.getEnfantsByParent(parentId);
        childrenTable.setItems(FXCollections.observableArrayList(enfants));
    }

    private void setupContextMenus() {


        // Menu contextuel pour Enfants
        ContextMenu enfantMenu = new ContextMenu();
        MenuItem deleteEnfantItem = new MenuItem("Supprimer");
        MenuItem editEnfantItem = new MenuItem("Modifier");
        editEnfantItem.setOnAction(e -> handleEditEnfant());
        deleteEnfantItem.setOnAction(e -> handleEnfantDelete());
        enfantMenu.getItems().addAll(deleteEnfantItem, editEnfantItem);
        childrenTable.setContextMenu(enfantMenu);
    }


    @FXML
    private void handleEditEnfant() {
        Enfant selectedEnfant = childrenTable.getSelectionModel().getSelectedItem();
        if (selectedEnfant != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/InscriEnf.fxml"));
                Parent root = loader.load();

                InscriEnf controller = loader.getController();
                controller.setParentController(this);
                controller.setEditMode(true); // Mode édition
                controller.initializeFieldsForEdit(selectedEnfant); // Pré-remplir les champs

                Stage stage = new Stage();
                stage.setTitle("Modifier Enfant");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un enfant à modifier.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void handleEnfantDelete() {
        Enfant selected = childrenTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showDeleteConfirmation(selected.getUsername(), () -> {
                System.out.println(selected.getId());
                if (enfService.deleteEnfant(selected.getId())) {
                    childrenTable.getItems().remove(selected);
                    childrenTable.refresh();                   // Rafraîchit l'affichage
                    loadData();                               // Recharge depuis la base (optionnel)
                    showAlert("Succès", "Enfant supprimé avec succès", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Erreur", "Échec de la suppression", Alert.AlertType.ERROR);
                }
            });
        }
    }

    private void showDeleteConfirmation(String name, Runnable onConfirm) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer " + name + " ?");
        alert.setContentText("Cette action est irréversible");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            onConfirm.run();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // Cette méthode sera appelée avant d'afficher le formulaire
  /*  public void initializeFields(int parentId) {
        parentIdField.setText(String.valueOf(parentId));
        codeField.setText("2"); // Code fixé à 2 comme exemple
        resquizField.setText("0"); // Initialisé à 0
    }



    @FXML
    private void handleAddChildButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InscriEnf.fxml"));
            Parent root = loader.load();

            GesEnfParent controller = loader.getController();
            controller.initializeFields(parentId); // currentParentId est l'ID du parent connecté

            Stage stage = new Stage();
            stage.setTitle("Ajouter un enfant");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addEnfant() {
        Enfant enfant = new Enfant();
        enfant.setParentId(Integer.parseInt(parentIdField.getText()));
        enfant.setUsername(usernameField.getText());
        enfant.setCode(codeField.getText());
        enfant.setType(typeField.getText());
        enfant.setResQuiz(Integer.parseInt(resquizField.getText()));

        enfService.addEnfant(enfant);

        // Fermer la fenêtre ou rafraîchir le tableau
        usernameField.getScene().getWindow().hide();
    }
*/


@FXML
    private void handleAddChildButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InscriEnf.fxml"));
            Parent root = loader.load();

            InscriEnf controller = loader.getController();
            controller.initializeFields(parentId); // currentParentId est l'ID du parent connecté
            controller.setParentController(this); // Passer la référence


            Stage stage = new Stage();
            stage.setTitle("Ajouter un enfant");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour rafraîchir la table
    public void refreshChildrenTable() {
        // Implémentez la logique pour rafraîchir votre TableView
        // Par exemple:
        childrenTable.getItems().clear();
        childrenTable.getItems().addAll(enfService.getEnfantsByParent(parentId));
    }


}
