package app.wordle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

public class Tutorial {
    public void display() throws IOException{
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("tutorial.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 460, 580);

        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        stage.setTitle("Tutorial");
        stage.setMinWidth(460);
        stage.setMinHeight(400);
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();

        scene.setOnKeyPressed(e -> {
            KeyCode keycode = e.getCode();
            if (keycode == KeyCode.ESCAPE)
                stage.close();
        });

        Label p1 = (Label) root.lookup("#p1");
        p1.setText("Guess the Wordle in 6 tries");
        Label p2 = (Label) root.lookup("#p2");
        p2.setText("Each guess must be a valid 5-letter word.\n" +
                "The color of the tiles will change to show how close your guess was to the word.");
        Label p3 = (Label) root.lookup("#p3");
        p3.setText("• Green letters are in the word and in the correct spot.\n" +
                "• Yellow letters are in the word but in the wrong spot.\n" +
                "• Gray letters are not in the word in any spot.");

    }
}
