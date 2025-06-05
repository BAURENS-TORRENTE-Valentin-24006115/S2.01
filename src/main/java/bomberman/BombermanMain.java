package bomberman;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class BombermanMain extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Créer l'interface directement en Java (sans FXML)
            VBox root = new VBox();
            root.getStyleClass().add("root");

            // En-tête
            HBox header = new HBox();
            header.getStyleClass().add("header");
            header.setAlignment(javafx.geometry.Pos.CENTER);
            header.setPrefHeight(50);

            Label title = new Label("Super Bomberman");
            title.getStyleClass().add("title");
            header.getChildren().add(title);

            // Barre d'informations
            HBox infoBar = new HBox();
            infoBar.getStyleClass().add("info-bar");
            infoBar.setAlignment(javafx.geometry.Pos.CENTER);

            Label infoLabel = new Label("Déplacement: ZQSD ou Flèches  |  Bombe: ESPACE");
            infoBar.getChildren().add(infoLabel);

            // Container du jeu
            HBox gameContainer = new HBox();
            gameContainer.getStyleClass().add("game-container");
            gameContainer.setAlignment(javafx.geometry.Pos.CENTER);
            gameContainer.setPrefHeight(620);
            gameContainer.setPrefWidth(620);

            // Grille de jeu
            GridPane gameGrid = new GridPane();
            gameGrid.getStyleClass().add("game-grid");
            gameGrid.setAlignment(javafx.geometry.Pos.CENTER);
            gameGrid.setHgap(0);
            gameGrid.setVgap(0);

            gameContainer.getChildren().add(gameGrid);

            // Assembler l'interface
            root.getChildren().addAll(header, infoBar, gameContainer);

            // Créer le contrôleur et l'initialiser
            GameController controller = new GameController();
            controller.setGameGrid(gameGrid);
            controller.initialize();

            // Créer la scène
            Scene scene = new Scene(root, 660, 720);

            // Ajouter les styles CSS
            String css = getClass().getResource("/game.css") != null ?
                    getClass().getResource("/game.css").toExternalForm() :
                    createInlineCSS();
            scene.getStylesheets().add(css);

            primaryStage.setTitle("Super Bomberman");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

            // Focus sur la grille pour la gestion des touches
            gameGrid.requestFocus();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String createInlineCSS() {
        return "data:text/css," +
                ".root { -fx-background-color: #2c3e50; }" +
                ".header { -fx-background-color: linear-gradient(to bottom, #34495e, #2c3e50); -fx-padding: 10px; }" +
                ".title { -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1; }" +
                ".info-bar { -fx-background-color: #34495e; -fx-padding: 8px; }" +
                ".info-bar .label { -fx-text-fill: #bdc3c7; -fx-font-size: 12px; }" +
                ".game-container { -fx-background-color: #27ae60; -fx-padding: 20px; }" +
                ".game-grid { -fx-background-color: #2c3e50; -fx-border-color: #1a252f; -fx-border-width: 3px; }";
    }

    public static void main(String[] args) {
        launch(args);
    }
}