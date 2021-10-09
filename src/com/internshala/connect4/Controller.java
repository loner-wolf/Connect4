package com.internshala.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Shape3D;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

    private static final int COLUMNS = 7;
    private static final int ROWS = 6;
    private static final int CIRCLE_DIAMETER = 84;
    private static final String discColor1 = "#24303E";
    private static final String discColor2 = "#4CAA88";

    private boolean isAllowedToInsert = true;

    private static String PLAYER_ONE = "Player One";
    private static String PLAYER_TWO = "Player Two";

    private boolean isPlayerOneTurn = true;

    private final Disc[][] insertedDiscsArray = new Disc[ROWS][COLUMNS]; // For Structural Changes: For Developers.

    @FXML
    public TextField playerOneName;

    @FXML
    public TextField playerTwoName;

    @FXML
    public Button setNameButton;

    @FXML
    public GridPane rootGridPane;

    @FXML
    public Pane insertDiscPane;

    @FXML
    public Label playerNameLabel;

    public void createPlayground(){
        Shape rectangleWithHoles = createGameStructuralGrid();

        rootGridPane.add(rectangleWithHoles,0,1);

        List<Rectangle> rectangleList = createClickableColumn();

        for (Rectangle rectangle : rectangleList ) {
            rootGridPane.add(rectangle,0,1);
        }



    }

    private Shape createGameStructuralGrid(){

        Shape rectangleWithHoles = new Rectangle((COLUMNS + 1)*CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);

        for (int row =0; row < ROWS; row++)
        {
            for (int col = 0;col < COLUMNS ; col++)
            {
                Circle circle = new Circle();
                circle.setRadius((float)(CIRCLE_DIAMETER / 2));
                circle.setCenterX((float)(CIRCLE_DIAMETER / 2));
                circle.setCenterY((float)(CIRCLE_DIAMETER / 2));
                circle.setSmooth(true);

                circle.setTranslateX(col * (CIRCLE_DIAMETER+5) + 27);
                circle.setTranslateY(row * (CIRCLE_DIAMETER+5)  +27);
                rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
            }
        }
        rectangleWithHoles.setFill(Color.WHITE);

        return rectangleWithHoles;
    }

    private List<Rectangle> createClickableColumn() {

        List<Rectangle> rectangleList = new ArrayList<>();

        for (int col = 0; col < COLUMNS; col++) {


        Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setTranslateX(col * (CIRCLE_DIAMETER+5) + 27);

        rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
        rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
        final int column = col;
        rectangle.setOnMouseClicked(event -> {
            if(isAllowedToInsert){
                isAllowedToInsert = false;
                insertDisc(new Disc(isPlayerOneTurn), column);

            }

        });

        rectangleList.add(rectangle);
         }
        return rectangleList;
    }

    private void insertDisc(Disc disc, int column){

        int row = ROWS;
        while (--row >=0){
            if (getDiscIfPresent(row,column) == null) {
                break;
            }

        }
        if (row<0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("No Room To Enter Disc in The Column.");
            alert.setContentText("You caused the Application to crash...\n Relaunch to play again.");
            Optional<ButtonType> btnClicked = alert.showAndWait();
            if(btnClicked.isPresent())
            {
                Platform.exit();
                System.exit(0);
            }


        }


        insertedDiscsArray[row][column] = disc;  // For structural Changes : For developers
        insertDiscPane.getChildren().add(disc); // For Visual Changes

        disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + 27) ;

        int currentRow = row;
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),disc);

        translateTransition.setToY(row * (CIRCLE_DIAMETER+5)  +27);
        translateTransition.setOnFinished(event -> {
            isAllowedToInsert = true;

            if(gameEnded(currentRow, column))
            {
                gameOver();
                return;
            }

            isPlayerOneTurn = !isPlayerOneTurn;

            playerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE: PLAYER_TWO);

        });

        translateTransition.play();


    }

    private void gameOver() {
        String winner = isPlayerOneTurn? PLAYER_ONE: PLAYER_TWO;
        System.out.println("Winner is : " + winner);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setHeaderText("The Winner is : " + winner);
        alert.setContentText("Want to play again ?");

        ButtonType yesBtn = new ButtonType(("Yes"));
        ButtonType noBtn = new ButtonType("No, Exit");
        alert.getButtonTypes().setAll(yesBtn, noBtn);

        Platform.runLater(() -> {   // Helps us to resolve IllegalStateException. (After Animation/Transtition)

            Optional<ButtonType> btnClicked = alert.showAndWait();
            if(btnClicked.isPresent() && btnClicked.get() == yesBtn)
            {
                //....user has chosen YES so RESET the game.
                resetGame();
            }else {
                Platform.exit();
                System.exit(0);
                //....user chose NO..so Exit the Game.
            }

        });

    }

    public void resetGame() {
        insertDiscPane.getChildren().clear();  // Remove all Inserted Disc from Pane

        //  Structurally , // Make all elements of insertedDisc
        for (Disc[] discs : insertedDiscsArray) Arrays.fill(discs, null);

        isPlayerOneTurn = true; // Let Player One Start the game....
        playerNameLabel.setText(PLAYER_ONE);

        createPlayground();  // Prepare fresh Playground....


    }

    private boolean gameEnded(int row, int column) {

        //Vertical Points
        List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3,row +3).//range of values 0,1,2,3,4,5
                                        mapToObj(r -> new Point2D(r,column)).// 0,3  1,3  2,3  3,3  4,3   5,3 -->  Point2D  x,y
                                        collect(Collectors.toList());

        List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3,column +3).
                mapToObj(col -> new Point2D(row,col)).
                collect(Collectors.toList());

        Point2D startPoint1 = new Point2D(row-3, column+3);
        List<Point2D> diagonal1Points = IntStream.rangeClosed(0,6).
                                        mapToObj(i-> startPoint1.add(i,-i)).
                                        collect(Collectors.toList());


        Point2D startPoint2 = new Point2D(row-3, column-3);
        List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6).
                                        mapToObj(i-> startPoint2.add(i,i)).
                                        collect(Collectors.toList());


        return checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
                            || checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);
    }

    private boolean checkCombinations(List<Point2D> points) {

        int chain = 0;
        for (Point2D point: points) {

            int rowIndexForArray = (int)point.getX();
            int columnIndexForArray = (int)point.getY();

            Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

            if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn) // is Last Inserted Disc is Belongs to the Current Player.
            {
                chain ++;
                if (chain == 4)
                {
                    return  true;
                }
            }else {
                chain = 0;
            }

        }
        return false;
    }

    private Disc getDiscIfPresent(int row, int column){
        if(row >= ROWS || row < 0 || column>= COLUMNS || column<0)
            return null;
        else
            return insertedDiscsArray[row][column];
    }

    private static class Disc extends Circle{

        private final boolean isPlayerOneMove;

        public Disc(boolean isPlayerOneMove){
            this.isPlayerOneMove = isPlayerOneMove;
            setRadius((float)(CIRCLE_DIAMETER/2));
            setFill(isPlayerOneMove? Color.valueOf(discColor1): Color.valueOf(discColor2));
            setCenterX((float)(CIRCLE_DIAMETER/2));
            setCenterY((float)(CIRCLE_DIAMETER/2));
        }



    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setNameButton.setOnAction(event -> getInput());


    }

    private void getInput() {
        PLAYER_ONE = playerOneName.getText();
        PLAYER_TWO = playerTwoName.getText();
    }
}
