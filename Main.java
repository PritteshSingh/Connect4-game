import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
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
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);

        Scene scene = new Scene(rootGridPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private MenuBar createMenu()
    {   // File Menu
        Menu fileMenu = new Menu ("File");

        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(event -> resetGame());
        MenuItem resetGame = new MenuItem("Reset Game");
        resetGame.setOnAction(event -> resetGame());
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit Game");
        exitGame.setOnAction(event -> exitGame());

        fileMenu.getItems().addAll(newGame,resetGame,separatorMenuItem,exitGame);

        // Help Menu
        Menu helpMenu = new Menu ("Help");

        MenuItem aboutGame = new MenuItem("About Connect 4");
        aboutGame.setOnAction(event -> aboutGame());
        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(event -> aboutMe());

        helpMenu.getItems().addAll(aboutGame,separator,aboutMe);

        MenuBar menubar = new MenuBar();
        menubar.getMenus().addAll(fileMenu,helpMenu);
        return menubar;
    }

    private void aboutMe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About the Developer");
        alert.setHeaderText("Prittesh Singh");
        alert.setContentText("Hello Earthlings! My name is LORD Prittesh Singh" +
                            " and I'm a self-prophesized GOD of Developers" +
                            ". So kneel before your LORD, or You shall feel my Wrath");
        alert.show();
    }

    private void aboutGame() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect4");
        alert.setHeaderText("How To Play");
        alert.setContentText("Connect Four is a two-player connection game" +
                " in which the players first choose a color and then" +
                " take turns dropping colored discs from the top into a seven-column," +
                " six-row vertically suspended grid. The pieces fall straight" +
                " down, occupying the next available space within the column." +
                " The objective of the game is to be the first to form" +
                " a horizontal, vertical, or diagonal line of four of one's own" +
                " discs. Connect Four is a solved game. The first player" +
                " can always win by playing the right moves.");
        alert.show();
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

    private void resetGame() {
        controller.resetGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}