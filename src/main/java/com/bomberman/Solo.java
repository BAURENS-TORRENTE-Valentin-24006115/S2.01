package com.bomberman;

// Importation des bibliothèques JavaFX nécessaires
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.application.Platform;
import javafx.util.Duration;

/**
 * Classe représentant le mode solo du jeu Bomberman.
 * <p>
 * Permet de lancer une partie en solo avec différents niveaux de difficulté (Easy, Difficile, Xtreme).
 * Gère l'affichage du titre, du fond et des boutons avec styles et animations.
 * </p>
 * @author Valentin B.
 */
public class Solo extends Application {

    // Largeur et hauteur de la fenêtre principale
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    // Conteneurs de la scène
    private StackPane root;
    private VBox menuContainer;

    // Images de fond et du titre
    private ImageView backgroundImage;
    private ImageView titleImage;

    /**
     * Point d'entrée de l'application JavaFX. Crée et affiche l'interface du mode solo.
     *
     * @param primaryStage la fenêtre principale de l'application
     */
    @Override
    public void start(Stage primaryStage) {
        // Initialisation des conteneurs
        initializeContainers();
        // Ajout de l'image de fond
        setBackground();
        // Ajout du logo ou titre du jeu
        createTitle();
        // Création des boutons et configuration de leurs actions
        createSliderWithLabels(primaryStage);
        addRetourButton(primaryStage);      // ajoute le bouton retour en bas à gauche

        // Création et affichage de la scène
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setTitle("Bomberman");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // empêche le redimensionnement de la fenêtre
        primaryStage.show(); // affichage de la fenêtre
    }

    /**
     * Initialise les conteneurs principaux de l'interface.
     * Crée un StackPane pour le conteneur racine et un VBox pour le menu.
     */
    private void initializeContainers() {
        root = new StackPane(); // conteneur racine

        menuContainer = new VBox(30); // espacement vertical entre les éléments
        menuContainer.setAlignment(Pos.TOP_CENTER); // alignement en haut au centre
        menuContainer.setMaxWidth(300); // largeur maximale du menu
        menuContainer.setTranslateY(80); // décalage vertical
    }

    /**
     * Crée un slider de difficulté avec des labels dynamiques et des boutons pour lancer le jeu.
     * Le slider permet de choisir la difficulté et met à jour un label en fonction de la valeur sélectionnée.
     *
     * @param primaryStage la fenêtre principale de l'application
     */
    private void createSliderWithLabels(Stage primaryStage) {
        // Boîte principale avec fond coloré et coins arrondis
        VBox blueBox = new VBox(18);
        blueBox.setAlignment(Pos.CENTER);
        blueBox.setPadding(new Insets(25, 30, 25, 30));
        blueBox.setMaxWidth(420);
        blueBox.setBackground(new javafx.scene.layout.Background(
                new javafx.scene.layout.BackgroundFill(
                        javafx.scene.paint.Color.valueOf("#00A0A0"),
                        new javafx.scene.layout.CornerRadii(12),
                        Insets.EMPTY
                )
        ));
        blueBox.setStyle("-fx-effect: dropshadow(gaussian, #222, 10, 0.2, 0, 2);");

        // Label dynamique pour la difficulté
        Label difficultyLabel = new Label();
        difficultyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        // Slider de difficulté
        Slider difficultySlider = new Slider(0, 500, 250);
        difficultySlider.setShowTickLabels(false);
        difficultySlider.setShowTickMarks(true);
        difficultySlider.setMajorTickUnit(100);
        difficultySlider.setMinorTickCount(9); // 10 graduations par 100
        difficultySlider.setBlockIncrement(10);
        difficultySlider.setPrefWidth(320);
        difficultySlider.setStyle("-fx-control-inner-background: #008080;");
        difficultySlider.setSnapToTicks(true); // pour forcer les pas de 10

        // Met à jour le label selon la valeur du slider
        difficultySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = newVal.intValue();
            if (value > 350) {
                difficultyLabel.setText("Facile (" + value + ")");
            } else if (value > 250) {
                difficultyLabel.setText("Moyen (" + value + ")");
            } else if (value > 150) {
                difficultyLabel.setText("Difficile (" + value + ")");
            } else if (value > 50) {
                difficultyLabel.setText("Fou (" + value + ")");
            } else if (value > 1) {
                difficultyLabel.setText("Impossible (" + value + ")");
            } else {
                difficultyLabel.setText("Omnipotent (0)");
            }
            double opacity = 0.2 + 0.5 * (1 - value / 500.0);
            backgroundOverlay.setBackground(new javafx.scene.layout.Background(
                    new javafx.scene.layout.BackgroundFill(
                            javafx.scene.paint.Color.rgb(0, 0, 0, opacity),
                            null, null
                    )
            ));
        });
        difficultySlider.setValue(500);

        // Labels fixes pour chaque plage
        HBox labels = new HBox(10);
        labels.setAlignment(Pos.CENTER);
        labels.getChildren().addAll(
                createDifficultyRangeLabel("Impossible\n0-50"),
                createDifficultyRangeLabel("Fou\n50-150"),
                createDifficultyRangeLabel("Difficile\n150-250"),
                createDifficultyRangeLabel("Moyen\n250-350"),
                createDifficultyRangeLabel("Facile\n350-500")
        );

        // Bouton lancer
        Button launchButton = createMenuButton("Lancer");
        launchButton.setStyle(
                "-fx-background-color: #008080; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 30; " +
                        "-fx-background-radius: 5;"
        );
        launchButton.setOnMouseEntered(e ->
                launchButton.setStyle(
                        "-fx-background-color: #006666; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 18px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 10 30; " +
                                "-fx-background-radius: 5;"
                )
        );
        launchButton.setOnMouseExited(e ->
                launchButton.setStyle(
                        "-fx-background-color: #008080; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 18px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 10 30; " +
                                "-fx-background-radius: 5;"
                )
        );
        launchButton.setOnAction(e -> {
            int delay = (int) difficultySlider.getValue();
            launchSoloWithBotDelay(delay, primaryStage);
        });

        // Ajout des éléments à la boîte bleue
        blueBox.getChildren().addAll(
                difficultyLabel,
                difficultySlider,
                labels,
                launchButton
        );

        // Wrapper pour centrer la boîte bleue dans le menu
        VBox wrapper = new VBox(blueBox);
        wrapper.setAlignment(Pos.TOP_CENTER);
        wrapper.setPadding(new Insets(30, 0, 0, 0));

        menuContainer.getChildren().add(wrapper);
    }



    /*     * Crée un label pour afficher les plages de difficulté.
     * Chaque label est stylisé pour être centré et en gras.
     *
     * @param text le texte à afficher dans le label
     * @return le label JavaFX stylisé
     */
    private Label createDifficultyRangeLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-alignment: center");
        label.setAlignment(Pos.CENTER);
        return label;
    }

    /*
     * Crée les boutons du menu avec leurs actions respectives.
     * Chaque bouton lance une partie en solo avec un délai différent pour les bots.
     *
     * @param primaryStage la fenêtre principale de l'application
     */
    private void createButtons(Stage primaryStage) {
        VBox buttonBox = new VBox(20); // conteneur vertical pour les boutons
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setTranslateY(30);

        // Création des boutons avec styles
        Button easyButton = createMenuButton("Facile");
        Button hardButton = createMenuButton("Difficile");
        Button xtremeButton = createMenuButton("Xtreme");

        // Action pour le bouton "easy"
        easyButton.setOnAction(e -> {
            launchSoloWithBotDelay(300, primaryStage);
        });

        // Action pour le bouton "hard"
        hardButton.setOnAction(e -> {
            launchSoloWithBotDelay(200, primaryStage);
        });

        // Action pour le bouton "Xtreme"
        xtremeButton.setOnAction(e -> {
            launchSoloWithBotDelay(0, primaryStage);
        });

        // Ajout de tous les boutons au conteneur
        buttonBox.getChildren().addAll(easyButton, hardButton, xtremeButton);
        menuContainer.getChildren().add(buttonBox);
    }

    /**
     * Ajoute un bouton "Retour" en bas à gauche de la fenêtre.
     * Ce bouton permet de revenir au menu principal.
     *
     * @param stage la fenêtre principale de l'application
     */
    private void addRetourButton(Stage stage) {
        Button retourBtn = new Button("<- Retour");
        retourBtn.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold;"
        );

        // Positionne le bouton en bas à gauche de la fenêtre
        StackPane.setAlignment(retourBtn, Pos.BOTTOM_LEFT);
        StackPane.setMargin(retourBtn, new Insets(10));

        // Action du bouton retour : revient au menu principal.
        retourBtn.setOnAction(e -> {
            try {
                MainMenu mainMenu = new MainMenu();
                mainMenu.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        root.getChildren().add(retourBtn);
    }

    /**
     * Crée un bouton stylisé avec effets de survol et animation d’agrandissement.
     *
     * @param text texte à afficher sur le bouton
     * @return le bouton JavaFX prêt à être affiché
     */
    private Button createMenuButton(String text) {
        Button button = new Button(text);

        // Style par défaut
        String defaultStyle = "-fx-background-color: #00A0A0; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-min-width: 200px; " +
                "-fx-min-height: 40px; " +
                "-fx-cursor: hand;";

        // Style au survol
        String hoverStyle = "-fx-background-color: #008080; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-min-width: 200px; " +
                "-fx-min-height: 40px; " +
                "-fx-cursor: hand;";

        button.setStyle(defaultStyle); // appliquer le style par défaut

        // Animation à l'entrée de la souris (zoom)
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), button);
        scaleIn.setToX(1.1);
        scaleIn.setToY(1.1);
        scaleIn.setAutoReverse(false);

        // Animation à la sortie de la souris (retour à l'échelle normale)
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), button);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);
        scaleOut.setAutoReverse(false);

        // Événements de survol
        button.setOnMouseEntered(e -> {
            button.setStyle(hoverStyle);
            scaleIn.playFromStart();
        });

        button.setOnMouseExited(e -> {
            button.setStyle(defaultStyle);
            scaleOut.playFromStart();
        });

        return button;
    }

    /**
     * Lance une partie en mode solo avec un délai spécifique pour les mouvements des bots.
     *
     * @param botDelayMs le délai en millisecondes pour les mouvements des bots
     * @param stage la fenêtre principale de l'application
     */
    public static void launchSoloWithBotDelay(int botDelayMs, Stage stage) {
        javafx.application.Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(Solo.class.getResource("/BombermanGame.fxml"));
                Parent root = loader.load();
                BombermanGame controller = loader.getController();
                controller.setAlternativeStyle(Option.Settings.alternativeStyle);
                controller.enableSoloMode();
                controller.setBotMoveDelay(botDelayMs); // <-- Ajoutez cette ligne

                Scene scene = new Scene(root, 800, 900);
                scene.getStylesheets().add(Solo.class.getResource("/bomberman.css").toExternalForm());
                Stage gameStage = new Stage();
                gameStage.setTitle("Super Bomberman - Mode Solo");
                gameStage.setScene(scene);
                gameStage.setResizable(false);
                gameStage.centerOnScreen();
                root.requestFocus();
                gameStage.show();
                stage.hide();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * Crée et ajoute l'image de titre/logo en haut du menu.
     */
    private void createTitle() {
        try {
            // Chargement de l'image du titre
            Image logo = new Image(getClass().getResourceAsStream("/images/bomberpx.png"));
            titleImage = new ImageView(logo);

            titleImage.setFitWidth(400);
            titleImage.setFitHeight(200);
            titleImage.setPreserveRatio(true); // conserve le ratio d'origine

            menuContainer.getChildren().add(titleImage); // ajout au menu
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du logo : " + e.getMessage());
        }
    }

    /**
     * Définit l'image de fond de la scène principale.
     */
    // Ajoutez ce champ dans la classe Solo
    private javafx.scene.layout.Region backgroundOverlay;

    private void setBackground() {
        try {
            Image image = new Image(getClass().getResourceAsStream("/images/imgfond.jpg"));
            backgroundImage = new ImageView(image);
            backgroundImage.setFitWidth(WINDOW_WIDTH);
            backgroundImage.setFitHeight(WINDOW_HEIGHT);
            backgroundImage.setPreserveRatio(false);

            // Calque d’assombrissement
            backgroundOverlay = new javafx.scene.layout.Region();
            backgroundOverlay.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            backgroundOverlay.setBackground(new javafx.scene.layout.Background(
                    new javafx.scene.layout.BackgroundFill(
                            javafx.scene.paint.Color.rgb(0, 0, 0, 0.2), // opacité initiale
                            null, null
                    )
            ));

            root.getChildren().addAll(backgroundImage, backgroundOverlay, menuContainer);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image de fond : " + e.getMessage());
        }
    }

    /**
     * Méthode main pour lancer l'application JavaFX.
     *
     * @param args arguments de la ligne de commande (non utilisés ici).
     */
    public static void main(String[] args) {
        launch(args);
    }
}
