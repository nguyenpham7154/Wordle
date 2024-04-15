package app.wordle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Screen;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        Scene scene = new Scene(root, 620, 860);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        stage.setResizable(false);
        stage.setTitle("wordleFX");
        stage.setScene(scene);

        controller.loadDictonary();
        controller.loadKeyboard();
        controller.loadTileGrid();
        controller.getWord();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}