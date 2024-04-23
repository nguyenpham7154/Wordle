package app.wordle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class Scoreboard {
    public static void display() throws IOException {
        Stage stage = new Stage();

        Parent root = new Parent() {
            Label title = new Label("Scoreboard");

        };
        Scene scene = new Scene(root, 460, 600);
        stage.setMinWidth(460);
        stage.setMinHeight(600);

        stage.setTitle("Scoreboard");
        stage.show();
    }
}
