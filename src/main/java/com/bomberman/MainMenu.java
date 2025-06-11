package com.bomberman;

// Importation des bibliothèques JavaFX nécessaires
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
 * Classe représentant le menu principal du jeu Bomberman.
 * <p>
 * Permet d'accéder aux modes de jeu (solo, multijoueur), aux options et de quitter le jeu.
 * Gère l'affichage du titre, du fond et des boutons avec styles et animations.
 * </p>
 * @author Valentin B. - Akim A.
 */
public class MainMenu extends Application {

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
     * Point d'entrée de l'application JavaFX. Crée et affiche l'interface du menu principal.
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
        createButtons(primaryStage);

        // Création et affichage de la scène
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setTitle("Bomberman");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // empêche le redimensionnement de la fenêtre
        primaryStage.show(); // affichage de la fenêtre
    }

    /**
     * Initialise les conteneurs principaux de l'interface.
     */
    private void initializeContainers() {
        root = new StackPane(); // conteneur racine

        menuContainer = new VBox(30); // espacement vertical entre les éléments
        menuContainer.setAlignment(Pos.TOP_CENTER); // alignement en haut au centre
        menuContainer.setMaxWidth(300); // largeur maximale du menu
        menuContainer.setTranslateY(80); // décalage vertical
    }

    /**
     * Crée les boutons du menu et définit leur comportement au clic.
     *
     * @param primaryStage la scène principale à manipuler (masquer, etc.)
     */
    private void createButtons(Stage primaryStage) {
        VBox buttonBox = new VBox(20); // conteneur vertical pour les boutons
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setTranslateY(30);

        // Création des boutons avec styles
        Button multiplayerButton = createMenuButton("MULTIJOUEUR");
        Button soloButton = createMenuButton("SOLO");
        Button optionsButton = createMenuButton("OPTIONS");
        Button exitButton = createMenuButton("EXIT");

        // Action pour le bouton "MULTIJOUEUR"
        multiplayerButton.setOnAction(e -> {
            Multijoueur playerSelection = new Multijoueur();
            Stage playerSelectionStage = new Stage();
            playerSelection.start(playerSelectionStage); // lance la scène multijoueur
            primaryStage.hide(); // masque le menu principal
        });

        // Action pour le bouton "SOLO"
        soloButton.setOnAction(e -> {
            try {
                // Charger le fichier FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/BombermanGame.fxml"));
                Parent root = loader.load();

                // Récupérer le contrôleur et activer le mode solo
                BombermanGame controller = loader.getController();
                // Appliquer le style alternatif si activé dans les options
                controller.setAlternativeStyle(Option.Settings.alternativeStyle);
                controller.enableSoloMode();


                // Créer une nouvelle scène
                Scene scene = new Scene(root, 800, 900);

                // Ajouter le CSS
                scene.getStylesheets().add(getClass().getResource("/bomberman.css").toExternalForm());

                // Configurer la nouvelle fenêtre
                Stage gameStage = new Stage();
                gameStage.setTitle("Super Bomberman - Mode Solo");
                gameStage.setScene(scene);
                gameStage.setResizable(false);
                gameStage.centerOnScreen();

                // Donner le focus pour les contrôles clavier
                root.requestFocus();

                // Afficher le jeu et cacher le menu
                gameStage.show();
                primaryStage.hide();

                System.out.println("Mode solo lancé");
            } catch (Exception ex) {
                System.err.println("Erreur lors du lancement du mode solo: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Action pour le bouton "OPTIONS"
        optionsButton.setOnAction(e -> {
            Option optionsPage = new Option(); // classe option (attention à la casse)
            Stage optionsStage = new Stage();
            optionsPage.start(optionsStage); // lance la scène des options
            primaryStage.hide(); // masque le menu principal
        });

        // Action pour quitter le jeu
        exitButton.setOnAction(e -> Platform.exit());

        // Ajout de tous les boutons au conteneur
        buttonBox.getChildren().addAll(multiplayerButton, soloButton, optionsButton, exitButton);
        menuContainer.getChildren().add(buttonBox);
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
    private void setBackground() {
        try {
            // Chargement de l’image de fond
            Image image = new Image(getClass().getResourceAsStream("/images/imgfond.jpg"));
            backgroundImage = new ImageView(image);

            backgroundImage.setFitWidth(WINDOW_WIDTH);
            backgroundImage.setFitHeight(WINDOW_HEIGHT);
            backgroundImage.setPreserveRatio(false); // s'étire pour couvrir toute la scène

            // Ajout dans le bon ordre : fond en premier, menu par-dessus
            root.getChildren().add(backgroundImage);
            root.getChildren().add(menuContainer);
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
