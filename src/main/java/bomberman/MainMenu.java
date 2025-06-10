package bomberman;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
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

public class MainMenu extends Application {

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    private StackPane root;
    private VBox menuContainer;
    private ImageView backgroundImage;
    private ImageView titleImage;

    @Override
    public void start(Stage primaryStage) {
        initializeContainers();
        setBackground();
        createTitle();
        createButtons(primaryStage);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        primaryStage.setTitle("Bomberman");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void initializeContainers() {
        root = new StackPane();

        menuContainer = new VBox(30);
        menuContainer.setAlignment(Pos.TOP_CENTER);
        menuContainer.setMaxWidth(300);
        menuContainer.setTranslateY(80);
    }

    private void createButtons(Stage primaryStage) {
        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setTranslateY(30);

        Button multiplayerButton = createMenuButton("MULTIJOUEUR");
        Button soloButton = createMenuButton("SOLO");
        Button optionsButton = createMenuButton("OPTIONS");
        Button exitButton = createMenuButton("EXIT");

        multiplayerButton.setOnAction(e -> {
            Multijoueur playerSelection = new Multijoueur();
            Stage playerSelectionStage = new Stage();
            playerSelection.start(playerSelectionStage);
            primaryStage.hide();
        });

        soloButton.setOnAction(e -> {
            System.out.println("Mode solo sélectionné");
        });

        optionsButton.setOnAction(e -> {
            System.out.println("Options sélectionnées");
        });

        exitButton.setOnAction(e -> Platform.exit());

        buttonBox.getChildren().addAll(multiplayerButton, soloButton, optionsButton, exitButton);
        menuContainer.getChildren().add(buttonBox);
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        String defaultStyle = "-fx-background-color: #00A0A0; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-min-width: 200px; " +
                "-fx-min-height: 40px; " +
                "-fx-cursor: hand;";

        String hoverStyle = "-fx-background-color: #008080; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-min-width: 200px; " +
                "-fx-min-height: 40px; " +
                "-fx-cursor: hand;";

        button.setStyle(defaultStyle);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), button);
        scaleIn.setToX(1.1);
        scaleIn.setToY(1.1);
        scaleIn.setAutoReverse(false);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), button);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);
        scaleOut.setAutoReverse(false);

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

    private void createTitle() {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/images/bomberpx.png"));
            titleImage = new ImageView(logo);

            titleImage.setFitWidth(400);
            titleImage.setFitHeight(200);
            titleImage.setPreserveRatio(true);

            menuContainer.getChildren().add(titleImage);

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du logo : " + e.getMessage());
        }
    }

    private void setBackground() {
        try {
            Image image = new Image(getClass().getResourceAsStream("/images/imgfond.jpg"));
            backgroundImage = new ImageView(image);
            backgroundImage.setFitWidth(WINDOW_WIDTH);
            backgroundImage.setFitHeight(WINDOW_HEIGHT);
            backgroundImage.setPreserveRatio(false);

            root.getChildren().add(backgroundImage);
            root.getChildren().add(menuContainer);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image de fond : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}