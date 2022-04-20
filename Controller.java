import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

    private static final int COLUMNS = 7;
    private static final int ROWS = 6;
    private static final float CIRCLE_DIAMETER = 80f;
    private static final String discColor1 = "#FD6F96";
    private static final String discColor2 = "#6F69AC";

    private static String PLAYER_ONE = "Player One";
    private static String PLAYER_TWO = "Player Two";

    private boolean isPlayerOneTurn = true;

    private final Disc[][] insertedDiscsArrays = new Disc[ROWS][COLUMNS];

    @FXML
    public GridPane rootGridPane;

    @FXML
    public Pane insertedDiscsPane;

    @FXML
    public Label playerNameLabel;

    @FXML
    public TextField playerOneTextField, playerTwoTextField;    // Part of Assignment Solution

    @FXML
    public Button setNamesButton;   // Part of Assignment Solution

    private boolean isAllowedToInsert = true;

    public void createPlayground() {

        setNamesButton.setOnAction(event -> {
            PLAYER_ONE = playerOneTextField.getText();
            PLAYER_TWO = playerTwoTextField.getText();
            playerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO);
        });

        Shape rectangleWithHoles = createGameStructuralGrid();
        rootGridPane.add(rectangleWithHoles, 0, 1);

        List<Rectangle> rectangleList = createClickableColumns();

        for (Rectangle rectangle:rectangleList) {
            rootGridPane.add(rectangle,0,1);
        }
    }

    private Shape createGameStructuralGrid() {

        Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);

        for (int row = 0; row < ROWS; row++) {

            for (int col = 0; col < COLUMNS; col++) {
                Circle circle = new Circle();
                circle.setRadius(CIRCLE_DIAMETER / 2);
                circle.setCenterX(CIRCLE_DIAMETER / 2);
                circle.setCenterY(CIRCLE_DIAMETER / 2);
                circle.setSmooth(true);

                circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
                circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

                rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
            }
        }

        rectangleWithHoles.setFill(Color.WHITE);

        return rectangleWithHoles;
    }

    private List<Rectangle> createClickableColumns()
    {
        List<Rectangle> rectangleList = new ArrayList<>();
       for(int col = 0; col <COLUMNS; col++) {

           Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
           rectangle.setFill(Color.TRANSPARENT);
           rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

           rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee66")));
           rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

           final int column = col;
           rectangle.setOnMouseClicked(event ->{
               if(isAllowedToInsert){
                   isAllowedToInsert = false;
                   insertDisc (new Disc(isPlayerOneTurn), column);}
           });

           rectangleList.add(rectangle);
       }
        return rectangleList;
    }

    private void insertDisc(Disc disc, int column) {

        int row = ROWS -1 ;
        while (row >= 0)
        {
            if (getDiscIfPresent(row,column) == null)
                break;
            row--;
        }

        if (row < 0)
            return;

        insertedDiscsArrays[row][column] = disc;
        insertedDiscsPane.getChildren().add(disc);

        disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

        int currentRow = row;
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),disc);
        translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
        translateTransition.setOnFinished(event -> {

            isAllowedToInsert = true;
            if (gameEnded(currentRow, column)){
                gameOver();
            }

            isPlayerOneTurn = !isPlayerOneTurn;
            playerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO);
        });
        translateTransition.play();
    }

    private boolean gameEnded(int row, int column) {

        List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3)  // If, row = 3, column = 3, then row = 0,1,2,3,4,5,6
                .mapToObj(r -> new Point2D(r, column))  // 0,3  1,3  2,3  3,3  4,3  5,3  6,3 [ Just an example for better understanding ]
                .collect(Collectors.toList());

        List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3)
                .mapToObj(col -> new Point2D(row, col))
                .collect(Collectors.toList());

        Point2D startingPoint1 = new Point2D(row - 3, column + 3);
        List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> startingPoint1.add(i, -i))
                .collect(Collectors.toList());

        Point2D startingPoint2 = new Point2D(row - 3, column - 3);
        List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> startingPoint2.add(i, i))
                .collect(Collectors.toList());

        return checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
                || checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);
    }

    private boolean checkCombinations(List<Point2D> points) {
        int chain = 0;

        for (Point2D point: points) {

            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();

            Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

            if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn) {  // if the last inserted Disc belongs to the current player

                chain++;
                if (chain == 4) {
                    return true;
                }
            } else {
                chain = 0;
            }
        }

        return false;
    }

    private Disc getDiscIfPresent(int row, int column)
    {
        if (row >= ROWS || row < 0 || column >= COLUMNS || column < 0)
            return null;

        return insertedDiscsArrays[row][column];
    }

    private void gameOver() {
        String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Winner");
        alert.setHeaderText("Congratulations! " + winner + " WON!!");
        alert.setContentText("Winner is: " + winner + ", Want to play Again?");

        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No, Exit");
        alert.getButtonTypes().setAll(yesBtn, noBtn);

        Platform.runLater(() ->{
            Optional<ButtonType> btnClicked  = alert.showAndWait();
            if (btnClicked.isPresent() && btnClicked.get() == yesBtn){
                resetGame();
            }else{
                Platform.exit();
                System.exit(0);
            }
        });


    }

    public void resetGame() {
        insertedDiscsPane.getChildren().clear();

        for (Disc[] insertedDiscsArray : insertedDiscsArrays) {
            Arrays.fill(insertedDiscsArray, null);
        }

        playerOneTextField.clear();
        playerTwoTextField.clear();
        PLAYER_ONE = "Player One";
        PLAYER_TWO = "Player Two";
        isPlayerOneTurn = true;
        playerNameLabel.setText(PLAYER_ONE);
    }

    private static class Disc extends Circle
    {
        private final boolean isPlayerOneMove;

        public Disc (boolean isPlayerOneMove) {
            this.isPlayerOneMove = isPlayerOneMove;
            setRadius(CIRCLE_DIAMETER/2);
            setFill(isPlayerOneMove? Color.valueOf(discColor1):Color.valueOf(discColor2));
            setCenterX(CIRCLE_DIAMETER/2);
            setCenterY(CIRCLE_DIAMETER/2);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
