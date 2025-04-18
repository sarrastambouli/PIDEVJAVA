package GesUsers.ControllersGesUsers;

import GesUsers.entities.User;
import GesUsers.entities.Enfant;
import GesUsers.services.UserService;
import GesUsers.services.EnfantService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.sql.Date;
import java.util.Optional;

public class AdminBackUsers {

    // Services
    private final UserService userService = new UserService();
    private final EnfantService enfantService = new EnfantService();

    // Table Parents
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> colUserId;
    @FXML private TableColumn<User, String> colNom;
    @FXML private TableColumn<User, String> colPrenom;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colTypeEnfant;
    @FXML private TableColumn<User, String> colTelephone;
    @FXML private TableColumn<User, Date> colNaissance;
    @FXML private TableColumn<User, Integer> colScoreQuiz;

    // Table Enfants
    @FXML private TableView<Enfant> childrenTable;
    @FXML private TableColumn<Enfant, Integer> colEnfantId;
    @FXML private TableColumn<Enfant, String> colUsername;
    @FXML private TableColumn<Enfant, String> colCode;
    @FXML private TableColumn<Enfant, String> colType;
    @FXML private TableColumn<Enfant, Integer> colParentId;
    @FXML private TableColumn<Enfant, Integer> colResQuiz;

    @FXML
    public void initialize() {
        configureTables();
        loadData();
        setupContextMenus();
    }

    private void configureTables() {
        // Configuration table Users
        colUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colTypeEnfant.setCellValueFactory(new PropertyValueFactory<>("typeEnfant"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("numTel"));
        colNaissance.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
        colScoreQuiz.setCellValueFactory(new PropertyValueFactory<>("resultatQuiz"));

        // Configuration table Enfants
        colEnfantId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colParentId.setCellValueFactory(new PropertyValueFactory<>("parentId"));
        colResQuiz.setCellValueFactory(new PropertyValueFactory<>("resQuiz"));
    }

    private void loadData() {
        // Chargement des utilisateurs
        List<User> users = userService.getAllUsers();
        usersTable.setItems(FXCollections.observableArrayList(users));

        // Chargement des enfants
        List<Enfant> enfants = enfantService.getAllEnfants();
        childrenTable.setItems(FXCollections.observableArrayList(enfants));
    }

    private void setupContextMenus() {
        // Menu contextuel pour Users
        ContextMenu userMenu = new ContextMenu();
        MenuItem deleteUserItem = new MenuItem("Supprimer");
        MenuItem editUserItem = new MenuItem("Modifier");
        editUserItem.setOnAction(e -> handleEditUser());
        deleteUserItem.setOnAction(e -> handleUserDelete());
        userMenu.getItems().addAll(deleteUserItem, editUserItem);
        usersTable.setContextMenu(userMenu);

        // Menu contextuel pour Enfants
        ContextMenu enfantMenu = new ContextMenu();
        MenuItem deleteEnfantItem = new MenuItem("Supprimer");
        MenuItem editEnfantItem = new MenuItem("Modifier");
        editEnfantItem.setOnAction(e -> handleEditEnfant());
        deleteEnfantItem.setOnAction(e -> handleEnfantDelete());
        enfantMenu.getItems().addAll(deleteEnfantItem, editEnfantItem);
        childrenTable.setContextMenu(enfantMenu);
    }





    private void handleEditEnfant() {
        Enfant selectedEnfant = childrenTable.getSelectionModel().getSelectedItem();
        if (selectedEnfant != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/InscriEnf.fxml"));
                Parent root = loader.load();

                InscriEnf controller = loader.getController();
                // Retirez setParentController ou adaptez-le
                controller.setEditMode(true);
                controller.initializeFieldsForEdit(selectedEnfant);


                Stage stage = new Stage();
                stage.setTitle("Modifier Enfant");
                stage.setScene(new Scene(root));
                stage.showAndWait(); // Utilisez showAndWait() pour attendre la fermeture

                // Rafraîchir après fermeture

                childrenTable.getItems().clear();
                childrenTable.getItems().addAll(enfantService.getAllEnfants());
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

    private void handleUserDelete() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showDeleteConfirmation(selected.getNom(), () -> {
                if (userService.deleteUser(selected.getId())) {
                    usersTable.getItems().remove(selected);
                    showAlert("Succès", "Utilisateur supprimé avec succès", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Erreur", "Échec de la suppression", Alert.AlertType.ERROR);
                }
            });
        }
    }

    private void handleEnfantDelete() {
        Enfant selected = childrenTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showDeleteConfirmation(selected.getUsername(), () -> {
                if (enfantService.deleteEnfant(selected.getId())) {
                    childrenTable.getItems().remove(selected);
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




    private void handleEditUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openUserEditForm(selected);
        }
    }

    private void openUserEditForm(User user) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParentSignUp.fxml"));
            Parent root = loader.load();

            SignUpController controller = loader.getController();
            controller.setEditMode(true);
            controller.initUserData(user);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier " + ("Parent".equals(user.getRole()) ? "Parent" : "Enfant"));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadData(); // Rafraîchir les données
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire", Alert.AlertType.ERROR);
        }
    }

}