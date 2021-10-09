package com.internshala.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();

        controller = loader.getController();
        controller.createPlayground();

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());  // Menu Bar Strech.

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);
        Scene scene = new Scene(rootGridPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    private  MenuBar  createMenu(){
        //File Menu
        Menu fileMenu = new Menu("File");

        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(event -> controller.resetGame());
        MenuItem resetGame = new MenuItem("Reset Game");
        resetGame.setOnAction(event -> controller.resetGame());
        SeparatorMenuItem sepratorMenuItem = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit Game");
        exitGame.setOnAction(event -> exitGame());

        fileMenu.getItems().addAll(newGame,resetGame,sepratorMenuItem,exitGame);

        //Help Menu
        Menu helpMenu = new Menu("Help");

        MenuItem aboutGame = new MenuItem("About Connect4");
        aboutGame.setOnAction(event -> aboutConnect4Game());
        SeparatorMenuItem seprator = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(event -> aboutMe());

        helpMenu.getItems().addAll(aboutGame,seprator,aboutMe);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu,helpMenu);

        return menuBar;

    }

    private void aboutMe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About The Developer");
        alert.setHeaderText("Aryan Srivastava");
        alert.setContentText("I love to learn new things and play around with code."+
                "I have created few Applications before, but this is my First JavaFX Game"+
                "Connect 4 is a game. \n"+
                "I like to spend time either with nears and dears or infront of my lappy.\n"+
                "Hope you will like the game.");

        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(480,320);

        alert.show();
    }

    private void aboutConnect4Game() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Four");
        alert.setHeaderText("How To Play");
        alert.setContentText("Connect Four is a two-player connection game in which"+
                " the players first choose a color and then take turns dropping colored discs"
                +" from the top into a seven-column, six-row vertically suspended grid."
                +" The pieces fall straight down, occupying the next available space within the column."
                +" The objective of the game is to be the first to form a horizontal, vertical,"
                +" or diagonal line of four of one's own discs. Connect Four is a solved game."
                +" The first player can always win by playing the right moves.");

        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(480,320);

        alert.show();

    }


    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }




    public static void main(String[] args) {
        launch(args);
    }
}
