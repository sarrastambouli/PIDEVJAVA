package frontend;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.layout.StackPane;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.animation.RotateTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

public class GestionFeedback_ADMIN extends Application {

    private boolean isNavBarOpen = true;
    private VBox sideNav;
    private final Color PRIMARY_COLOR = Color.web("#3f51b5");
    private final Color MENUCOLOR = Color.web("#BECCE4");
    private final Color SECONDARY_COLOR = Color.web("#f5f5f5");
    private final Color ACCENT_COLOR = Color.web("#ff5722");
    private final Color HOVER_COLOR = Color.web("#3949ab");
    private final Color SELECTED_COLOR = Color.web("#1a237e");
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

        ImageView userIcon = new ImageView(new Image("/Images/chourouk.png"));
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

        Tooltip userTooltip = new Tooltip("Admin - Gestion des feedbacks");
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
        Button btnTheme = createNavButton("Feedback", "/Images/feedback.png");


        Button btnJeux = createNavButton("Réclamations", "/Images/reclamation.png");

        sideNav.getChildren().addAll(bntHome, btnTheme,  btnJeux);
        return sideNav;
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
                GestionFeedback_ADMIN adminApp = new GestionFeedback_ADMIN();

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
        if (text.equals("feedback")) {
            button.setOnAction(e -> {
                Stage feedStage = new Stage();
                GestionFeedback_FEEDBACKADMIN feedApp = new GestionFeedback_FEEDBACKADMIN();
                try {
                    feedApp.start(feedStage);
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
        VBox contentBox = new VBox(20);
        contentBox.setStyle("-fx-background-color: white;");
        contentBox.setPadding(new Insets(20));
        contentBox.setAlignment(Pos.TOP_CENTER);

        Label welcomeLabel = new Label("Bienvenue dans le Dashboard EDUCARE");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + toHex(PRIMARY_COLOR) + ";");

        contentBox.getChildren().add(welcomeLabel);
        return contentBox;
    }

    private void showUserMenu(Button userBtn) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;");

        MenuItem adminAccount = new MenuItem("Bienvenue GATRI Chourouk !");
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
