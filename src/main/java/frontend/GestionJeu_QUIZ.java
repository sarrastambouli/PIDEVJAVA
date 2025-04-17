package frontend;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import tools.MyConnection;
import entities.Question;
import services.QuestionService;
import entities.Option;
import services.OptionService;
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

public class GestionJeu_QUIZ extends Application {

    private boolean isNavBarOpen = true;
    private VBox sideNav;
    private final Color PRIMARY_COLOR = Color.web("#3f51b5");
    private final Color MENUCOLOR = Color.web("#BECCE4");
    private final Color SECONDARY_COLOR = Color.web("#f5f5f5");
    private final Color ACCENT_COLOR = Color.web("#ff5722");
    private final Color HOVER_COLOR = Color.web("#3949ab");
    private final Color SELECTED_COLOR = Color.web("#1a237e");
    private TableView<Quiz> quizTable; // Ajoutez cette ligne
    private int notificationCount = 3; // Exemple: 3 notifications non lues
    private MediaPlayer notificationPlayer;


    @Override
    public void start(Stage primaryStage) {
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

    private VBox createDashboardContent() {
        VBox mainContainer = new VBox(20);
        mainContainer.setId("mainContainer"); // Ajout d'un ID pour le retrouver
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: white;");

        // Titre principal
        Label titleLabel = new Label("Gestion des Quiz");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + toHex(PRIMARY_COLOR) + ";");

        // Création du TabPane pour les deux onglets
        TabPane tabPane = new TabPane();

        // Onglet 1: Création de quiz
        Tab createTab = new Tab("Créer un Quiz");
        createTab.setContent(createQuizForm());
        createTab.setClosable(false);

        // Onglet 2: Liste des quiz
        Tab listTab = new Tab("Liste des Quiz");
        listTab.setContent(createQuizTable());
        listTab.setClosable(false);

        tabPane.getTabs().addAll(createTab, listTab);

        mainContainer.getChildren().addAll(titleLabel, tabPane);
        return mainContainer;
    }
    private VBox createQuizForm() {
        VBox formContainer = new VBox(15);
        formContainer.setPadding(new Insets(20));
        formContainer.setStyle("-fx-background-color: #f9f9f9;");

        Label formTitle = new Label("Nouveau Quiz");
        formTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane quizForm = new GridPane();
        quizForm.setVgap(15);
        quizForm.setHgap(10);
        quizForm.setAlignment(Pos.CENTER);

        TextField nomQuizField = new TextField();
        nomQuizField.setPromptText("Nom du quiz");
        nomQuizField.setPrefWidth(300);

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Description");
        descriptionField.setPrefRowCount(3);
        descriptionField.setPrefWidth(300);

        ComboBox<Integer> nbQuestionsCombo = new ComboBox<>();
        nbQuestionsCombo.setPromptText("Nombre de questions");
        nbQuestionsCombo.getItems().addAll(2, 3, 4, 5, 6, 7, 8, 9, 10);
        nbQuestionsCombo.setPrefWidth(300);

        Button submitButton = new Button("Créer Quiz");
        submitButton.setStyle("-fx-background-color: " + toHex(PRIMARY_COLOR) + "; -fx-text-fill: white; -fx-font-weight: bold;");
        submitButton.setOnAction(e -> handleQuizCreation(nomQuizField, descriptionField, nbQuestionsCombo));

        quizForm.add(new Label("Nom du quiz:"), 0, 0);
        quizForm.add(nomQuizField, 1, 0);
        quizForm.add(new Label("Description:"), 0, 1);
        quizForm.add(descriptionField, 1, 1);
        quizForm.add(new Label("Nombre de questions:"), 0, 2);
        quizForm.add(nbQuestionsCombo, 1, 2);
        quizForm.add(submitButton, 1, 3);

        formContainer.getChildren().addAll(formTitle, quizForm);
        return formContainer;
    }
    private VBox createQuizTable() {
        VBox tableContainer = new VBox(15);
        tableContainer.setPadding(new Insets(20));
        tableContainer.setStyle("-fx-background-color: #f9f9f9;");

        Label tableTitle = new Label("Quiz Existants");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Création du TableView
        quizTable = new TableView<>();

        // Colonne ID
        TableColumn<Quiz, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Colonne Nom
        TableColumn<Quiz, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nomQuiz"));
        nomCol.setPrefWidth(200);

        // Colonne Description
        TableColumn<Quiz, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("descriptionQuiz"));
        descCol.setPrefWidth(300);

        // Colonne Nombre de questions
        TableColumn<Quiz, Integer> nbQCol = new TableColumn<>("Questions");
        nbQCol.setCellValueFactory(new PropertyValueFactory<>("nombreQuestions"));

        // Colonne Actions
        TableColumn<Quiz, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);

        // Configuration des cellules d'action
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final Button viewBtn = new Button("Questions");

            {
                // Style des boutons
                editBtn.setStyle("-fx-background-color: " + toHex(PRIMARY_COLOR) + "; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white;");
                viewBtn.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white;");

                // Actions des boutons
                editBtn.setOnAction(event -> {
                    Quiz quiz = getTableView().getItems().get(getIndex());
                    showEditQuizDialog(quiz);
                });

                deleteBtn.setOnAction(event -> {
                    Quiz quiz = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(quiz);
                });

                viewBtn.setOnAction(event -> {
                    Quiz quiz = getTableView().getItems().get(getIndex());
                    // Ouvrir la gestion des questions
                    Stage questionStage = new Stage();
                    GestionJeu_QUESTION questionApp = new GestionJeu_QUESTION(quiz.getId(), quiz.getNombreQuestions());
                    try {
                        questionApp.start(questionStage);
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
                    HBox buttons = new HBox(5, editBtn, deleteBtn, viewBtn);
                    setGraphic(buttons);
                }
            }
        });

        // Ajout des colonnes
        quizTable.getColumns().addAll(idCol, nomCol, descCol, nbQCol, actionsCol);

        // Chargement des données
        try {
            QuizService quizService = new QuizService();
            quizTable.setItems(FXCollections.observableArrayList(quizService.getAllQuizzes()));
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des quiz: " + e.getMessage(), Alert.AlertType.ERROR);
        }

        // Ajout d'un bouton de rafraîchissement
        Button refreshBtn = new Button("Rafraîchir la liste");
        refreshBtn.setStyle("-fx-background-color: " + toHex(PRIMARY_COLOR) + "; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> {
            try {
                QuizService quizService = new QuizService();
                quizTable.setItems(FXCollections.observableArrayList(quizService.getAllQuizzes()));
            } catch (Exception ex) {
                showAlert("Erreur",  ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        tableContainer.getChildren().addAll(tableTitle, quizTable, refreshBtn);
        return tableContainer;
    }
    private void showEditQuizDialog(Quiz quiz) {
        Dialog<Quiz> dialog = new Dialog<>();
        dialog.setTitle("Modifier Quiz");
        dialog.setHeaderText("Modifier les détails du quiz");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nomField = new TextField(quiz.getNomQuiz());
        TextArea descriptionArea = new TextArea(quiz.getDescriptionQuiz());
        descriptionArea.setPrefRowCount(3);

        // Garder l'ancien nombre de questions pour comparaison
        int oldQuestionCount = quiz.getNombreQuestions();
        ComboBox<Integer> nbQuestionsCombo = new ComboBox<>(FXCollections.observableArrayList(2, 3, 4, 5, 6, 7, 8, 9, 10));
        nbQuestionsCombo.setValue(oldQuestionCount);

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);
        grid.add(new Label("Nombre de questions:"), 0, 2);
        grid.add(nbQuestionsCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                quiz.setNomQuiz(nomField.getText());
                quiz.setDescriptionQuiz(descriptionArea.getText());
                quiz.setNombreQuestions(nbQuestionsCombo.getValue());
                return quiz;
            }
            return null;
        });

        Optional<Quiz> result = dialog.showAndWait();
        result.ifPresent(updatedQuiz -> {
            try {
                QuizService quizService = new QuizService();
                quizService.updateQuiz(updatedQuiz, updatedQuiz.getId());

                // Gestion du changement du nombre de questions
                int newQuestionCount = updatedQuiz.getNombreQuestions();
                if (newQuestionCount != oldQuestionCount) {
                    handleQuestionCountChange(quiz.getId(), oldQuestionCount, newQuestionCount);
                }

                refreshQuizTable();
                showAlert("Succès", "Quiz mis à jour avec succès", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Échec de la mise à jour: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }
    private void handleQuestionCountChange(int quizId, int oldCount, int newCount) {
        QuestionService questionService = new QuestionService();

        if (newCount > oldCount) {
            // Ajout de nouvelles questions
            for (int i = oldCount; i < newCount; i++) {
                Question newQuestion = new Question();
                newQuestion.setQuizId(quizId);
                newQuestion.setContenuQuestion("Nouvelle question " + (i + 1));
                newQuestion.setNombreOptions(4); // Valeur par défaut
                questionService.addQuestion(newQuestion);
            }
        } else if (newCount < oldCount) {
            // Suppression de questions - demande confirmation
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Suppression de questions");
            confirm.setContentText("Voulez-vous vraiment supprimer " + (oldCount - newCount) + " question(s)?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    // Récupérer toutes les questions et supprimer les dernières
                    List<Question> questions = questionService.getAllQuestionsByQuizId(quizId);
                    for (int i = newCount; i < oldCount; i++) {
                        questionService.deleteQuestion(questions.get(i).getId());
                    }
                } catch (Exception e) {
                    showAlert("Erreur", "Erreur lors de la suppression des questions: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        }
    }
    private void showDeleteConfirmation(Quiz quiz) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le quiz: " + quiz.getNomQuiz());
        alert.setContentText("Cette action supprimera également toutes les questions et options associées. Continuer?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // D'abord supprimer toutes les questions (et leurs options)
                QuestionService questionService = new QuestionService();
                List<Question> questions = questionService.getAllQuestionsByQuizId(quiz.getId());

                OptionService optionService = new OptionService();
                for (Question question : questions) {
                    // Supprimer toutes les options de cette question
                    optionService.deleteOptionsByQuestionId(question.getId());
                    // Puis supprimer la question
                    questionService.deleteQuestion(question.getId());
                }

                // Enfin supprimer le quiz
                QuizService quizService = new QuizService();
                quizService.deleteQuiz(quiz.getId());

                refreshQuizTable();
                showAlert("Succès", "Quiz et toutes ses questions/options ont été supprimés", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Échec de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }



    private void refreshQuizTable() {
        try {
            QuizService quizService = new QuizService();
            quizTable.setItems(FXCollections.observableArrayList(quizService.getAllQuizzes()));
            quizTable.refresh(); // Rafraîchit l'affichage du tableau
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du rafraîchissement: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private void handleQuizCreation(TextField nomQuizField, TextArea descriptionField, ComboBox<Integer> nbQuestionsCombo) {
        try {
            if (nomQuizField.getText().isEmpty() || descriptionField.getText().isEmpty() || nbQuestionsCombo.getValue() == null) {
                showAlert("Champs manquants", "Veuillez remplir tous les champs obligatoires!", Alert.AlertType.WARNING);
                return;
            }

            if (Character.isDigit(nomQuizField.getText().charAt(0))) {
                showAlert("Format incorrect", "Le nom ne peut pas commencer par un chiffre!", Alert.AlertType.WARNING);
                return;
            }

            if (Character.isDigit(descriptionField.getText().charAt(0))) {
                showAlert("Format incorrect", "La description ne peut pas commencer par un chiffre!", Alert.AlertType.WARNING);
                return;
            }

            Quiz newQuiz = new Quiz();
            newQuiz.setNomQuiz(nomQuizField.getText());
            newQuiz.setDescriptionQuiz(descriptionField.getText());
            newQuiz.setNombreQuestions(nbQuestionsCombo.getValue());

            QuizService quizService = new QuizService();
            ConsoleOutputCapturer capturer = new ConsoleOutputCapturer();
            capturer.start();
            quizService.addQuiz(newQuiz);
            String serviceOutput = capturer.stop();

            if (serviceOutput.contains("existe déjà")) {
                showAlert("Erreur", "Ce quiz existe déjà!", Alert.AlertType.ERROR);
            } else if (serviceOutput.contains("Erreur")) {
                showAlert("Erreur", "Erreur technique", Alert.AlertType.ERROR);
            } else {
                showAlert("Succès", "Quiz créé avec succès!", Alert.AlertType.INFORMATION);

                // Récupérer l'ID du quiz nouvellement créé
                int quizId = getLastInsertedQuizId();

                // Ouvrir la fenêtre de gestion des questions
                Stage questionStage = new Stage();
                GestionJeu_QUESTION questionApp = new GestionJeu_QUESTION(quizId, nbQuestionsCombo.getValue());
                questionApp.start(questionStage);

                // Fermer la fenêtre actuelle
                ((Stage) nomQuizField.getScene().getWindow()).close();


                nomQuizField.clear();
                descriptionField.clear();
                nbQuestionsCombo.getSelectionModel().clearSelection();
            }
        } catch (Exception ex) {
            showAlert("Erreur", "Erreur inattendue: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private int getLastInsertedQuizId() throws SQLException {
        String query = "SELECT LAST_INSERT_ID()";
        Statement st = MyConnection.getInstance().getCnx().createStatement();
        ResultSet rs = st.executeQuery(query);
        if (rs.next()) {
            return rs.getInt(1);
        }
        return -1;
    }


    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private static class ConsoleOutputCapturer {
        private ByteArrayOutputStream baos;
        private PrintStream original;

        public void start() {
            baos = new ByteArrayOutputStream();
            original = System.out;
            System.setOut(new PrintStream(baos));
        }

        public String stop() {
            System.setOut(original);
            return baos.toString();
        }
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
