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

public class option extends Application {

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    private StackPane root;
    private VBox mainContainer;
    private ImageView backgroundImage;
    private ImageView titleImage;

    @Override
    public void start(Stage stage) {
        initializeContainers();
        setBackground();
        createTitle();
        setupContent(stage);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Options");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void initializeContainers() {
        root = new StackPane();
        mainContainer = new VBox();
        mainContainer.setAlignment(Pos.TOP_CENTER);
        root.getChildren().add(mainContainer);
    }

    private void setBackground() {
        try {
            Image image = new Image(getClass().getResourceAsStream("/images/imgfond.jpg"));
            backgroundImage = new ImageView(image);
            backgroundImage.setFitWidth(WINDOW_WIDTH);
            backgroundImage.setFitHeight(WINDOW_HEIGHT);
            backgroundImage.setPreserveRatio(false);

            root.getChildren().add(0, backgroundImage);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image de fond : " + e.getMessage());
        }
    }

    private void createTitle() {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/images/Options.png")); // Image du MainMenu
            titleImage = new ImageView(logo);

            titleImage.setFitWidth(400);
            titleImage.setFitHeight(200);
            titleImage.setPreserveRatio(true);

            VBox titleBox = new VBox(titleImage);
            titleBox.setAlignment(Pos.TOP_CENTER);
            titleBox.setPadding(new Insets(20, 0, 20, 0)); // espace après le titre
            mainContainer.getChildren().add(titleBox);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du titre : " + e.getMessage());
        }
    }

    private void setupContent(Stage stage) {
        VBox centerBox = new VBox();
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPrefHeight(WINDOW_HEIGHT - 300); // Espace restant après titre
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

        // CheckBox pour le style alternatif
        CheckBox styleCheckBox = new CheckBox("Activer style alternatif");
        styleCheckBox.setSelected(Settings.alternativeStyle);
        styleCheckBox.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

        styleCheckBox.setOnAction(e -> {
            Settings.alternativeStyle = styleCheckBox.isSelected();
            System.out.println("alternativeStyle = " + Settings.alternativeStyle);
        });

        // Bouton Retour
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

        optionBox.getChildren().addAll(styleCheckBox, backButton);
        centerBox.getChildren().add(optionBox);

        mainContainer.getChildren().add(centerBox);
    }

    public static class Settings {
        public static boolean alternativeStyle = false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
