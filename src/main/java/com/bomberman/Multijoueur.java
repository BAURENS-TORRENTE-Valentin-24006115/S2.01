package com.bomberman;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;

import java.util.HashSet;
import java.util.Set;

/**
 * Classe représentant la fenêtre de sélection des joueurs en mode multijoueur.
 * Permet aux joueurs d’entrer leur pseudo, valide les doublons et propose un bouton retour au menu principal.
 */
public class Multijoueur extends Application {

    // Dimensions de la fenêtre
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    // Conteneurs principaux et éléments UI
    private StackPane root;
    private VBox contentContainer;
    private ImageView backgroundImage;
    private ImageView titleImage;
    private Label errorLabel;     // Pour afficher les erreurs de saisie (doublons)
    private Stage stage;          // Référence au stage principale pour navigation

    /**
     * Méthode principale JavaFX appelée au lancement.
     * Initialise la fenêtre, les conteneurs, le fond, le titre, le contenu et le bouton retour.
     *
     * @param stage fenêtre principale JavaFX
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage; // sauvegarde la référence au stage pour pouvoir y revenir

        initializeContainers(); // initialise les conteneurs root et contentContainer
        setBackground();        // configure le fond d’écran
        createTitle();          // crée l’image titre
        setupContent();         // ajoute les champs de saisie pour les joueurs et bouton confirmer
        addRetourButton();      // ajoute le bouton retour en bas à gauche

        // Création et affichage de la scène principale
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Sélection des Joueurs");
        stage.setResizable(false);  // fenêtre fixe
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Initialise les conteneurs racine root (StackPane) et contentContainer (VBox).
     */
    private void initializeContainers() {
        root = new StackPane();

        contentContainer = new VBox(20);  // espacement vertical de 20px entre enfants
        contentContainer.setAlignment(Pos.TOP_CENTER);
        contentContainer.setMaxWidth(600);
        contentContainer.setPadding(new Insets(30, 0, 0, 0)); // marge intérieure haute
    }

    /**
     * Charge et ajoute l’image de fond à la racine.
     */
    private void setBackground() {
        try {
            Image image = new Image(getClass().getResourceAsStream("/images/imgfond.jpg"));
            backgroundImage = new ImageView(image);

            backgroundImage.setFitWidth(WINDOW_WIDTH);
            backgroundImage.setFitHeight(WINDOW_HEIGHT);
            backgroundImage.setPreserveRatio(false); // pas de ratio conservé, étire

            root.getChildren().add(backgroundImage);    // fond en arrière-plan
            root.getChildren().add(contentContainer);  // contenu au-dessus
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image de fond : " + e.getMessage());
        }
    }

    /**
     * Charge et affiche l’image du titre (pseudo.png) en haut de la fenêtre.
     */
    private void createTitle() {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/images/pseudo.png"));
            titleImage = new ImageView(logo);

            titleImage.setFitWidth(400);
            titleImage.setFitHeight(200);
            titleImage.setPreserveRatio(true);

            VBox titleContainer = new VBox(titleImage);
            titleContainer.setAlignment(Pos.TOP_CENTER);
            titleContainer.setPadding(new Insets(10, 0, 40, 0)); // marge interne

            contentContainer.getChildren().add(titleContainer);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du logo : " + e.getMessage());
        }
    }

    /**
     * Crée une ligne (HBox) pour un joueur avec un label "JOUEUR N :" et un champ TextField pour le pseudo.
     *
     * @param playerNumber numéro du joueur (1, 2, 3, 4)
     * @param fields tableau où stocker la référence au TextField
     * @param index index dans le tableau fields pour stocker ce TextField
     * @return HBox contenant le label et le champ de saisie
     */
    private HBox createPlayerRow(String playerNumber, TextField[] fields, int index) {
        HBox playerRow = new HBox(15);  // espacement horizontal 15px
        playerRow.setAlignment(Pos.CENTER_LEFT);
        playerRow.setPadding(new Insets(5, 5, 5, 20)); // marge interne
        playerRow.setMinWidth(400);

        Label titleLabel = new Label("JOUEUR " + playerNumber + " :");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        titleLabel.setMinWidth(160);

        TextField pseudoInput = new TextField();
        pseudoInput.setPromptText("Pseudo");    // texte indicatif grisé
        pseudoInput.setPrefWidth(180);
        pseudoInput.setStyle("-fx-font-size: 18px; -fx-padding: 5px;");

        fields[index] = pseudoInput;  // stocke la référence du champ dans le tableau

        playerRow.getChildren().addAll(titleLabel, pseudoInput);
        return playerRow;
    }

    /**
     * Configure le contenu principal : les 4 lignes joueurs, label d'erreur, bouton confirmer.
     */
    private void setupContent() {
        VBox blueBox = new VBox(10);
        blueBox.setAlignment(Pos.CENTER);
        blueBox.setPadding(new Insets(15));
        blueBox.setMaxWidth(420);
        blueBox.setTranslateY(0);

        // Fond turquoise avec coins arrondis pour la boîte contenant les joueurs
        blueBox.setBackground(new Background(new BackgroundFill(
                Color.valueOf("#00A0A0"),
                new CornerRadii(10),
                Insets.EMPTY
        )));

        TextField[] fields = new TextField[4]; // tableau pour stocker les TextFields des joueurs

        // Création des lignes pour chaque joueur
        HBox player1Row = createPlayerRow("1", fields, 0);
        HBox player2Row = createPlayerRow("2", fields, 1);
        HBox player3Row = createPlayerRow("3", fields, 2);
        HBox player4Row = createPlayerRow("4", fields, 3);

        // Label pour afficher les erreurs (doublons)
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px; -fx-font-weight: bold;");
        errorLabel.setVisible(false); // caché par défaut

        // Bouton confirmer la saisie
        Button confirmButton = new Button("Confirmer");
        confirmButton.setStyle(
                "-fx-background-color: #008080; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 30; " +
                        "-fx-background-radius: 5;"
        );

        // Effet hover (surlignage) sur le bouton confirmer
        confirmButton.setOnMouseEntered(e ->
                confirmButton.setStyle(
                        "-fx-background-color: #006666; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 18px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 10 30; " +
                                "-fx-background-radius: 5;"
                )
        );

        confirmButton.setOnMouseExited(e ->
                confirmButton.setStyle(
                        "-fx-background-color: #008080; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 18px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 10 30; " +
                                "-fx-background-radius: 5;"
                )
        );

        // Action à la validation : vérifie qu'il n'y ait pas de doublons parmi les pseudos
        confirmButton.setOnAction(e -> {
            Set<String> pseudos = new HashSet<>();
            boolean hasDuplicate = false;

            // Parcours des champs et vérification doublons
            for (TextField field : fields) {
                String text = field.getText().trim();
                if (!text.isEmpty()) {
                    if (pseudos.contains(text)) {
                        hasDuplicate = true;
                        break;
                    } else {
                        pseudos.add(text);
                    }
                }
            }

            if (hasDuplicate) {
                // Affiche l'erreur en rouge
                errorLabel.setText("Erreur : Vérifiez les pseudos !");
                errorLabel.setVisible(true);
            } else {
                // Pas d'erreur : cache le label d'erreur et continue
                errorLabel.setVisible(false);
                System.out.println("Pseudos valides. Lancement du jeu...");
                // TODO : Lancer la partie multijoueur ici
            }
        });

        // Conteneur centré pour le bouton confirmer
        HBox buttonContainer = new HBox(confirmButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(15, 0, 5, 0));

        // Ajout des lignes joueurs, label d'erreur et bouton dans la boîte principale
        blueBox.getChildren().addAll(
                player1Row,
                player2Row,
                player3Row,
                player4Row,
                errorLabel,
                buttonContainer
        );

        // Wrapper pour centrer et espacer la boîte bleue dans contentContainer
        VBox boxWrapper = new VBox(blueBox);
        boxWrapper.setAlignment(Pos.TOP_CENTER);
        boxWrapper.setPadding(new Insets(15, 0, 0, 0));

        contentContainer.getChildren().add(boxWrapper);
    }

    /**
     * Ajoute un bouton retour en bas à gauche permettant de revenir au menu principal.
     */
    private void addRetourButton() {
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
}
