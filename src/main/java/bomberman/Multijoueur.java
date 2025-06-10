package bomberman;

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

public class Multijoueur extends Application {

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    private StackPane root;
    private VBox contentContainer;
    private ImageView backgroundImage;
    private ImageView titleImage;
    private Label errorLabel;
    private Stage stage;  // On garde la référence du stage ici

    @Override
    public void start(Stage stage) {
        this.stage = stage; // garde le stage pour navigation

        initializeContainers();
        setBackground();
        createTitle();
        setupContent();
        addRetourButton();

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Sélection des Joueurs");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void initializeContainers() {
        root = new StackPane();
        contentContainer = new VBox(20);
        contentContainer.setAlignment(Pos.TOP_CENTER);
        contentContainer.setMaxWidth(600);
        contentContainer.setPadding(new Insets(30, 0, 0, 0));
    }

    private void setBackground() {
        try {
            Image image = new Image(getClass().getResourceAsStream("/images/imgfond.jpg"));
            backgroundImage = new ImageView(image);
            backgroundImage.setFitWidth(WINDOW_WIDTH);
            backgroundImage.setFitHeight(WINDOW_HEIGHT);
            backgroundImage.setPreserveRatio(false);

            root.getChildren().add(backgroundImage);
            root.getChildren().add(contentContainer);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image de fond : " + e.getMessage());
        }
    }

    private void createTitle() {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/images/pseudo.png"));
            titleImage = new ImageView(logo);
            titleImage.setFitWidth(400);
            titleImage.setFitHeight(200);
            titleImage.setPreserveRatio(true);

            VBox titleContainer = new VBox(titleImage);
            titleContainer.setAlignment(Pos.TOP_CENTER);
            titleContainer.setPadding(new Insets(10, 0, 40, 0));

            contentContainer.getChildren().add(titleContainer);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du logo : " + e.getMessage());
        }
    }

    private HBox createPlayerRow(String playerNumber, TextField[] fields, int index) {
        HBox playerRow = new HBox(15);
        playerRow.setAlignment(Pos.CENTER_LEFT);
        playerRow.setPadding(new Insets(5, 5, 5, 20));
        playerRow.setMinWidth(400);

        Label titleLabel = new Label("JOUEUR " + playerNumber + " :");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        titleLabel.setMinWidth(160);

        TextField pseudoInput = new TextField();
        pseudoInput.setPromptText("Pseudo");
        pseudoInput.setPrefWidth(180);
        pseudoInput.setStyle("-fx-font-size: 18px; -fx-padding: 5px;");
        fields[index] = pseudoInput;

        playerRow.getChildren().addAll(titleLabel, pseudoInput);
        return playerRow;
    }

    private void setupContent() {
        VBox blueBox = new VBox(10);
        blueBox.setAlignment(Pos.CENTER);
        blueBox.setPadding(new Insets(15));
        blueBox.setMaxWidth(420);
        blueBox.setTranslateY(0);

        blueBox.setBackground(new Background(new BackgroundFill(
                Color.valueOf("#00A0A0"),
                new CornerRadii(10),
                Insets.EMPTY
        )));

        TextField[] fields = new TextField[4];

        HBox player1Row = createPlayerRow("1", fields, 0);
        HBox player2Row = createPlayerRow("2", fields, 1);
        HBox player3Row = createPlayerRow("3", fields, 2);
        HBox player4Row = createPlayerRow("4", fields, 3);

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px; -fx-font-weight: bold;");
        errorLabel.setVisible(false);

        Button confirmButton = new Button("Confirmer");
        confirmButton.setStyle(
                "-fx-background-color: #008080; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 30; " +
                        "-fx-background-radius: 5;"
        );

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

        confirmButton.setOnAction(e -> {
            Set<String> pseudos = new HashSet<>();
            boolean hasDuplicate = false;

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
                errorLabel.setText("Erreur : Deux joueurs ont le même pseudo !");
                errorLabel.setVisible(true);
            } else {
                errorLabel.setVisible(false);
                System.out.println("Pseudos valides. Lancement du jeu...");
                // Lancer le jeu ici
            }
        });

        HBox buttonContainer = new HBox(confirmButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(15, 0, 5, 0));

        blueBox.getChildren().addAll(
                player1Row,
                player2Row,
                player3Row,
                player4Row,
                errorLabel,
                buttonContainer
        );

        VBox boxWrapper = new VBox(blueBox);
        boxWrapper.setAlignment(Pos.TOP_CENTER);
        boxWrapper.setPadding(new Insets(15, 0, 0, 0));

        contentContainer.getChildren().add(boxWrapper);
    }

    private void addRetourButton() {
        Button retourBtn = new Button("<- Retour");
        retourBtn.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold;"
        );
        // Positionner le bouton en bas à gauche
        StackPane.setAlignment(retourBtn, Pos.BOTTOM_LEFT);
        StackPane.setMargin(retourBtn, new Insets(10));

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
