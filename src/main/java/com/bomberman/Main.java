package com.bomberman;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Point d'entrée principal de l'application Bomberman.
 * <p>
 * Lance le menu principal via JavaFX.
 * </p>
 * @author Valentin B. - Thomas A. - Akim A.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Lancer le menu principal
        MainMenu mainMenu = new MainMenu();
        mainMenu.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}