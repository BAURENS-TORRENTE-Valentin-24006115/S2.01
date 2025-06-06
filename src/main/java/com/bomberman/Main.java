package com.bomberman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger le fichier FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/BombermanGame.fxml"));
        Parent root = loader.load();

        // Créer la scène
        Scene scene = new Scene(root, 800, 900);

        // Charger le CSS
        scene.getStylesheets().add(getClass().getResource("/bomberman.css").toExternalForm());

        // Configuration de la fenêtre
        primaryStage.setTitle("Super Bomberman - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();

        // S'assurer que la fenêtre peut recevoir les événements clavier
        root.requestFocus();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}