package bomberman;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Classe représentant la fenêtre des options du jeu Bomberman.
 * Permet notamment d’activer un style alternatif via une checkbox.
 */
public class option extends Application {

    // Dimensions de la fenêtre
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    // Conteneurs principaux
    private StackPane root;
    private VBox mainContainer;

    // Images pour le fond et le titre
    private ImageView backgroundImage;
    private ImageView titleImage;

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
        centerBox.setPrefHeight(WINDOW_HEIGHT - 300); // hauteur ajustée pour tenir compte du titre
        centerBox.setMinHeight(200);

        VBox optionBox = new VBox(20); // espacement vertical entre éléments
        optionBox.setAlignment(Pos.CENTER);
        optionBox.setPadding(new Insets(30));
        optionBox.setMaxWidth(300);

        // Fond avec couleur et coins arrondis pour la boîte d'options
        optionBox.setBackground(new Background(new BackgroundFill(
                Color.valueOf("#00A0A0"),
                new CornerRadii(10),
                Insets.EMPTY
        )));

        // Checkbox pour activer/désactiver le style alternatif
        CheckBox styleCheckBox = new CheckBox("Activer style alternatif");
        styleCheckBox.setSelected(Settings.alternativeStyle); // état initial selon Settings
        styleCheckBox.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

        // Action lors du changement d’état de la checkbox
        styleCheckBox.setOnAction(e -> {
            Settings.alternativeStyle = styleCheckBox.isSelected();
            System.out.println("alternativeStyle = " + Settings.alternativeStyle);
        });

        // Bouton "Retour" pour revenir au menu principal
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

        // Style au survol
        backButton.setOnMouseEntered(e -> backButton.setStyle(
                "-fx-background-color: #006666; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-min-width: 120px; " +
                        "-fx-min-height: 40px; " +
                        "-fx-cursor: hand;"
        ));

        // Retour au style par défaut quand la souris quitte
        backButton.setOnMouseExited(e -> backButton.setStyle(
                "-fx-background-color: #008080; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-min-width: 120px; " +
                        "-fx-min-height: 40px; " +
                        "-fx-cursor: hand;"
        ));

        // Action du bouton retour : revenir au menu principal
        backButton.setOnAction(e -> {
            MainMenu mainMenu = new MainMenu();
            try {
                mainMenu.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Ajout de la checkbox et du bouton retour dans la boîte d’options
        optionBox.getChildren().addAll(styleCheckBox, backButton);
        centerBox.getChildren().add(optionBox);

        // Ajout du contenu principal dans le conteneur principal
        mainContainer.getChildren().add(centerBox);
    }

    /**
     * Classe interne statique pour gérer les paramètres globaux des options.
     */
    public static class Settings {
        public static boolean alternativeStyle = false; // option style alternatif.
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
