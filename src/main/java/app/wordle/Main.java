package app.wordle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // loads fxml class into scene and creates controller instance
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("view.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        Scene scene = new Scene(root, 580, 820);

        // and sets window title, icons, and dimensions
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        stage.setMinWidth(580);
        stage.setMinHeight(820);
        stage.setResizable(true);
        stage.setTitle("WordleFX");
        stage.setScene(scene);

        controller.loadDictonary();
        controller.loadKeyboard();
        controller.loadTileGrid();
        controller.getWord();

        stage.show();
        controller.tutorial.display();
        Timer.start();

        // ESC shortcut to close window
        scene.setOnKeyPressed(e -> {
            KeyCode keycode = e.getCode();
            if (keycode == KeyCode.ESCAPE)
                stage.close();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}