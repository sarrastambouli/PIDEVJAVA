package frontend;

import entities.Question;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import services.QuestionService;
import tools.MyConnection;
import entities.Theme;
import services.ThemeService;
import entities.Quiz;
import services.QuizService;
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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.io.File;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.animation.RotateTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

public class GestionJeu_THEME extends Application {

    private boolean isNavBarOpen = true;
    private VBox sideNav;
    private final Color PRIMARY_COLOR = Color.web("#3f51b5");
    private final Color MENUCOLOR = Color.web("#BECCE4");
    private final Color SECONDARY_COLOR = Color.web("#f5f5f5");
    private final Color ACCENT_COLOR = Color.web("#ff5722");
    private final Color HOVER_COLOR = Color.web("#3949ab");
    private final Color SELECTED_COLOR = Color.web("#1a237e");
    private VBox themeListTabContent; // Pour garder une référence à l'onglet Liste
    private int currentthemeId; // Ajoutez ce champ à votre classe
    private TableView<Theme> themeTable; // Ajoutez cette ligne
    private int notificationCount = 3; // Exemple: 3 notifications non lues
    private MediaPlayer notificationPlayer;

    @Override
    public void start(Stage primaryStage) throws SQLException {
        // Création du layout principal
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + toHex(SECONDARY_COLOR) + ";");

        // Création de la barre de navigation
        sideNav = createSideNav();
        mainLayout.setLeft(sideNav);

        // Création du contenu central
        mainLayout.setCenter(createDashboardContent());

        // En-tête
        mainLayout.setTop(createHeader());

        // Pied de page
        mainLayout.setBottom(createFooter());

        // Scène
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

        // Logo
        ImageView logoView = new ImageView(new Image("/Images/LOGOEDUCARE.png"));
        logoView.setFitHeight(120);
        logoView.setFitWidth(120);
        HBox.setMargin(logoView, new Insets(0, 0, 0, -30));

        // Menu hamburger avec tooltip
        ImageView menuIcon = new ImageView(new Image("/Images/menu.png"));
        menuIcon.setFitHeight(25);
        menuIcon.setFitWidth(25);
        Button menuBtn = new Button();
        menuBtn.setGraphic(menuIcon);
        styleIconButton(menuBtn);
        menuBtn.setOnAction(e -> toggleNavBar());

        Tooltip menuTooltip = new Tooltip("Menu de navigation");
        Tooltip.install(menuBtn, menuTooltip);

        // Espaceur
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Icône utilisateur ronde et cliquable
        StackPane userContainer = new StackPane();
        userContainer.setPadding(new Insets(0, -10, 0, 0)); // Marge à droite

        // Icône de notification
        StackPane notificationContainer = createNotificationIcon();
        HBox.setMargin(notificationContainer, new Insets(0, 20, 0, 0));

        ImageView userIcon = new ImageView(new Image("/Images/sarra.png"));
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

        // Conteneur pour éviter le clipping
        userContainer.getChildren().add(userBtn);

        Tooltip userTooltip = new Tooltip("Admin - Gestion des jeux");
        Tooltip.install(userBtn, userTooltip);

        header.getChildren().addAll(logoView, menuBtn, spacer, notificationContainer,userContainer);
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
            System.out.println("Erreur de lecture du son: " + e.getMessage());
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
        Button btnTheme = createNavButton("Thème", "/Images/theme.png");

        // Création du bouton Quiz avec ComboBox
        HBox quizContainer = createQuizComboBoxContainer();

        Button btnJeux = createNavButton("Jeux", "/Images/jeux.png");

        sideNav.getChildren().addAll(bntHome, btnTheme, quizContainer, btnJeux);
        return sideNav;
    }

    private HBox createQuizComboBoxContainer() {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);
        container.setSpacing(0);

        // Bouton principal Quiz
        Button quizButton = createNavButton("Quiz", "/Images/quiz.png");

        // ComboBox pour les sous-options
        ComboBox<String> quizComboBox = new ComboBox<>();
        quizComboBox.setPromptText("Options");
        quizComboBox.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        quizComboBox.setPrefWidth(40);

        // Personnalisation de la flèche du ComboBox
        quizComboBox.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        HBox box = new HBox(10);
                        ImageView icon = new ImageView();

                        if (item.equals("Questions")) {
                            icon.setImage(new Image("/Images/question.png", 20, 20, true, true));
                        } else if (item.equals("Options")) {
                            icon.setImage(new Image("/Images/option.png", 20, 20, true, true));
                        }

                        Label label = new Label(item);
                        label.setStyle("-fx-text-fill: #333; -fx-font-size: 14px;");

                        box.getChildren().addAll(icon, label);
                        box.setAlignment(Pos.CENTER_LEFT);
                        setGraphic(box);
                    }
                }
            };
            return cell;
        });

        // Ajout des items
        quizComboBox.getItems().addAll("Questions", "Options");

        // Gestion de la sélection
        quizComboBox.setOnAction(e -> {
            String selected = quizComboBox.getValue();
            if (selected != null) {
                switch (selected) {
                    case "Questions":
                        // Créer une nouvelle instance de GestionJeu_QUESTION pour afficher toutes les questions
                        Stage questionStage = new Stage();
                        // Passer -1 comme quizId pour indiquer qu'on veut toutes les questions
                        GestionJeu_QUESTION questionApp = new GestionJeu_QUESTION(-1, 0);
                        try {
                            questionApp.start(questionStage);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        // Fermer la fenêtre actuelle si nécessaire
                        ((Stage) quizComboBox.getScene().getWindow()).close();
                        break;
                    case "Options":
                        System.out.println("Ouverture gestion des options");
                        // Ajoutez votre logique ici
                        break;
                }
                quizComboBox.getSelectionModel().clearSelection(); // Réinitialiser la sélection
            }
        });

        // Style du ComboBox
        //   quizComboBox.setButtonCell(new ListCell<String>() {
        //   @Override
        //    protected void updateItem(String item, boolean empty) {
        //   super.updateItem(item, empty);
        //   if (empty || item == null) {
        //        setGraphic(null);
        //   } else {
        //       ImageView arrow = new ImageView(new Image("/Images/arrow_down.png", 16, 16, true, true));
        //       setGraphic(arrow);
        //     }
        //    }
        //   });

        container.getChildren().addAll(quizButton, quizComboBox);
        return container;
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

        // Effet hover sur toute la ligne
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
        // Ajout de l'action spécifique pour le bouton Accueil
        if (text.equals("Acceuil")) {
            button.setOnAction(e -> {
                // Créer et afficher la fenêtre GestionJeu_ADMIN
                Stage adminStage = new Stage();
                GestionJeu_ADMIN adminApp = new GestionJeu_ADMIN();

                try {
                    adminApp.start(adminStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // Fermer la fenêtre actuelle si nécessaire
                ((Stage) button.getScene().getWindow()).close();
            });
        }
        // Action pour le bouton Quiz
        if (text.equals("Quiz")) {
            button.setOnAction(e -> {
                Stage quizStage = new Stage();
                GestionJeu_QUIZ quizApp = new GestionJeu_QUIZ();
                try {
                    quizApp.start(quizStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                ((Stage) button.getScene().getWindow()).close();
            });
        }
        // Action pour le bouton Theme
        if (text.equals("Thème")) {
            button.setOnAction(e -> {
                Stage themeStage = new Stage();
                GestionJeu_THEME themeApp = new GestionJeu_THEME();
                try {
                    themeApp.start(themeStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                ((Stage) button.getScene().getWindow()).close();
            });
        }
        // Action pour le bouton Jeux
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
        // Autres actions de boutons...

        return button;
    }

    private VBox createDashboardContent() throws SQLException {
        // Création du TabPane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Onglet de création des thèmes
        Tab createTab = new Tab("Création des Thèmes");
        createTab.setContent(createThemeCreationTab());

        // Onglet de liste des thèmes
        Tab listTab = new Tab("Liste des Thèmes");
        VBox listTabContent = createThemeListTab();
        listTab.setContent(listTabContent);

        // Stocker la référence pour pouvoir rafraîchir
        this.themeListTabContent = listTabContent;

        tabPane.getTabs().addAll(createTab, listTab);

        VBox container = new VBox(tabPane);
        container.setPadding(new Insets(10));
        return container;
    }

    private VBox createThemeCreationTab() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label("Création d'un nouveau thème");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + toHex(PRIMARY_COLOR) + ";");

        // Formulaire de création
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));

        // Nom du thème
        Label nameLabel = new Label("Nom du thème:");
        TextField nameField = new TextField();
        nameField.setPromptText("Entrez le nom du thème");

        // Validation - ne peut pas commencer par un chiffre
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && Character.isDigit(newVal.charAt(0))) {
                showAlert("Erreur", "Le nom ne peut pas commencer par un chiffre", Alert.AlertType.WARNING);
                nameField.setText(oldVal);
            }
        });

        form.add(nameLabel, 0, 0);
        form.add(nameField, 1, 0);

        // Description
        Label descLabel = new Label("Description:");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Entrez la description du thème");
        descArea.setPrefRowCount(3);
        descArea.setWrapText(true);

        // Validation - ne peut pas commencer par un chiffre
        descArea.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && Character.isDigit(newVal.charAt(0))) {
                showAlert("Erreur", "La description ne peut pas commencer par un chiffre", Alert.AlertType.WARNING);
                descArea.setText(oldVal);
            }
        });

        form.add(descLabel, 0, 1);
        form.add(descArea, 1, 1);

        // Image
        Label imageLabel = new Label("Image:");
        TextField imageField = new TextField();
        imageField.setPromptText("Chemin de l'image");
        Button browseButton = new Button("Parcourir");
        browseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
            );
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                imageField.setText(file.getAbsolutePath());
            }
        });
        HBox imageBox = new HBox(10, imageField, browseButton);
        form.add(imageLabel, 0, 2);
        form.add(imageBox, 1, 2);

        // Bouton d'enregistrement
        Button saveButton = new Button("Enregistrer le thème");
        saveButton.setStyle("-fx-background-color: " + toHex(PRIMARY_COLOR) + "; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            if (validateAndSaveTheme(nameField.getText(), descArea.getText(), imageField.getText())) {
                nameField.clear();
                descArea.clear();
                imageField.clear();

                // Basculer vers l'onglet Liste après enregistrement
                TabPane tabPane = (TabPane) container.getParent().getParent();
                tabPane.getSelectionModel().select(1);

                // Rafraîchir la liste
                try {
                    refreshThemeList();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        container.getChildren().addAll(titleLabel, form, saveButton);
        return container;
    }
    private boolean validateAndSaveTheme(String themeNomText,String themeText,
                                            String imagePath) {
        // Validation
        if (themeNomText.isEmpty()) {
            showAlert("Erreur", "Veuillez saisir le nom du thème ", Alert.AlertType.ERROR);
            return false;
        }
        if (themeText.isEmpty()) {
            showAlert("Erreur", "Veuillez saisir la dscription  du thème ", Alert.AlertType.ERROR);
            return false;
        }

        if (!imagePath.isEmpty()) {
            String extension = imagePath.substring(imagePath.lastIndexOf(".") + 1).toLowerCase();
            if (!extension.matches("png|jpg|jpeg")) {
                showAlert("Erreur", "Format d'image non valide. Utilisez .png, .jpg ou .jpeg", Alert.AlertType.ERROR);
                return false;
            }
        }

        try {
            // Enregistrer la question
            Theme theme = new Theme();
            theme.setNomTheme(themeNomText);
            theme.setDescriptionTheme(themeText);
            theme.setImageTheme(imagePath.isEmpty() ? null : imagePath);

            // Ajout des dates automatiques
            LocalDateTime now = LocalDateTime.now();
            theme.setDateAjoutTheme(now);
            theme.setDateDerniereModification(now);

            ThemeService themeService = new ThemeService();
            ThemeService.addTheme(theme);

            // Récupérer l'ID de la question créée
            currentthemeId = themeService.getLastInsertedId();

            return true;
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'enregistrement: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }
    private VBox createThemeListTab() throws SQLException {
        VBox container = new VBox(10);
        container.setPadding(new Insets(20));

        // Titre
        Label titleLabel = new Label("Liste des Thèmes");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + toHex(PRIMARY_COLOR) + ";");

        // TableView pour afficher les thèmes - INITIALISATION ICI
        themeTable = new TableView<>(); // Initialisation de la variable de classe
        setupThemeTable(themeTable);

        // Charger les données
        loadThemesData();

        container.getChildren().addAll(titleLabel, themeTable);
        return container;
    }

    private void setupThemeTable(TableView<Theme> table) {
        table.getColumns().clear();

        // Colonne ID
        TableColumn<Theme, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Colonne Nom
        TableColumn<Theme, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("nomTheme"));
        nameCol.setPrefWidth(150);

        // Colonne Description
        TableColumn<Theme, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("descriptionTheme"));
        descCol.setPrefWidth(250);

        // Colonne Image
        TableColumn<Theme, String> imageCol = new TableColumn<>("Image");
        imageCol.setCellValueFactory(new PropertyValueFactory<>("imageTheme"));
        imageCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) {
                    setGraphic(null);
                } else {
                    ImageView imageView = new ImageView(new Image("file:" + item));
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    setGraphic(imageView);
                }
            }
        });

        // Colonne Date d'ajout
        TableColumn<Theme, LocalDateTime> addDateCol = new TableColumn<>("Date d'ajout");
        addDateCol.setCellValueFactory(new PropertyValueFactory<>("dateAjoutTheme"));
        addDateCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                }
            }
        });

        // Colonne Actions
        TableColumn<Theme, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(column -> new TableCell<>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox pane = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: " + toHex(PRIMARY_COLOR) + "; -fx-text-fill: white; -fx-font-size: 12px;");
                deleteBtn.setStyle("-fx-background-color: " + toHex(ACCENT_COLOR) + "; -fx-text-fill: white; -fx-font-size: 12px;");

                editBtn.setOnAction(event -> {
                    Theme theme = getTableView().getItems().get(getIndex());
                    showEditThemeDialog(theme, getTableView());
                });

                deleteBtn.setOnAction(event -> {
                    Theme theme = getTableView().getItems().get(getIndex());
                    try {
                        showDeleteThemeConfirmation(theme, getTableView());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        table.getColumns().addAll(idCol, nameCol, descCol, imageCol, addDateCol, actionsCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void refreshThemeList() throws SQLException {
        if (themeListTabContent != null) {
            TableView<Theme> table = (TableView<Theme>) themeListTabContent.getChildren().get(1);
            loadThemesData();
        }
    }


    private void showEditThemeDialog(Theme theme, TableView<Theme> table) {
        Dialog<Theme> dialog = new Dialog<>();
        dialog.setTitle("Modifier Thème");
        dialog.setHeaderText("Modifier les détails du thème");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Création du formulaire
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));

        // Nom du thème
        TextField nameField = new TextField(theme.getNomTheme());
        nameField.setPromptText("Nom du thème");
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && Character.isDigit(newVal.charAt(0))) {
                showAlert("Erreur", "Le nom ne peut pas commencer par un chiffre", Alert.AlertType.WARNING);
                nameField.setText(oldVal);
            }
        });
        form.add(new Label("Nom:"), 0, 0);
        form.add(nameField, 1, 0);

        // Description
        TextArea descArea = new TextArea(theme.getDescriptionTheme());
        descArea.setPromptText("Description");
        descArea.setPrefRowCount(3);
        descArea.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && Character.isDigit(newVal.charAt(0))) {
                showAlert("Erreur", "La description ne peut pas commencer par un chiffre", Alert.AlertType.WARNING);
                descArea.setText(oldVal);
            }
        });
        form.add(new Label("Description:"), 0, 1);
        form.add(descArea, 1, 1);

        // Image
        TextField imageField = new TextField(theme.getImageTheme() != null ? theme.getImageTheme() : "");
        imageField.setPromptText("Image");
        Button browseButton = new Button("Parcourir");
        browseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
            );
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                imageField.setText(file.getAbsolutePath());
            }
        });
        form.add(new Label("Image:"), 0, 2);
        form.add(new HBox(10, imageField, browseButton), 1, 2);

        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                theme.setNomTheme(nameField.getText());
                theme.setDescriptionTheme(descArea.getText());
                theme.setImageTheme(imageField.getText().isEmpty() ? null : imageField.getText());
                theme.setDateDerniereModification(LocalDateTime.now());
                return theme;
            }
            return null;
        });

        Optional<Theme> result = dialog.showAndWait();
        result.ifPresent(updatedTheme -> {
            ThemeService themeService = new ThemeService();
            themeService.updateTheme(updatedTheme, updatedTheme.getId());
            try {
                loadThemesData();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            showAlert("Succès", "Thème mis à jour", Alert.AlertType.INFORMATION);
        });
    }

    private void showDeleteThemeConfirmation(Theme theme, TableView<Theme> table) throws SQLException {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer ce thème?");
        confirm.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ThemeService themeService = new ThemeService();
            themeService.deleteTheme(theme.getId());
            loadThemesData();
            showAlert("Succès", "Thème supprimé", Alert.AlertType.INFORMATION);
        }
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

        MenuItem adminAccount = new MenuItem("Bienvenue STAMBOULI Sarra !");
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
    private void loadThemesData() throws SQLException {
        ThemeService themeService = new ThemeService();
        List<Theme> themes = themeService.getAllThemes();

        ObservableList<Theme> observableList = FXCollections.observableArrayList(themes);
        themeTable.setItems(observableList); // Utilise la variable de classe déjà initialisée
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
