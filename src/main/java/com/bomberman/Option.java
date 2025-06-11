package com.bomberman;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Classe représentant la fenêtre des options du jeu Bomberman.
 * Permet notamment d’activer un style alternatif via une checkbox.
 */
public class Option extends Application {

    private Stage primaryStage;

    // Dimensions de la fenêtre
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    // Conteneurs principaux
    private StackPane root;
    private VBox mainContainer;

    // Images pour le fond et le titre
    private ImageView backgroundImage;
    private ImageView titleImage;

    public static class Settings {
        public static boolean alternativeStyle = false; // option style alternatif.
        public static boolean soundEnabled = true; // option son activé par défaut
        public static int aiDifficulty = BotAI.AI_SPEED_NORMAL; // difficulté par défaut
    }

    /**
     * Point d'entrée JavaFX qui initialise et affiche la fenêtre des options.
     *
     * @param stage la fenêtre principale de l'application
     */
    @Override
    public void start(Stage stage) {
        initializeContainers(); // initialise les conteneurs
        setBackground();        // configure l'image de fond
        createTitle();          // ajoute le titre/image d’en-tête
        setupContent(stage);    // ajoute le contenu des options (checkbox, bouton retour)

        // Création et affichage de la scène
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Options");
        stage.setResizable(false); // fenêtre non redimensionnable
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Initialise les conteneurs root et mainContainer.
     */
    private void initializeContainers() {
        root = new StackPane();
        mainContainer = new VBox();
        mainContainer.setAlignment(Pos.TOP_CENTER);
        root.getChildren().add(mainContainer); // ajoute mainContainer à la racine
    }

    /**
     * Configure l’image de fond de la fenêtre.
     */
    private void setBackground() {
        try {
            Image image = new Image(getClass().getResourceAsStream("/images/imgfond.jpg"));
            backgroundImage = new ImageView(image);

            backgroundImage.setFitWidth(WINDOW_WIDTH);
            backgroundImage.setFitHeight(WINDOW_HEIGHT);
            backgroundImage.setPreserveRatio(false); // étirement total

            // Ajout du fond en première position dans la pile
            root.getChildren().add(0, backgroundImage);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image de fond : " + e.getMessage());
        }
    }

    /**
     * Crée et ajoute le titre (image) en haut de la fenêtre.
     */
    private void createTitle() {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/images/Options.png")); // image titre
            titleImage = new ImageView(logo);

            titleImage.setFitWidth(400);
            titleImage.setFitHeight(200);
            titleImage.setPreserveRatio(true);

            VBox titleBox = new VBox(titleImage);
            titleBox.setAlignment(Pos.TOP_CENTER);
            titleBox.setPadding(new Insets(20, 0, 20, 0)); // marge en haut et en bas

            mainContainer.getChildren().add(titleBox);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du titre : " + e.getMessage());
        }
    }

    /**
     * Configure le contenu principal de la fenêtre (checkbox style alternatif et bouton retour).
     *
     * @param stage la fenêtre sur laquelle les actions pourront agir (retour au menu principal)
     */

    private void setupContent(Stage stage) {
        VBox centerBox = new VBox();
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPrefHeight(WINDOW_HEIGHT - 300);
        centerBox.setMinHeight(200);

        VBox optionBox = new VBox(20);
        optionBox.setAlignment(Pos.CENTER);
        optionBox.setPadding(new Insets(30));
        optionBox.setMaxWidth(300);

        optionBox.setBackground(new Background(new BackgroundFill(
                Color.valueOf("#00A0A0"),
                new CornerRadii(10),
                Insets.EMPTY
        )));

        CheckBox styleCheckBox = new CheckBox("Activer style alternatif");
        styleCheckBox.setSelected(Settings.alternativeStyle);
        styleCheckBox.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

        styleCheckBox.setOnAction(e -> {
            Settings.alternativeStyle = styleCheckBox.isSelected();
            System.out.println("alternativeStyle = " + Settings.alternativeStyle);
        });

        CheckBox soundCheckBox = new CheckBox("Activer la musique");
        soundCheckBox.setSelected(Settings.soundEnabled);
        soundCheckBox.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

        soundCheckBox.setOnAction(e -> {
            Settings.soundEnabled = soundCheckBox.isSelected();
            System.out.println("soundEnabled = " + Settings.soundEnabled);
        });

        Button backButton = new Button("Retour");
        backButton.setStyle(
                "-fx-background-color: #008080; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-min-width: 120px; " +
                        "-fx-min-height: 40px; " +
                        "-fx-cursor: hand;"
        );

        backButton.setOnMouseEntered(e -> backButton.setStyle(
                "-fx-background-color: #006666; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-min-width: 120px; " +
                        "-fx-min-height: 40px; " +
                        "-fx-cursor: hand;"
        ));

        backButton.setOnMouseExited(e -> backButton.setStyle(
                "-fx-background-color: #008080; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-min-width: 120px; " +
                        "-fx-min-height: 40px; " +
                        "-fx-cursor: hand;"
        ));

        backButton.setOnAction(e -> {
            MainMenu mainMenu = new MainMenu();
            try {
                mainMenu.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Ajouter la section de difficulté créée par la méthode
        VBox difficultyBox = createDifficultyOption();

        // Ajout de tous les éléments dans la boîte d'options
        optionBox.getChildren().addAll(styleCheckBox, soundCheckBox, difficultyBox, backButton);
        centerBox.getChildren().add(optionBox);

        mainContainer.getChildren().add(centerBox);
    }

    /**
     * Crée les options de difficulté pour l'IA
     *
     * @return un conteneur avec les options de difficulté
     */
    private VBox createDifficultyOption() {
        Label difficultyLabel = new Label("Difficulté des IA");
        difficultyLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Groupe pour les boutons radio de difficulté
        ToggleGroup difficultyGroup = new ToggleGroup();

        RadioButton easyDifficulty = new RadioButton("Baby Mode");
        easyDifficulty.setToggleGroup(difficultyGroup);
        easyDifficulty.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        easyDifficulty.setSelected(Settings.aiDifficulty == BotAI.AI_SPEED_BABY);

        RadioButton normalDifficulty = new RadioButton("Normal");
        normalDifficulty.setToggleGroup(difficultyGroup);
        normalDifficulty.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        normalDifficulty.setSelected(Settings.aiDifficulty == BotAI.AI_SPEED_NORMAL);

        RadioButton hardDifficulty = new RadioButton("Real Man Mode");
        hardDifficulty.setToggleGroup(difficultyGroup);
        hardDifficulty.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        hardDifficulty.setSelected(Settings.aiDifficulty == BotAI.AI_SPEED_REALMAN);

        // Écouter les changements
        easyDifficulty.setOnAction(e -> Settings.aiDifficulty = BotAI.AI_SPEED_BABY);
        normalDifficulty.setOnAction(e -> Settings.aiDifficulty = BotAI.AI_SPEED_NORMAL);
        hardDifficulty.setOnAction(e -> Settings.aiDifficulty = BotAI.AI_SPEED_REALMAN);

        VBox difficultyBox = new VBox(10, difficultyLabel, easyDifficulty, normalDifficulty, hardDifficulty);
        difficultyBox.setStyle("-fx-padding: 10; -fx-background-color: rgba(0,0,0,0.2); -fx-background-radius: 5;");
        return difficultyBox;
    }

    /**
     * Méthode main pour lancer l'application JavaFX.
     *
     * @param args arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        launch(args);
    }
}