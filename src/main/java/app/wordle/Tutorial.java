package app.wordle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class Tutorial {
    public void display() throws IOException{
        VBox root = new VBox();
        Label title = new Label("How to play");

        GridPane gridpane = new GridPane();

        root.getChildren().addAll(title, gridpane);

        Stage stage = new Stage();
        Scene scene = new Scene(root, 460, 600);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.setTitle("Tutorial");
        stage.setMinWidth(460);
        stage.setMinHeight(600);
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();

        scene.setOnKeyPressed(e -> {
            KeyCode keycode = e.getCode();
            if (keycode == KeyCode.ESCAPE)
                stage.close();
        });

    }
}
