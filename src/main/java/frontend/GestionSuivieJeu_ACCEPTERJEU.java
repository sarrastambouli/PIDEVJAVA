package frontend;

import entities.*;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import services.JeuService;
import services.NotificationService;
import tools.MyConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class GestionSuivieJeu_ACCEPTERJEU extends Application {

    private final int MEDECIN_ID = 1; // ID du médecin
    private TableView<Notification> notificationTable;
    private TableView<Jeu> jeuxEnAttenteTable;

    @Override
    public void start(Stage primaryStage) throws SQLException {
        // Layout principal
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");

        // En-tête
        mainLayout.setTop(createHeader());

        // Contenu central
        TabPane tabPane = new TabPane();

        // Onglet Notifications
        Tab notificationsTab = new Tab("Notifications");
        notificationsTab.setContent(createNotificationsTab());
        notificationsTab.setClosable(false);

        // Onglet Jeux en attente
        Tab jeuxTab = new Tab("Jeux en attente");
        jeuxTab.setContent(createJeuxEnAttenteTab());
        jeuxTab.setClosable(false);

        tabPane.getTabs().addAll(notificationsTab, jeuxTab);
        mainLayout.setCenter(tabPane);

        // Pied de page
        mainLayout.setBottom(createFooter());

        // Scene
        Scene scene = new Scene(mainLayout, 1000, 700);
        primaryStage.setTitle("Validation des Jeux - EDUCARE");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Charger les données initiales
        refreshNotifications();
        refreshJeuxEnAttente();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #3f51b5;");
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER_LEFT);

        // Logo
        ImageView logoView = new ImageView(new Image("/Images/LOGOEDUCARE.png"));
        logoView.setFitHeight(50);
        logoView.setFitWidth(50);

        // Titre
        Label titleLabel = new Label("Validation des Jeux Éducatifs");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        // Espaceur
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Photo de profil
        ImageView userIcon = new ImageView(new Image("/Images/taib.png"));
        userIcon.setFitHeight(40);
        userIcon.setFitWidth(40);
        Circle clip = new Circle(20);
        clip.setCenterX(20);
        clip.setCenterY(20);
        userIcon.setClip(clip);

        header.getChildren().addAll(logoView, titleLabel, spacer, userIcon);
        return header;
    }

    private VBox createNotificationsTab() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));

        // Titre
        Label titleLabel = new Label("Notifications de Validation");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #3f51b5;");

        // TableView pour les notifications
        notificationTable = new TableView<>();
        setupNotificationTable();

        // Bouton de rafraîchissement
        Button refreshBtn = new Button("Rafraîchir");
        refreshBtn.setStyle("-fx-background-color: #3f51b5; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> {
            refreshNotifications();
        });

        container.getChildren().addAll(titleLabel, notificationTable, refreshBtn);
        return container;
    }

    private void setupNotificationTable() {
        notificationTable.getColumns().clear();

        // Colonne ID
        TableColumn<Notification, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Colonne Message
        TableColumn<Notification, String> messageCol = new TableColumn<>("Message");
        messageCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        messageCol.setPrefWidth(300);

        // Colonne Date
        TableColumn<Notification, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cell -> {
            LocalDateTime date = LocalDateTime.parse(cell.getValue().getDateCreation().toLocaleString());
            return new SimpleStringProperty(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        });

        // Colonne Statut
        TableColumn<Notification, String> statutCol = new TableColumn<>("Statut");
        statutCol.setCellValueFactory(cell -> {
            boolean lue = cell.getValue().isLue();
            return new SimpleStringProperty(lue ? "Lue" : "Non lue");
        });

        // Colonne Actions
        TableColumn<Notification, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("Voir");
            private final Button markAsReadBtn = new Button("Marquer comme lue");

            {
                viewBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                viewBtn.setOnAction(event -> {
                    Notification notification = getTableView().getItems().get(getIndex());
                    voirNotification(notification);
                });

                markAsReadBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                markAsReadBtn.setOnAction(event -> {
                    Notification notification = getTableView().getItems().get(getIndex());
                    try {
                        marquerCommeLue(notification);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, viewBtn, markAsReadBtn);
                    setGraphic(buttons);
                }
            }
        });

        notificationTable.getColumns().addAll(idCol, messageCol, dateCol, statutCol, actionsCol);
    }

    private VBox createJeuxEnAttenteTab() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));

        // Titre
        Label titleLabel = new Label("Jeux en Attente de Validation");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #3f51b5;");

        // TableView pour les jeux en attente
        jeuxEnAttenteTable = new TableView<>();
        setupJeuxEnAttenteTable();

        // Bouton de rafraîchissement
        Button refreshBtn = new Button("Rafraîchir");
        refreshBtn.setStyle("-fx-background-color: #3f51b5; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> refreshJeuxEnAttente());

        container.getChildren().addAll(titleLabel, jeuxEnAttenteTable, refreshBtn);
        return container;
    }

    private void setupJeuxEnAttenteTable() {
        jeuxEnAttenteTable.getColumns().clear();

        // Colonne ID
        TableColumn<Jeu, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Colonne Nom
        TableColumn<Jeu, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nomJeu"));
        nomCol.setPrefWidth(200);

        // Colonne Description
        TableColumn<Jeu, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(300);

        // Colonne Date
        TableColumn<Jeu, String> dateCol = new TableColumn<>("Date Création");
        dateCol.setCellValueFactory(cell -> {
            LocalDateTime date = cell.getValue().getDateCreation();
            return new SimpleStringProperty(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        });

        // Colonne Actions
        TableColumn<Jeu, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button acceptBtn = new Button("Accepter");
            private final Button rejectBtn = new Button("Refuser");
            private final Button viewBtn = new Button("Détails");

            {
                acceptBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                acceptBtn.setOnAction(event -> {
                    Jeu jeu = getTableView().getItems().get(getIndex());
                    try {
                        accepterJeu(jeu);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                rejectBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                rejectBtn.setOnAction(event -> {
                    Jeu jeu = getTableView().getItems().get(getIndex());
                    try {
                        refuserJeu(jeu);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                viewBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                viewBtn.setOnAction(event -> {
                    Jeu jeu = getTableView().getItems().get(getIndex());
                    afficherDetailsJeu(jeu);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, acceptBtn, rejectBtn, viewBtn);
                    setGraphic(buttons);
                }
            }
        });

        jeuxEnAttenteTable.getColumns().addAll(idCol, nomCol, descCol, dateCol, actionsCol);
    }

    private void refreshNotifications() {
        try {
            NotificationService notificationService = new NotificationService();
            List<Notification> notifications = notificationService.getUnreadNotificationsByDestinataireId(MEDECIN_ID);

            System.out.println("Nombre de notifications trouvées: " + notifications.size()); // Debug
            notifications.forEach(n -> System.out.println("Notification: " + n.getMessage())); // Debug

            notificationTable.setItems(FXCollections.observableArrayList(notifications));
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des notifications:");
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les notifications", Alert.AlertType.ERROR);
        }
    }
    private void testDatabaseConnection() {
        try {
            System.out.println("Test de connexion à la base de données...");
            Connection conn = MyConnection.getInstance().getCnx();
            System.out.println("Connexion réussie: " + conn);

            // Test requête simple
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM notification");
            if (rs.next()) {
                System.out.println("Nombre total de notifications en BD: " + rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Échec de connexion à la BD:");
            e.printStackTrace();
        }
    }

    private void checkExistingNotifications() {
        try {
            System.out.println("Vérification des notifications pour médecin ID=1...");
            NotificationService service = new NotificationService();
            List<Notification> notifs = service.getUnreadNotificationsByDestinataireId(1);

            System.out.println("Notifications trouvées: " + notifs.size());
            notifs.forEach(n -> System.out.println(
                    "ID: " + n.getId() +
                            " | Message: " + n.getMessage() +
                            " | Date: " + n.getDateCreation()
            ));
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification:");
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void refreshJeuxEnAttente() {
        JeuService jeuService = new JeuService();
        List<Jeu> jeux = jeuService.getAllJeux().stream()
                .filter(jeu -> jeu.getStatut() == StatutJeu.EN_ATTENTE)
                .toList();
        jeuxEnAttenteTable.setItems(FXCollections.observableArrayList(jeux));
    }

    private void voirNotification(Notification notification) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de la notification");
        alert.setHeaderText("Notification #" + notification.getId());
        alert.setContentText("Message: " + notification.getMessage() + "\n" +
                "Date: " + notification.getDateCreation() + "\n" +
                "Type: " + notification.getType());
        alert.showAndWait();
    }

    private void marquerCommeLue(Notification notification) throws SQLException {
        NotificationService notificationService = new NotificationService();
        notificationService.marquerCommeLue(notification.getId());
        refreshNotifications();
    }

    private void accepterJeu(Jeu jeu) throws SQLException {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Accepter le jeu");
        confirmation.setContentText("Êtes-vous sûr de vouloir accepter ce jeu ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Mettre à jour le statut du jeu
            jeu.setStatut(StatutJeu.VALIDE);
            jeu.setValide(true);

            JeuService jeuService = new JeuService();
            jeuService.updateJeu(jeu, jeu.getId());

            // Envoyer une notification au créateur (ID=2)
            NotificationService notificationService = new NotificationService();
            Notification notification = new Notification();
            notification.setMessage("Votre jeu '" + jeu.getNomJeu() + "' a été accepté");
            notification.setType("VALIDATION_REPONSE");
            notification.setEntiteType("JEU");
            notification.setEntiteId(jeu.getId());
            notification.setDestinataireId(2); // ID créateur
            notification.setEmetteurId(MEDECIN_ID); // ID médecin

            notificationService.creerNotification(notification.getMessage(), jeu.getId(), 2);

            // Rafraîchir les tables
            refreshJeuxEnAttente();
            refreshNotifications();

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Succès");
            success.setHeaderText(null);
            success.setContentText("Le jeu a été accepté avec succès !");
            success.showAndWait();
        }
    }

    private void refuserJeu(Jeu jeu) throws SQLException {
        // Créer une boîte de dialogue pour la raison du refus
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Refuser le jeu");
        dialog.setHeaderText("Veuillez indiquer la raison du refus");
        dialog.setContentText("Raison:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().isEmpty()) {
            // Mettre à jour le statut du jeu
            jeu.setStatut(StatutJeu.REFUSE);
            jeu.setValide(false);

            JeuService jeuService = new JeuService();
            jeuService.updateJeu(jeu, jeu.getId());

            // Envoyer une notification au créateur (ID=2)
            NotificationService notificationService = new NotificationService();
            Notification notification = new Notification();
            notification.setMessage("Votre jeu '" + jeu.getNomJeu() + "' a été refusé. Raison: " + result.get());
            notification.setType("VALIDATION_REPONSE");
            notification.setEntiteType("JEU");
            notification.setEntiteId(jeu.getId());
            notification.setDestinataireId(2); // ID créateur
            notification.setEmetteurId(MEDECIN_ID); // ID médecin

            notificationService.creerNotification(notification.getMessage(), jeu.getId(), 2);

            // Rafraîchir les tables
            refreshJeuxEnAttente();
            refreshNotifications();

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Succès");
            success.setHeaderText(null);
            success.setContentText("Le jeu a été refusé avec succès !");
            success.showAndWait();
        }
    }

    private void afficherDetailsJeu(Jeu jeu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails du jeu");
        alert.setHeaderText("Détails du jeu: " + jeu.getNomJeu());

        String content = "ID: " + jeu.getId() + "\n" +
                "Nom: " + jeu.getNomJeu() + "\n" +
                "Description: " + jeu.getDescription() + "\n" +
                "Date création: " + jeu.getDateCreation() + "\n" +
                "Niveau: " + jeu.getNiveau() + "\n" +
                "Type enfant: " + jeu.getTypeEnf() + "\n" +
                "Statut: " + jeu.getStatut().getLibelle();

        alert.setContentText(content);
        alert.showAndWait();
    }

    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setStyle("-fx-background-color: #3f51b5;");
        footer.setPadding(new Insets(10));
        footer.setAlignment(Pos.CENTER);

        Label copyright = new Label("© 2024 EDUCARE - Tous droits réservés");
        copyright.setStyle("-fx-text-fill: white;");

        footer.getChildren().add(copyright);
        return footer;
    }

    public static void main(String[] args) {
        launch(args);
    }
}