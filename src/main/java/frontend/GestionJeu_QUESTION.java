package frontend;

import entities.Quiz;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import services.QuizService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import tools.MyConnection;
import entities.Question;
import services.QuestionService;
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
import entities.Option;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import services.OptionService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import javafx.animation.RotateTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

public class GestionJeu_QUESTION extends Application {

    private boolean isNavBarOpen = true;
    private VBox sideNav;
    private final Color PRIMARY_COLOR = Color.web("#3f51b5");
    private final Color MENUCOLOR = Color.web("#BECCE4");
    private final Color SECONDARY_COLOR = Color.web("#f5f5f5");
    private final Color ACCENT_COLOR = Color.web("#ff5722");
    private final Color HOVER_COLOR = Color.web("#3949ab");
    private final Color SELECTED_COLOR = Color.web("#1a237e");
    private final int quizId;
    private final int totalQuestions;
    private Pagination pagination;
    private TableView<Question> questionTable; // Ajoutez cette ligne
    private int notificationCount = 3; // Exemple: 3 notifications non lues
    private MediaPlayer notificationPlayer;

    public GestionJeu_QUESTION(int quizId, int totalQuestions) {
        this.quizId = quizId;
        this.totalQuestions = totalQuestions;
    }

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

        Button bntHome = createNavButton("Acceuil", "/Images/homejeu.png");
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
       // quizComboBox.setButtonCell(new ListCell<String>() {
         //   @Override
           // protected void updateItem(String item, boolean empty) {
           //     super.updateItem(item, empty);
            //    if (empty || item == null) {
              //      setGraphic(null);
              //  } else {
              //      ImageView arrow = new ImageView(new Image("/Images/arrow_down.png", 16, 16, true, true));
               //     setGraphic(arrow);
              //  }
           // }
      //  });

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

        // Onglet de création des questions
        Tab createTab = new Tab("Création des Questions");
        createTab.setContent(createQuestionCreationTab());

        // Onglet de liste des questions
        Tab listTab = new Tab("Liste des Questions");
        listTab.setContent(createQuestionListTab());

        tabPane.getTabs().addAll(createTab, listTab);

        VBox container = new VBox(tabPane);
        container.setPadding(new Insets(10));
        return container;
    }
    private VBox createQuestionCreationTab() {
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label("Création des Questions - Quiz #" + quizId);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + toHex(PRIMARY_COLOR) + ";");

        // Pagination
        pagination = new Pagination(totalQuestions, 0);
        pagination.setPageFactory(this::createQuestionPage);
        pagination.setStyle("-fx-page-information-visible: false;");

        // Création du ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: white; -fx-border-color: white;");
        scrollPane.setPannable(true);

        mainContainer.getChildren().addAll(titleLabel, pagination);

        VBox outerContainer = new VBox(scrollPane);
        outerContainer.setPadding(new Insets(0));
        return outerContainer;
    }

    private VBox createQuestionListTab() throws SQLException {
        VBox container = new VBox(10);
        container.setPadding(new Insets(20));

        // Titre
        Label titleLabel = new Label("Liste des Questions - Quiz #" + quizId);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + toHex(PRIMARY_COLOR) + ";");

        // TableView pour afficher les questions
        questionTable = new TableView<>();
        setupQuestionsTable();

        // Boutons d'action
        HBox buttonsBox = new HBox(10);
        Button editButton = new Button("Modifier");
        editButton.setStyle("-fx-background-color: " + toHex(PRIMARY_COLOR) + "; -fx-text-fill: white;");
        editButton.setOnAction(e -> editSelectedQuestion());

        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: " + toHex(ACCENT_COLOR) + "; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> {
            try {
                deleteSelectedQuestion();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        buttonsBox.getChildren().addAll(editButton, deleteButton);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        // Charger les données
        loadQuestionsData();

        container.getChildren().addAll(titleLabel, questionTable);
        return container;
    }

    private void setupQuestionsTable() {
        questionTable.getColumns().clear();

        // Colonne ID
        TableColumn<Question, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Colonne Contenu
        TableColumn<Question, String> contentCol = new TableColumn<>("Question");
        contentCol.setCellValueFactory(new PropertyValueFactory<>("contenuQuestion"));
        contentCol.setPrefWidth(300);

        // Colonne Nombre d'options
        TableColumn<Question, Integer> optionsCol = new TableColumn<>("Nb Options");
        optionsCol.setCellValueFactory(new PropertyValueFactory<>("nombreOptions"));

        // Colonne Image
        TableColumn<Question, String> imageCol = new TableColumn<>("Image");
        imageCol.setCellValueFactory(new PropertyValueFactory<>("image"));

        // Colonne Options (affichage des options)
        TableColumn<Question, String> optionsDetailsCol = new TableColumn<>("Options");
        optionsDetailsCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Question question = getTableView().getItems().get(getIndex());
                    VBox optionsBox = new VBox(5);

                    OptionService optionService = new OptionService();
                    List<Option> options = null;
                    try {
                        options = optionService.getAllOptionsByQuestionId(question.getId());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    for (Option option : options) {
                        HBox optionRow = new HBox(5);
                        Label optionLabel = new Label(option.getContenuOption());
                        if (option.isEstCorrecte()) {
                            optionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: green;");
                        }
                        optionsBox.getChildren().add(optionLabel);
                    }

                    setGraphic(optionsBox);
                }
            }
        });

        // Colonne Actions
        TableColumn<Question, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(column -> new TableCell<>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox pane = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: " + toHex(PRIMARY_COLOR) + "; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: " + toHex(ACCENT_COLOR) + "; -fx-text-fill: white;");

                editBtn.setOnAction(event -> {
                    Question question = getTableView().getItems().get(getIndex());
                    try {
                        showEditQuestionDialog(question);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                deleteBtn.setOnAction(event -> {
                    Question question = getTableView().getItems().get(getIndex());
                    showDeleteQuestionConfirmation(question);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        questionTable.getColumns().addAll(idCol, contentCol, optionsCol, imageCol, optionsDetailsCol, actionsCol);
        questionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadQuestionsData() throws SQLException {
        QuestionService questionService = new QuestionService();
        List<Question> questions;

        if (quizId == -1) {
            // Charger toutes les questions
            questions = questionService.getAllQuestions();
        } else {
            // Charger seulement les questions du quiz spécifié
            questions = questionService.getAllQuestionsByQuizId(quizId);
        }

        ObservableList<Question> observableList = FXCollections.observableArrayList(questions);
        questionTable.setItems(observableList);
    }

    private void editSelectedQuestion() {
        Question selected = questionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Ouvrir une fenêtre de modification
            // Vous pouvez créer une nouvelle fenêtre ou un dialogue pour la modification
            System.out.println("Modification de la question: " + selected.getId());
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une question à modifier", Alert.AlertType.WARNING);
        }
    }
    private void showEditQuestionDialog(Question question) throws SQLException {
        Dialog<Question> dialog = new Dialog<>();
        dialog.setTitle("Modifier Question");
        dialog.setHeaderText("Modifier la question et ses options");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Création du formulaire
        VBox formContainer = new VBox(15);
        formContainer.setPadding(new Insets(15));

        // Champ question
        TextArea questionContent = new TextArea(question.getContenuQuestion());
        questionContent.setPromptText("Question");
        questionContent.setPrefRowCount(3);

        // Image
        HBox imageContainer = new HBox(10);
        TextField imagePathField = new TextField(question.getImage());
        imagePathField.setPromptText("Image (optionnel)");
        Button browseButton = new Button("Parcourir");
        browseButton.setOnAction(e -> handleImageBrowse(imagePathField));
        imageContainer.getChildren().addAll(imagePathField, browseButton);

        // Options
        Label optionsLabel = new Label("Options:");
        VBox optionsContainer = new VBox(10);

        OptionService optionService = new OptionService();
        List<Option> options = optionService.getAllOptionsByQuestionId(question.getId());

        for (Option option : options) {
            HBox optionBox = new HBox(10);
            TextField contentField = new TextField(option.getContenuOption());
            CheckBox isCorrect = new CheckBox("Correcte");
            isCorrect.setSelected(option.isEstCorrecte());

            // Image option
            TextField optionImageField = new TextField(option.getImage1());
            Button optionBrowseBtn = new Button("Parcourir");
            optionBrowseBtn.setOnAction(e -> handleImageBrowse(optionImageField));

            optionBox.getChildren().addAll(
                    new Label("Option:"), contentField,
                    new Label("Image:"), optionImageField, optionBrowseBtn,
                    isCorrect
            );
            optionsContainer.getChildren().add(optionBox);
        }

        formContainer.getChildren().addAll(
                new Label("Question:"), questionContent,
                new Label("Image:"), imageContainer,
                optionsLabel, optionsContainer
        );

        ScrollPane scrollPane = new ScrollPane(formContainer);
        scrollPane.setFitToWidth(true);
        dialog.getDialogPane().setContent(scrollPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                // Mettre à jour la question
                question.setContenuQuestion(questionContent.getText());
                question.setImage(imagePathField.getText().isEmpty() ? null : imagePathField.getText());

                // Mettre à jour les options
                for (int i = 0; i < options.size(); i++) {
                    HBox optionBox = (HBox) optionsContainer.getChildren().get(i);
                    Option option = options.get(i);

                    TextField contentField = (TextField) optionBox.getChildren().get(1);
                    TextField imageField = (TextField) optionBox.getChildren().get(3);
                    CheckBox isCorrect = (CheckBox) optionBox.getChildren().get(6);

                    option.setContenuOption(contentField.getText());
                    option.setImage1(imageField.getText().isEmpty() ? null : imageField.getText());
                    option.setEstCorrecte(isCorrect.isSelected());
                }

                return question;
            }
            return null;
        });

        Optional<Question> result = dialog.showAndWait();
        result.ifPresent(updatedQuestion -> {
            try {
                QuestionService questionService = new QuestionService();
                questionService.updateQuestion(updatedQuestion, updatedQuestion.getId());

                // Mettre à jour les options
                for (Option option : options) {
                    optionService.updateOption(option, option.getId());
                }

                refreshQuestionsTable();
                showAlert("Succès", "Question et options mises à jour", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la mise à jour: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }
    private void showDeleteQuestionConfirmation(Question question) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText("Supprimer cette question?");
        confirm.setContentText("Cette action supprimera également toutes les options associées.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // D'abord supprimer les options
                OptionService optionService = new OptionService();
                optionService.deleteOptionsByQuestionId(question.getId());

                // Puis supprimer la question
                QuestionService questionService = new QuestionService();
                questionService.deleteQuestion(question.getId());

                // Mettre à jour le nombre de questions dans le quiz
                QuizService quizService = new QuizService();
                Quiz quiz = quizService.getQuizById(question.getQuizId());
                quiz.setNombreQuestions(quiz.getNombreQuestions() - 1);
                quizService.updateQuiz(quiz, quiz.getId());

                refreshQuestionsTable();
                showAlert("Succès", "Question et options supprimées", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    private void handleImageBrowse(TextField targetField) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            targetField.setText(file.getAbsolutePath());
        }
    }
    private void refreshQuestionsTable() {
        try {
            QuestionService questionService = new QuestionService();
            List<Question> questions;

            if (quizId == -1) {
                questions = questionService.getAllQuestions();
            } else {
                questions = questionService.getAllQuestionsByQuizId(quizId);
            }

            questionTable.setItems(FXCollections.observableArrayList(questions));
            questionTable.refresh();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du rafraîchissement: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private void deleteSelectedQuestion() throws SQLException {
        Question selected = questionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation de suppression");
            confirm.setHeaderText(null);
            confirm.setContentText("Êtes-vous sûr de vouloir supprimer cette question ?");

            if (confirm.showAndWait().get() == ButtonType.OK) {
                QuestionService questionService = new QuestionService();
                questionService.deleteQuestion(selected.getId());
                loadQuestionsData(); // Rafraîchir la table
                showAlert("Succès", "Question supprimée avec succès", Alert.AlertType.INFORMATION);
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une question à supprimer", Alert.AlertType.WARNING);
        }
    }

    private int currentQuestionId; // Ajoutez ce champ à votre classe

    private Node createQuestionPage(int pageIndex) {
        VBox pageContainer = new VBox(15);
        pageContainer.setPadding(new Insets(20));
        pageContainer.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-width: 1;");
        pageContainer.setMinHeight(600); // Hauteur minimale pour forcer le scroll si nécessaire

        Label questionTitle = new Label("Question " + (pageIndex + 1) + "/" + totalQuestions);
        questionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Champ question
        TextArea questionContent = new TextArea();
        questionContent.setPromptText("Entrez le texte de la question...");
        questionContent.setPrefRowCount(3);
        questionContent.setWrapText(true);

        // Validation - ne peut pas commencer par un chiffre
        questionContent.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && Character.isDigit(newVal.charAt(0))) {
                showAlert("Erreur", "La question ne peut pas commencer par un chiffre", Alert.AlertType.WARNING);
                questionContent.setText(oldVal);
            }
        });

        // Image optionnelle
        HBox imageContainer = new HBox(10);
        TextField imagePathField = new TextField();
        imagePathField.setPromptText("Chemin de l'image (optionnel)");
        Button browseButton = new Button("Parcourir");
        browseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
            );
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                imagePathField.setText(file.getAbsolutePath());
            }
        });
        imageContainer.getChildren().addAll(imagePathField, browseButton);

        // Nombre d'options (ComboBox 2-4)
        HBox optionsCountContainer = new HBox(10);
        Label optionsCountLabel = new Label("Nombre d'options:");
        ComboBox<Integer> optionsCountCombo = new ComboBox<>();
        optionsCountCombo.getItems().addAll(2, 3, 4);
        optionsCountCombo.setValue(4); // Valeur par défaut
        optionsCountContainer.getChildren().addAll(optionsCountLabel, optionsCountCombo);

        // Conteneur pour les options (vide initialement)
        VBox optionsContainer = new VBox(10);
        optionsContainer.setVisible(false);

        // Boutons
        HBox buttonContainer = new HBox(15);
        buttonContainer.setAlignment(Pos.CENTER);

        Button saveButton = new Button("Enregistrer Question");
        saveButton.setStyle("-fx-background-color: " + toHex(PRIMARY_COLOR) + "; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            if (validateAndSaveQuestion(pageIndex, questionContent.getText(),
                    imagePathField.getText(), optionsCountCombo.getValue())) {
                // Afficher les options après validation
                optionsContainer.setVisible(true);
                createOptionFields(optionsContainer, optionsCountCombo.getValue(), currentQuestionId);
                saveButton.setDisable(true); // Désactiver après enregistrement
            }
        });

        buttonContainer.getChildren().add(saveButton);

        pageContainer.getChildren().addAll(
                questionTitle,
                new Label("Question:"),
                questionContent,
                new Label("Image (optionnelle):"),
                imageContainer,
                optionsCountContainer,
                buttonContainer,
                optionsContainer
        );

        return pageContainer;
    }

    private boolean validateAndSaveQuestion(int questionIndex, String questionText,
                                            String imagePath, int optionsCount) {
        // Validation
        if (questionText.isEmpty()) {
            showAlert("Erreur", "Veuillez saisir le texte de la question", Alert.AlertType.ERROR);
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
            Question question = new Question();
            question.setQuizId(quizId);
            question.setContenuQuestion(questionText);
            question.setImage(imagePath.isEmpty() ? null : imagePath);
            question.setNombreOptions(optionsCount);

            QuestionService questionService = new QuestionService();
            questionService.addQuestion(question);

            // Récupérer l'ID de la question créée
            currentQuestionId = questionService.getLastInsertedId();

            return true;
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'enregistrement: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    private void createOptionFields(VBox container, int optionsCount, int questionId) {
        container.getChildren().clear();

        for (int i = 0; i < optionsCount; i++) {
            VBox optionBox = new VBox(5);
            optionBox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10px; -fx-border-color: #ddd; -fx-border-width: 1;");

            Label optionLabel = new Label("Option " + (i + 1));
            optionLabel.setStyle("-fx-font-weight: bold;");

            // Contenu textuel
            TextField contentField = new TextField();
            contentField.setPromptText("Contenu de l'option");

            // Validation - contenu obligatoire
            contentField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.isEmpty()) {
                    contentField.setStyle("-fx-border-color: red;");
                } else {
                    contentField.setStyle("");
                }
            });

            // Case à cocher "Correcte"
            CheckBox isCorrect = new CheckBox("Option correcte");

            // Image optionnelle
            HBox imageBox = new HBox(5);
            TextField imageField = new TextField();
            imageField.setPromptText("Image optionnelle");
            Button browseBtn = new Button("Parcourir");
            browseBtn.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
                );
                File file = fileChooser.showOpenDialog(null);
                if (file != null) {
                    imageField.setText(file.getAbsolutePath());
                }
            });
            imageBox.getChildren().addAll(imageField, browseBtn);

            optionBox.getChildren().addAll(optionLabel, contentField, imageBox, isCorrect);
            container.getChildren().add(optionBox);
        }

        // Bouton pour enregistrer les options
        Button saveOptionsBtn = new Button("Enregistrer les Options et Passer à la Suivante");
        saveOptionsBtn.setStyle("-fx-background-color: " + toHex(PRIMARY_COLOR) + "; -fx-text-fill: white;");
        saveOptionsBtn.setOnAction(e -> {
            if (saveOptions(container, questionId)) {
                // Passer à la question suivante si ce n'est pas la dernière
                if (pagination.getCurrentPageIndex() < totalQuestions - 1) {
                    pagination.setCurrentPageIndex(pagination.getCurrentPageIndex() + 1);
                } else {
                    showAlert("Félicitations", "Toutes les questions ont été créées!", Alert.AlertType.INFORMATION);
                }
            }
        });

        container.getChildren().add(saveOptionsBtn);
    }

    private boolean saveOptions(VBox container, int questionId) {
        OptionService optionService = new OptionService();
        boolean hasErrors = false;
        int correctOptionsCount = 0;

        for (Node node : container.getChildren()) {
            if (node instanceof VBox) {
                VBox optionBox = (VBox) node;

                // Récupérer les valeurs
                TextField contentField = (TextField) optionBox.getChildren().get(1);
                TextField imageField = (TextField) ((HBox) optionBox.getChildren().get(2)).getChildren().get(0);
                CheckBox isCorrect = (CheckBox) optionBox.getChildren().get(3);

                // Validation
                if (contentField.getText().isEmpty()) {
                    contentField.setStyle("-fx-border-color: red;");
                    hasErrors = true;
                    continue;
                }

                if (!imageField.getText().isEmpty()) {
                    String extension = imageField.getText().substring(imageField.getText().lastIndexOf(".") + 1).toLowerCase();
                    if (!extension.matches("png|jpg|jpeg")) {
                        showAlert("Erreur", "Format d'image non valide pour l'option", Alert.AlertType.ERROR);
                        hasErrors = true;
                        continue;
                    }
                }

                if (isCorrect.isSelected()) {
                    correctOptionsCount++;
                }

                // Créer et sauvegarder l'option
                Option option = new Option();
                option.setQuestionId(questionId);
                option.setContenuOption(contentField.getText());
                option.setImage1(imageField.getText().isEmpty() ? null : imageField.getText());
                option.setEstCorrecte(isCorrect.isSelected());

                optionService.addOption(option);
            }
        }

        // Vérifier qu'il y a au moins une option correcte
        if (correctOptionsCount == 0) {
            showAlert("Erreur", "Vous devez sélectionner au moins une option correcte", Alert.AlertType.ERROR);
            return false;
        }

        if (hasErrors) {
            showAlert("Erreur", "Veuillez corriger les champs en rouge", Alert.AlertType.ERROR);
            return false;
        }

        showAlert("Succès", "Options enregistrées avec succès!", Alert.AlertType.INFORMATION);
        return true;
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


