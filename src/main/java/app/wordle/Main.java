package app.wordle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Screen;
import java.io.IOException;

public class Main extends Application {
    private final Controller controller = new Controller();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 520, 740);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        stage.setResizable(false);
        stage.setTitle("wordleFX");
        stage.setScene(scene);
        stage.show();

        controller.loadDictonary();
        controller.loadTileGrid();
        controller.loadKeyboard();
    }

    public static void main(String[] args) {
        launch();
    }
}