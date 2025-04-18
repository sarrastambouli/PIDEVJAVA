package GesUsers.ControllersGesUsers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import GesUsers.entities.User;
import GesUsers.entities.Enfant;
import GesUsers.services.UserService;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

public class SignUpController {

    private String userRole; // Stocke "Parent" ou "Enfant"

    // Services
    private UserService userService = new UserService();

    // Champs FXML (noms doivent MATCHER exactement les fx:id)
    @FXML private TextField nomuser;
    @FXML private TextField prenomuser;
    @FXML private TextField emailuser;
    @FXML private PasswordField mdpuser;
    @FXML private DatePicker dateuser;
    @FXML private TextField teluser;
    @FXML private Button btninscri;
    @FXML private TextField typeenf;
    @FXML private Label typeEnfantLabel; // Ajoutez ce champ
    @FXML
    private Label mdpLabel;


    private boolean editMode = false;
    private User userToEdit;



    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        if (editMode) {
            btninscri.setText("Modifier");
            mdpuser.setVisible(false);
            mdpLabel.setVisible(false);
            //emailuser.setEditable(false);
        }
    }

    @FXML
    private void gestionuser() {
        try {
            if (editMode) {
                updateUser(); // Mode modification
            } else {
                ajoutuser(); // Mode ajout
            }
            redirectToLoginPage();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur de navigation", "Impossible de charger la page de login: " + e.getMessage());
        }
    }

    private void redirectToLoginPage() throws IOException {
        // 1. Charger le fichier FXML
        Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));

        // 2. Récupérer la scène actuelle (en utilisant un élément de votre scène)
        Stage stage = (Stage) nomuser.getScene().getWindow(); // Remplacez usernameField par un élément existant

        // 3. Changer la scène
        stage.setScene(new Scene(root));

    }




    public void initUserData(User user) {
        this.userToEdit = user;

        // Remplir les champs communs
        nomuser.setText(user.getNom());
        prenomuser.setText(user.getPrenom());
        emailuser.setText(user.getEmail());
        //teluser.setText(user.getNumTel());
        teluser.setText("+21623772614");
        dateuser.setValue(user.getDateNaissance().toLocalDate());

        // Gestion spécifique selon le rôle
        if ("Enfant".equals(user.getRole())) {
            typeenf.setText(user.getTypeEnfant());

        } else {
            // Masquer les champs spécifiques aux enfants pour les parents
            typeenf.setVisible(false);
            typeEnfantLabel.setVisible(false);


            // Ajuster la disposition
            //GridPane.setRowIndex(btninscri, 6); // Décaler le bouton plus haut
        }
    }


    public void setUserRole(String role) {
        this.userRole = role;
        adjustFormForRole();
    }

    private void adjustFormForRole() {
        boolean isEnfant = "Enfant".equals(userRole);

        if (typeEnfantLabel != null) {
            typeEnfantLabel.setVisible(isEnfant);
        }
        if (typeenf != null) {
            typeenf.setVisible(isEnfant);
        }
        if (btninscri != null) {
            btninscri.setText(isEnfant
                    ? "S'inscrire comme Enfant"
                    : "S'inscrire comme Parent");
        }
    }



    /*@FXML
    private void ajoutuser() {
        try {


            User user = new User();
            user.setNom(nomuser.getText());
            user.setPrenom(prenomuser.getText());
            user.setEmail(emailuser.getText());
            user.setMdp(mdpuser.getText());
            user.setNumTel(teluser.getText());
            user.setRole(userRole);

            if (dateuser.getValue() != null) {
                user.setDateNaissance(Date.valueOf(dateuser.getValue()));
            }

            if ("Enfant".equals(userRole) && typeenf != null) {
                user.setTypeEnfant(typeenf.getText());
            }

            userService.addUser(user);
            showAlert("Succès", "Inscription réussie !");
            clearForm();

        } catch (Exception e) {
            showAlert("Erreur", "Échec de l'inscription: " + e.getMessage());
            e.printStackTrace();
        }
    }*/


    @FXML
    private void ajoutuser() {
        try {
            // Validation des champs
            if (!isValidEmail(emailuser.getText())) {
                showErrorAlert("Email invalide", "Veuillez entrer une adresse email valide.");
                return;
            }

            if (!isValidPassword(mdpuser.getText())) {
                showErrorAlert("Mot de passe invalide",
                        "Le mot de passe doit contenir:\n" +
                                "- Au moins 8 caractères\n" +
                                "- 1 majuscule\n" +
                                "- 1 minuscule\n" +
                                "- 1 chiffre\n" +
                                "- 1 caractère spécial (@#$%^&+=!)");
                return;
            }

            if (!isValidTunisianPhone(teluser.getText())) {
                showErrorAlert("Téléphone invalide",
                        "Veuillez entrer un numéro de téléphone tunisien valide (8 chiffres, commençant par 2,4,5,7 ou 9).");
                return;
            }

            // Si toutes les validations passent, créer l'utilisateur
            User user = new User();
            user.setNom(nomuser.getText());
            user.setPrenom(prenomuser.getText());
            user.setEmail(emailuser.getText());
            user.setMdp(mdpuser.getText());
            user.setNumTel(teluser.getText().replaceAll("[^0-9]", "")); // Nettoyer le numéro
            user.setRole(userRole);

            if (dateuser.getValue() != null) {
                user.setDateNaissance(Date.valueOf(dateuser.getValue()));
            }

            if ("Enfant".equals(userRole) && typeenf != null) {
                user.setTypeEnfant(typeenf.getText());
            }

            userService.addUser(user);
            showAlert("Succès", "Inscription réussie !");
            clearForm();

        } catch (Exception e) {
            showAlert("Erreur", "Échec de l'inscription: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        nomuser.clear();
        prenomuser.clear();
        emailuser.clear();
        mdpuser.clear();
        teluser.clear();
        dateuser.setValue(null);
        if (typeenf != null) typeenf.clear();
    }


    /*private void updateUser() {
        try {
            // Mettre à jour les propriétés de l'utilisateur existant
            userToEdit.setNom(nomuser.getText());
            userToEdit.setPrenom(prenomuser.getText());
            userToEdit.setNumTel(teluser.getText());
            userToEdit.setEmail(emailuser.getText());

            if (dateuser.getValue() != null) {
                userToEdit.setDateNaissance(Date.valueOf(dateuser.getValue()));
            }

            if ("Enfant".equals(userRole)) {
                userToEdit.setTypeEnfant(typeenf.getText());
            }

            // Appeler le service de mise à jour
            if (userService.updateUser(userToEdit)) {
                showAlert("Succès", "Utilisateur modifié avec succès");
                closeWindow();
            } else {
                showAlert("Erreur", "Échec de la modification");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Échec de la modification: " + e.getMessage());
            e.printStackTrace();
        }
    }*/



    private void updateUser() {
        try {
            // Validation des champs
            if (!isValidEmail(emailuser.getText())) {
                showErrorAlert("Email invalide", "Veuillez entrer une adresse email valide.");
                return;
            }

            if (!isValidTunisianPhone(teluser.getText())) {
                showErrorAlert("Téléphone invalide",
                        "Veuillez entrer un numéro de téléphone tunisien valide (8 chiffres, commençant par 2,4,5,7 ou 9).");
                return;
            }

            // Mettre à jour les propriétés
            userToEdit.setNom(nomuser.getText());
            userToEdit.setPrenom(prenomuser.getText());
            userToEdit.setNumTel(teluser.getText().replaceAll("[^0-9]", ""));
            userToEdit.setEmail(emailuser.getText());

            if (dateuser.getValue() != null) {
                userToEdit.setDateNaissance(Date.valueOf(dateuser.getValue()));
            }

            if ("Enfant".equals(userRole)) {
                userToEdit.setTypeEnfant(typeenf.getText());
            }

            if (userService.updateUser(userToEdit)) {
                showAlert("Succès", "Utilisateur modifié avec succès");
                closeWindow();
            } else {
                showAlert("Erreur", "Échec de la modification");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Échec de la modification: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void closeWindow() {
        nomuser.getScene().getWindow().hide();
    }


    // Méthodes de validation
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email != null && email.matches(emailRegex);
    }

    private boolean isValidPassword(String password) {
        // Au moins 1 majuscule, 1 minuscule, 1 chiffre, 1 caractère spécial, longueur >= 8
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password != null && password.matches(passwordRegex);
    }

    private boolean isValidTunisianPhone(String phone) {
        // Numéro tunisien commençant par 2, 4, 5, 7, 9 et ayant 8 chiffres
        String phoneRegex = "^[24579][0-9]{7}$";
        // Supprimer les espaces et caractères non numériques
        String cleanedPhone = phone.replaceAll("[^0-9]", "");
        return cleanedPhone.matches(phoneRegex);
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }






}