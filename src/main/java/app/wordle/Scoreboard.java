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

public class Scoreboard {
    public void display(int played, int won, int lost, int streak, int totalGuesses) throws IOException{
        VBox root = new VBox();
        Label title = new Label("Scoreboard");
        title.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: white;");

        GridPane table = new GridPane();

        root.getStyleClass().add("scoreboard");
        root.getChildren().addAll(title, table);

        Stage stage = new Stage();
        Scene scene = new Scene(root, 460, 600);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.setTitle("Scoreboard");
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

        double averageGuesses = (won == 0)? 0 : Math.floor(10.0*totalGuesses/won)/10.0;
        System.out.println("\n--- Scoreboard ---");
        System.out.println("Games played:     " + played);
        System.out.println("Games won:        " + won);
        System.out.println("Games lost:       " + lost);;
        System.out.println("Streak:           " + streak);
        System.out.println("Average guesses:  " + averageGuesses + "\n");

    }
}
