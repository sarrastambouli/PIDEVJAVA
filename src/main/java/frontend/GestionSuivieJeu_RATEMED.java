package frontend;
import entities.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import services.*;
import javafx.util.StringConverter;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import tools.MyConnection;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.layout.StackPane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import javafx.beans.property.SimpleStringProperty;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import javafx.animation.RotateTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import static org.apache.pdfbox.pdmodel.font.PDType1Font.*;

public class GestionSuivieJeu_RATEMED extends Application {

    private boolean isNavBarOpen = true;
    private VBox sideNav;
    private final Color PRIMARY_COLOR = Color.web("#3f51b5");
    private final Color MENUCOLOR = Color.web("#BECCE4");
    private final Color SECONDARY_COLOR = Color.web("#f5f5f5");
    private final Color ACCENT_COLOR = Color.web("#ff5722");
    private final Color HOVER_COLOR = Color.web("#3949ab");
    private final Color SELECTED_COLOR = Color.web("#1a237e");
    private VBox themeListTabContent;
    private int currentMedecinId = 1;
    private Medecin currentMedecin;
    private TableView<HistoriqueJeu> historiqueTable;
    private TableView<RateMed> evaluationsTable;
    private int notificationCount = 3; // Exemple: 3 notifications non lues
    private MediaPlayer notificationPlayer;

    @Override
    public void start(Stage primaryStage) throws SQLException {
        currentMedecin = new Medecin();
        currentMedecin.setId(currentMedecinId);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + toHex(SECONDARY_COLOR) + ";");

        sideNav = createSideNav();
        mainLayout.setLeft(sideNav);

        mainLayout.setCenter(createDashboardContent());
        mainLayout.setTop(createHeader());
        mainLayout.setBottom(createFooter());

        Scene scene = new Scene(mainLayout, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setTitle("Dashboard EDUCARE");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: " + toHex(PRIMARY_COLOR) + ";");
        header.setPrefHeight(60);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 20, 0, 20));

        ImageView logoView = new ImageView(new Image("/Images/LOGOEDUCARE.png"));
        logoView.setFitHeight(120);
        logoView.setFitWidth(120);
        HBox.setMargin(logoView, new Insets(0, 0, 0, -30));

        ImageView menuIcon = new ImageView(new Image("/Images/menu.png"));
        menuIcon.setFitHeight(25);
        menuIcon.setFitWidth(25);
        Button menuBtn = new Button();
        menuBtn.setGraphic(menuIcon);
        styleIconButton(menuBtn);
        menuBtn.setOnAction(e -> toggleNavBar());

        Tooltip menuTooltip = new Tooltip("Menu de navigation");
        Tooltip.install(menuBtn, menuTooltip);

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        StackPane userContainer = new StackPane();
        userContainer.setPadding(new Insets(0, -10, 0, 0));

        // Icône de notification
        StackPane notificationContainer = createNotificationIcon();
        HBox.setMargin(notificationContainer, new Insets(0, 20, 0, 0));

        ImageView userIcon = new ImageView(new Image("/Images/taib.png"));
        userIcon.setFitHeight(80);
        userIcon.setFitWidth(80);
        userIcon.setPreserveRatio(true);

        Circle clip = new Circle(30);
        clip.setCenterX(30);
        clip.setCenterY(40);
        userIcon.setClip(clip);

        Button userBtn = new Button();
        userBtn.setGraphic(userIcon);
        userBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        userBtn.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        userBtn.setOnAction(e -> showUserMenu(userBtn));

        userContainer.getChildren().add(userBtn);

        Tooltip userTooltip = new Tooltip("Medecin principal");
        Tooltip.install(userBtn, userTooltip);

        header.getChildren().addAll(logoView, menuBtn, spacer,notificationContainer, userContainer);
        return header;
    }
    private StackPane createNotificationIcon() {
        StackPane container = new StackPane();

        // Icône de cloche
        ImageView bellIcon = new ImageView(new Image("/Images/bell.png"));
        bellIcon.setFitHeight(25);
        bellIcon.setFitWidth(25);

        // Badge pour le compteur de notifications
        Circle notificationBadge = new Circle(10);
        notificationBadge.setFill(Color.RED);
        notificationBadge.setStroke(Color.WHITE);
        notificationBadge.setStrokeWidth(1);
        notificationBadge.setTranslateX(10);
        notificationBadge.setTranslateY(-10);

        // Texte du compteur
        Label countLabel = new Label(String.valueOf(notificationCount));
        countLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");
        countLabel.setTranslateX(10);
        countLabel.setTranslateY(-10);

        // Bouton cliquable
        Button notificationBtn = new Button();
        notificationBtn.setGraphic(bellIcon);
        notificationBtn.setStyle("-fx-background-color: transparent;");
        notificationBtn.setOnAction(e -> showNotifications());

        // Jouer le son quand on clique
        notificationBtn.setOnMouseClicked(e -> playNotificationSound());

        container.getChildren().addAll(notificationBtn, notificationBadge, countLabel);

        // Animation de secousse quand une nouvelle notification arrive
        RotateTransition shake = new RotateTransition(Duration.millis(100), bellIcon);
        shake.setByAngle(15);
        shake.setCycleCount(4);
        shake.setAutoReverse(true);

        // Simuler une nouvelle notification (pour la démo)
        KeyFrame keyFrame = new KeyFrame(
                Duration.seconds(10),
                e -> {
                    notificationCount++;
                    countLabel.setText(String.valueOf(notificationCount));
                    shake.play();
                    playNotificationSound();
                }
        );

        Timeline newNotification = new Timeline(keyFrame);
        newNotification.setCycleCount(Animation.INDEFINITE);
        newNotification.play();

        return container;
    }
    private void playNotificationSound() {
        try {
            String soundFile = getClass().getResource("/sounds/notification.mp3").toString();
            Media sound = new Media(soundFile);
            notificationPlayer = new MediaPlayer(sound);
            notificationPlayer.play();
        } catch (Exception e) {
            System.err.println("Notification sonore désactivée (erreur JavaFX Media)");
            // e.printStackTrace(); // Optionnel : afficher la stacktrace
        }
    }

    private void showNotifications() {
        // Réinitialiser le compteur
        notificationCount = 0;

        // Créer une popup de notifications
        Stage notificationStage = new Stage();
        notificationStage.initModality(Modality.NONE);
        notificationStage.initStyle(StageStyle.UTILITY);
        notificationStage.setTitle("Notifications");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1;");

        Label title = new Label("Vos notifications");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Liste des notifications (exemple)
        VBox notificationsList = new VBox(5);
        notificationsList.getChildren().addAll(
                createNotificationItem("Nouvelle évaluation à faire", "Il y a 10 min"),
                createNotificationItem("Rapport médical validé", "Hier"),
                createNotificationItem("Mise à jour des jeux disponibles", "Il y a 2 jours")
        );

        root.getChildren().addAll(title, notificationsList);

        Scene scene = new Scene(root, 300, 200);
        notificationStage.setScene(scene);
        notificationStage.show();
    }

    private HBox createNotificationItem(String message, String time) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(5));

        Circle dot = new Circle(4, Color.web("#3f51b5"));

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px;");

        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #757575;");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        item.getChildren().addAll(dot, messageLabel, spacer, timeLabel);
        return item;
    }

    private VBox createSideNav() {
        VBox sideNav = new VBox();
        sideNav.setStyle("-fx-background-color:" + toHex(MENUCOLOR) + ";" + "-fx-border-color: #e0e0e0; -fx-border-width: 0 1 0 0");
        sideNav.setPrefWidth(250);
        sideNav.setPadding(new Insets(10, 0, 10, 0));

        Button bntHome = createNavButton("Acceuil","Images/homejeu.png");
        Button btnEvaluation = createNavButton("Evaluation", "/Images/evaluation.png");
        Button btndemandes = createNavButton("Demandes d'ajout", "/Images/suggestion.png");

        sideNav.getChildren().addAll(bntHome, btnEvaluation, btndemandes);
        return sideNav;
    }

    // Méthode pour valider/rejeter un jeu
    public void traiterValidationJeu(int jeuId, boolean accepter) {
        JeuService jeuService = new JeuService();
        Jeu jeu = jeuService.getJeuById(jeuId);

        if (accepter) {
            // 1. Accepter le jeu
            jeu.setValide(true);
            jeu.setStatut(StatutJeu.VALIDE);
            jeuService.updateJeu(jeu, jeuId);

            // 2. Notifier le créateur (ID=2)
            Notification notif = new Notification();
            notif.setMessage("Votre jeu '" + jeu.getNomJeu() + "' a été accepté");
            notif.setType("VALIDATION_REPONSE");
            notif.setEntiteType("JEU");
            notif.setEntiteId(jeuId);
            notif.setDestinataireId(2); // ID créateur = 2
            notif.setEmetteurId(1);     // ID médecin = 1

            new NotificationService().creerNotification(String.valueOf(notif), jeuId, 2);
        } else {
            // 1. Refuser le jeu (on ne supprime pas, juste on marque)
            jeu.setStatut(StatutJeu.REFUSE);
            jeuService.updateJeu(jeu, jeuId);

            // 2. Notifier le créateur
            Notification notif = new Notification();
            notif.setMessage("Votre jeu '" + jeu.getNomJeu() + "' a été refusé");
            // ... mêmes paramètres que ci-dessus
            new NotificationService().creerNotification(String.valueOf(notif), jeuId, 2);
        }
    }

    private Button createNavButton(String text, String iconPath) {
        ImageView icon = new ImageView(new Image(iconPath));
        icon.setFitHeight(24);
        icon.setFitWidth(24);

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #333; -fx-font-size: 14px;");

        HBox content = new HBox(15, icon, label);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(0, 0, 0, 20));

        StackPane buttonContainer = new StackPane(content);
        buttonContainer.setAlignment(Pos.CENTER_LEFT);

        Button button = new Button();
        button.setGraphic(buttonContainer);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setStyle("-fx-background-color: transparent; -fx-padding: 12px 0;");

        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: " + toHex(HOVER_COLOR) + "; -fx-padding: 12px 0;");
            label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            icon.setFitHeight(26);
            icon.setFitWidth(26);
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: transparent; -fx-padding: 12px 0;");
            label.setStyle("-fx-text-fill: #333; -fx-font-size: 14px;");
            icon.setFitHeight(24);
            icon.setFitWidth(24);
        });

        button.setOnMousePressed(e -> {
            button.setStyle("-fx-background-color: " + toHex(SELECTED_COLOR) + "; -fx-padding: 12px 0;");
            label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        });

        if (text.equals("Acceuil")) {
            button.setOnAction(e -> {
                Stage adminStage = new Stage();
                GestionSuivieJeu_MEDECIN adminApp = new GestionSuivieJeu_MEDECIN();
                try {
                    adminApp.start(adminStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                ((Stage) button.getScene().getWindow()).close();
            });
        }

        if (text.equals("Evaluation")) {
            button.setOnAction(e -> {
                Stage evaluationStage = new Stage();
                GestionSuivieJeu_RATEMED EVALUATIONjeuApp = new GestionSuivieJeu_RATEMED();
                try {
                    EVALUATIONjeuApp.start(evaluationStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                ((Stage) button.getScene().getWindow()).close();
            });
        }
        if (text.equals("Jeux")) {
            button.setOnAction(e -> {
                Stage jeuStage = new Stage();
                GestionJeu_JEU jeuApp = new GestionJeu_JEU();
                try {
                    jeuApp.start(jeuStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                ((Stage) button.getScene().getWindow()).close();
            });
        }

        return button;
    }

    private VBox createDashboardContent() throws SQLException {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Onglet d'évaluation des jeux
        Tab evaluationTab = new Tab("Évaluation des Jeux");
        ScrollPane evaluationScroll = new ScrollPane(createJeuEvaluationTab());
        evaluationScroll.setFitToWidth(true);
        evaluationTab.setContent(evaluationScroll);

        // Onglet des évaluations existantes
        Tab evaluationsTab = new Tab("Mes Évaluations");
        ScrollPane evaluationsScroll = new ScrollPane(createEvaluationsTab());
        evaluationsScroll.setFitToWidth(true);
        evaluationsTab.setContent(evaluationsScroll);

        tabPane.getTabs().addAll(evaluationTab, evaluationsTab);

        VBox container = new VBox(tabPane);
        container.setPadding(new Insets(10));
        return container;
    }

    private VBox createEvaluationsTab() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label("Mes Évaluations");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + toHex(PRIMARY_COLOR) + ";");

        evaluationsTable = new TableView<>();
        setupEvaluationsTable();

        try {
            RateMedService rateService = new RateMedService();
            List<RateMed> evaluations = rateService.getRateMedByMedecinId(currentMedecinId);
            evaluationsTable.setItems(FXCollections.observableArrayList(evaluations));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        container.getChildren().addAll(titleLabel, evaluationsTable);
        return container;
    }

    private void setupEvaluationsTable() {
        evaluationsTable.getColumns().clear();

        // Colonne ID Jeu
        TableColumn<RateMed, Integer> jeuIdCol = new TableColumn<>("ID Jeu");
        jeuIdCol.setCellValueFactory(new PropertyValueFactory<>("jeuId"));

        // Colonne Nom Jeu
        TableColumn<RateMed, String> nomJeuCol = new TableColumn<>("Nom du Jeu");
        nomJeuCol.setCellValueFactory(cell -> {
            JeuService jeuService = new JeuService();
            Jeu jeu = jeuService.getJeuById(cell.getValue().getJeuId());
            return new SimpleStringProperty(jeu != null ? jeu.getNomJeu() : "Inconnu");
        });

        // Colonne Note
        TableColumn<RateMed, Integer> noteCol = new TableColumn<>("Note");
        noteCol.setCellValueFactory(new PropertyValueFactory<>("nbreEtoiles"));
        noteCol.setCellFactory(column -> new TableCell<RateMed, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    StarRating stars = new StarRating(5, 15);
                    stars.setRating(item);
                    stars.setDisable(true); // Empêche la modification
                    setGraphic(stars);
                }
            }
        });

        // Colonne Rapport
        TableColumn<RateMed, Void> rapportCol = new TableColumn<>("Rapport");
        rapportCol.setCellFactory(column -> new TableCell<RateMed, Void>() {
            private final Button viewBtn = new Button("Voir");
            private final Button pdfBtn = new Button("PDF");

            {
                viewBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                viewBtn.setOnAction(e -> {
                    RateMed evaluation = getTableView().getItems().get(getIndex());
                    afficherRapport(evaluation);
                });

                pdfBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                pdfBtn.setOnAction(e -> {
                    RateMed evaluation = getTableView().getItems().get(getIndex());
                    genererPDF(evaluation);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, viewBtn, pdfBtn);
                    setGraphic(buttons);
                }
            }
        });

        evaluationsTable.getColumns().addAll(jeuIdCol, nomJeuCol, noteCol, rapportCol);
        evaluationsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupJeuEvaluationTable(TableView<Jeu> table) {
        table.getColumns().clear();

        // Colonne ID
        TableColumn<Jeu, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id")); // Doit correspondre à getid()
        // Colonne Nom
        TableColumn<Jeu, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nomJeu"));
        nomCol.setPrefWidth(150);

        // Colonne Description
        TableColumn<Jeu, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(250);

        // Colonne Démo
        TableColumn<Jeu, String> demoCol = new TableColumn<>("Démo");
        demoCol.setCellValueFactory(new PropertyValueFactory<>("demoJeu"));
        demoCol.setCellFactory(column -> new TableCell<>() {
            private final Button viewBtn = new Button("Voir");

            {
                viewBtn.setOnAction(e -> {
                    Jeu jeu = getTableView().getItems().get(getIndex());
                    ouvrirDemoJeu(jeu.getDemoJeu());
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(viewBtn);
                }
            }
        });

        // Colonne Évaluation (étoiles)
        TableColumn<Jeu, Void> rateCol = new TableColumn<>("Note");
        rateCol.setCellFactory(column -> new TableCell<>() {
            private final StarRating starRating = new StarRating(5, 20);
            private final Button saveBtn = new Button("Enregistrer");
            private final HBox container = new HBox(10, starRating, saveBtn);

            {
                saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                saveBtn.setOnAction(e -> {
                    Jeu ratemed = getTableView().getItems().get(getIndex());
                    enregistrerEvaluation(ratemed, starRating.getRating());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Jeu ratemed = getTableView().getItems().get(getIndex());
                    chargerEvaluationExistante(ratemed.getId(), starRating);
                    setGraphic(container);
                }
            }
        });

        // Colonne Rapport
        TableColumn<Jeu, Void> rapportCol = new TableColumn<>("Rapport");
        rapportCol.setCellFactory(column -> new TableCell<>() {
            private final Button rapportBtn = new Button("Écrire");

            {
                rapportBtn.setStyle("-fx-background-color: " + toHex(ACCENT_COLOR) + "; -fx-text-fill: white;");
                rapportBtn.setOnAction(e -> {
                    Jeu jeu = getTableView().getItems().get(getIndex());
                    ouvrirEditeurRapport(jeu);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(rapportBtn);
                }
            }
        });
        table.getColumns().addAll(idCol, nomCol, descCol, demoCol, rateCol, rapportCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void ouvrirDemoJeu(String demoPath) {
        try {
            if (demoPath != null && !demoPath.isEmpty()) {
                File file = new File(demoPath);
                if (file.exists()) {
                    Desktop.getDesktop().open(file);
                } else {
                    showAlert("Erreur", "Fichier démo introuvable", Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir la démo: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void chargerEvaluationExistante(int jeuId, StarRating starRating) {
        try {
            RateMedService rateService = new RateMedService();
            List<RateMed> evaluations = rateService.getRateMedByJeuIdAndMedecinId(jeuId, currentMedecinId);

            if (!evaluations.isEmpty()) {
                starRating.setRating(evaluations.get(0).getNbreEtoiles());
            } else {
                starRating.setRating(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            starRating.setRating(0);
        }
    }

    private void enregistrerEvaluation(Jeu jeu, int note) {
        try {
            // Log de débogage crucial
            System.out.println("Tentative d'enregistrement pour :");
            System.out.println("Jeu ID: " + jeu.getId());
            System.out.println("Nom Jeu: " + jeu.getNomJeu());
            System.out.println("Médecin ID: " + currentMedecinId);
            System.out.println("Note: " + note);

            RateMedService rateService = new RateMedService();
            RateMed evaluation = new RateMed();
            evaluation.setJeuId(jeu.getId()); // Ici l'ID doit être correct
            evaluation.setMedecinId(currentMedecinId);
            evaluation.setNbreEtoiles(note);

            // Log avant enregistrement
            System.out.println("Évaluation à enregistrer : " + evaluation);

            List<RateMed> existing = rateService.getRateMedByJeuIdAndMedecinId(jeu.getId(), currentMedecinId);

            if (existing.isEmpty()) {
                rateService.addRateMed(evaluation);
                System.out.println("Nouvelle évaluation créée");
            } else {
                evaluation.setId(existing.get(0).getId());
                rateService.updateRateMed(evaluation, evaluation.getId());
                System.out.println("Évaluation existante mise à jour");
            }

            // Vérification après enregistrement
            List<RateMed> verif = rateService.getRateMedByJeuIdAndMedecinId(jeu.getId(), currentMedecinId);
            System.out.println("Vérification post-enregistrement : " + verif);

            refreshEvaluationsTable();
            showAlert("Succès", "Évaluation enregistrée pour jeu ID: " + jeu.getId(), Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement: " + e.getMessage());
            showAlert("Erreur", "Échec de l'enregistrement: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private VBox createJeuEvaluationTab() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label("Évaluation des Jeux Éducatifs");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + toHex(PRIMARY_COLOR) + ";");

        TableView<Jeu> jeuTable = new TableView<>();
        setupJeuEvaluationTable(jeuTable);

        JeuService jeuService = new JeuService();
        List<Jeu> jeux = jeuService.getAllJeux();
        jeuTable.setItems(FXCollections.observableArrayList(jeux));

        container.getChildren().addAll(titleLabel, jeuTable);
        return container;
    }

    private void ouvrirEditeurRapport(Jeu jeu) {
        Stage rapportStage = new Stage();
        rapportStage.setTitle("Rapport médical pour " + jeu.getNomJeu());

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // En-tête
        VBox header = new VBox(10);
        Label titleLabel = new Label("Rapport médical - " + jeu.getNomJeu());
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + toHex(PRIMARY_COLOR) + ";");

        Label subtitleLabel = new Label("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        subtitleLabel.setStyle("-fx-font-size: 14px;");

        header.getChildren().addAll(titleLabel, subtitleLabel);
        root.setTop(header);

        // Éditeur de texte
        TextArea editor = new TextArea();
        editor.setWrapText(true);
        editor.setStyle("-fx-font-size: 14px;");

        // Charger un rapport existant s'il y en a un
        try {
            RateMedService rateService = new RateMedService();
            List<RateMed> evaluations = rateService.getRateMedByJeuIdAndMedecinId(jeu.getId(), currentMedecin.getId());
            if (!evaluations.isEmpty()) {
                editor.setText(evaluations.get(0).getDescription());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        root.setCenter(editor);

        // Pied de page avec boutons
        HBox footer = new HBox(20);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(20, 0, 0, 0));

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 16;");
        saveBtn.setOnAction(e -> {
            enregistrerRapport(jeu, editor.getText());
            rapportStage.close();
        });

        Button pdfBtn = new Button("Générer PDF");
        pdfBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 16;");
        pdfBtn.setOnAction(e -> genererPDF(jeu, editor.getText()));

        Button cancelBtn = new Button("Annuler");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 16;");
        cancelBtn.setOnAction(e -> rapportStage.close());

        footer.getChildren().addAll(cancelBtn, pdfBtn, saveBtn);
        root.setBottom(footer);

        Scene scene = new Scene(root, 800, 600);
        rapportStage.setScene(scene);
        rapportStage.showAndWait();
    }

    private void enregistrerRapport(Jeu jeu, String rapport) {
        try {
            RateMedService rateService = new RateMedService();
            List<RateMed> evaluations = rateService.getRateMedByJeuIdAndMedecinId(jeu.getId(), currentMedecin.getId());

            if (evaluations.isEmpty()) {
                RateMed evaluation = new RateMed();
                evaluation.setJeuId(jeu.getId());
                evaluation.setMedecinId(currentMedecin.getId());
                evaluation.setDescription(rapport);
                rateService.addRateMed(evaluation);
            } else {
                RateMed evaluation = evaluations.get(0);
                evaluation.setDescription(rapport);
                rateService.updateRateMed(evaluation, evaluation.getId());
            }

            showAlert("Succès", "Rapport enregistré", Alert.AlertType.INFORMATION);
            refreshEvaluationsTable();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'enregistrement: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshEvaluationsTable() {
        try {
            // Sauvegarder la sélection actuelle
            RateMed selected = evaluationsTable.getSelectionModel().getSelectedItem();

            RateMedService rateService = new RateMedService();
            List<RateMed> evaluations = rateService.getRateMedByMedecinId(currentMedecinId);
            evaluationsTable.setItems(FXCollections.observableArrayList(evaluations));

            // Restaurer la sélection si possible
            if (selected != null) {
                evaluationsTable.getSelectionModel().select(selected);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de rafraîchir", Alert.AlertType.ERROR);
        }
    }

    private void afficherRapport(RateMed rapport) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Rapport médical");
        dialog.setHeaderText("Rapport pour le jeu ID: " + rapport.getJeuId());

        ButtonType closeButton = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        TextArea textArea = new TextArea(rapport.getDescription());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(600, 400);

        dialog.getDialogPane().setContent(textArea);
        dialog.showAndWait();
    }

    private void genererPDF(Jeu jeu, String rapport) {
        try {
            // Création du document PDF
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Titre
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Rapport médical - " + jeu.getNomJeu());
            contentStream.endText();

            // Date
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);            contentStream.newLineAtOffset(50, 720);
            contentStream.showText("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            contentStream.endText();

            // Contenu
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);            contentStream.newLineAtOffset(50, 690);

            // Découpage du texte en lignes
            String[] lines = rapport.split("\n");
            float yPosition = 690;

            for (String line : lines) {
                // Vérifier si on doit changer de page
                if (yPosition < 50) {
                    contentStream.endText(); // Fermer le texte actuel
                    contentStream.close();   // Fermer le flux actuel

                    // Créer une nouvelle page
                    page = new PDPage();
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    yPosition = 750;

                    // Recommencer le texte
                    contentStream.beginText();
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);                    contentStream.newLineAtOffset(50, yPosition);
                }

                contentStream.showText(line);
                contentStream.newLineAtOffset(0, -15); // Descendre d'une ligne
                yPosition -= 15;
            }

            // Fermer le dernier contexte de texte
            contentStream.endText();
            contentStream.close();

            // Sauvegarde du fichier
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le rapport PDF");
            fileChooser.setInitialFileName("rapport_" + jeu.getNomJeu() + "_" + System.currentTimeMillis() + ".pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                document.save(file);
                showAlert("Succès", "PDF généré avec succès: " + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
            }
            document.close();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la génération du PDF: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private void genererPDF(RateMed rapport) {
        JeuService jeuService = new JeuService();
        Jeu jeu = jeuService.getJeuById(rapport.getJeuId());
        genererPDF(jeu, rapport.getDescription());
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showUserMenu(Button userBtn) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;");

        MenuItem adminAccount = new MenuItem("Bienvenue KADDOUR Taib !");
        adminAccount.setStyle("-fx-font-size: 14px; -fx-padding: 8px 20px;");

        MenuItem manageAccount = new MenuItem("Gérer le compte");
        manageAccount.setStyle("-fx-font-size: 14px; -fx-padding: 8px 20px;");
        manageAccount.setGraphic(new ImageView(new Image("/Images/user.png", 16, 16, true, true)));
        manageAccount.setOnAction(e -> manageAccount());

        MenuItem logout = new MenuItem("Déconnexion");
        logout.setStyle("-fx-font-size: 14px; -fx-padding: 8px 20px;");
        logout.setGraphic(new ImageView(new Image("/Images/logout.png", 16, 16, true, true)));
        logout.setOnAction(e -> logout());

        SeparatorMenuItem separator = new SeparatorMenuItem();
        separator.setStyle("-fx-padding: 5 0 5 0;");

        contextMenu.getItems().addAll(adminAccount, manageAccount, separator, logout);
        contextMenu.show(userBtn, javafx.geometry.Side.BOTTOM, 0, 0);
    }
    private void loadHistoriqueData() throws SQLException {
        HistoriqueJeuService HistoriqueJeuService = new HistoriqueJeuService();
        List<HistoriqueJeu> HistoriqueJeux = HistoriqueJeuService.getAll();

        ObservableList<HistoriqueJeu> observableList = FXCollections.observableArrayList(HistoriqueJeux);
        historiqueTable.setItems(observableList); // Utilise la variable de classe déjà initialisée
    }

    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setStyle("-fx-background-color: " + toHex(PRIMARY_COLOR) + ";");
        footer.setPadding(new Insets(15));
        footer.setAlignment(Pos.CENTER);

        VBox footerContent = new VBox(10);
        footerContent.setAlignment(Pos.CENTER);

        // Copyright
        Label copyright = new Label("© 2024-2025 EDUCARE - Tous droits réservés");
        copyright.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        // Membres de l'équipe avec icône LinkedIn
        HBox teamBox = new HBox(10);
        teamBox.setAlignment(Pos.CENTER);

        ImageView linkedinIcon = new ImageView(new Image("/Images/linkedin.png", 20, 20, true, true));
        Label teamLabel = new Label("Équipe de développement:");
        teamLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        teamBox.getChildren().addAll(linkedinIcon, teamLabel);

        // Grille pour les membres avec photos rondes
        GridPane membersGrid = new GridPane();
        membersGrid.setHgap(20);
        membersGrid.setVgap(15);
        membersGrid.setAlignment(Pos.CENTER);

        // Ajout des membres avec leurs photos rondes et liens LinkedIn
        addMemberWithRoundPhoto(membersGrid, 0, 0, "STAMBOULI Sarra", "/Images/sarra.png",
                "https://www.linkedin.com/in/sarah-stambouli-6a1069244/", 50);
        addMemberWithRoundPhoto(membersGrid, 1, 0, "KADDOUR Taib", "/Images/taib.png",
                "https://www.linkedin.com/in/taib-kaddour-8a3b63253/", 50);
        addMemberWithRoundPhoto(membersGrid, 2, 0, "ANGAR Nada", "/Images/nada.png",
                "https://www.linkedin.com/in/nada-angar-0422ab260/", 50);
        addMemberWithRoundPhoto(membersGrid, 0, 1, "SLAMA Sadok", "/Images/sadok.png",
                "https://www.linkedin.com/in/sadok-slama-5999672a0/", 50);
        addMemberWithRoundPhoto(membersGrid, 1, 1, "GATRI Chourouk", "/Images/chourouk.png",
                "https://www.linkedin.com/in/chourouk-gatri-73b0b5228/", 50);
        addMemberWithRoundPhoto(membersGrid, 2, 1, "KHIRALLAH Yassmine", "/Images/yassmine.png",
                "https://www.linkedin.com/in/yasmine-khirallah-707139299/", 50);

        footerContent.getChildren().addAll(copyright, teamBox, membersGrid);
        footer.getChildren().add(footerContent);
        return footer;
    }

    private void addMemberWithRoundPhoto(GridPane grid, int col, int row, String name, String photoPath, String url, double size) {
        // Création de l'image ronde sans bordure
        ImageView photo = new ImageView(new Image(photoPath));
        photo.setFitWidth(size);
        photo.setFitHeight(size);
        photo.setPreserveRatio(true);

        Circle clip = new Circle(40);
        clip.setCenterX(20);
        clip.setCenterY(30);
        photo.setClip(clip);

        // Nom avec lien LinkedIn
        Hyperlink memberLink = new Hyperlink(name);
        memberLink.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-border-width: 0;");
        memberLink.setOnAction(e -> getHostServices().showDocument(url));

        // Effet hover sur le lien
        memberLink.setOnMouseEntered(e -> {
            memberLink.setStyle("-fx-text-fill: " + toHex(ACCENT_COLOR) + "; -fx-font-size: 12px; -fx-underline: true;");
        });
        memberLink.setOnMouseExited(e -> {
            memberLink.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-underline: false;");
        });

        VBox memberBox = new VBox(5, photo, memberLink);
        memberBox.setAlignment(Pos.CENTER);
        memberBox.setStyle("-fx-padding: 5px;");

        grid.add(memberBox, col, row);
    }

    private void toggleNavBar() {
        isNavBarOpen = !isNavBarOpen;

        if (isNavBarOpen) {
            sideNav.setPrefWidth(250);
            // Afficher le texte
            for (Node node : sideNav.getChildren()) {
                if (node instanceof Button) {
                    Button btn = (Button) node;
                    HBox content = (HBox) ((StackPane) btn.getGraphic()).getChildren().get(0);
                    Label label = (Label) content.getChildren().get(1);
                    label.setVisible(true);
                }
            }
        } else {
            sideNav.setPrefWidth(70);
            // Masquer le texte
            for (Node node : sideNav.getChildren()) {
                if (node instanceof Button) {
                    Button btn = (Button) node;
                    HBox content = (HBox) ((StackPane) btn.getGraphic()).getChildren().get(0);
                    Label label = (Label) content.getChildren().get(1);
                    label.setVisible(false);
                }
            }
        }
    }

    private void styleIconButton(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-padding: 5px;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-padding: 5px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-padding: 5px;"));
    }

    private void manageAccount() {
        System.out.println("Gestion du compte...");
    }

    private void logout() {
        System.out.println("Déconnexion...");
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
