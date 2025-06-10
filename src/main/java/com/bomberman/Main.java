package com.bomberman;

import javafx.application.Application;
import javafx.stage.Stage;

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