package app.wordle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Scoreboard {

    public int gamesWon = 0, gamesLost = 0, gamesPlayed = 0, totalGuesses = 0, streak = 0;

    private final String[] stats = {
            "Time",
            "Best Time",
            "Average Time",
            "",
            "Streak",
            "Games played",
            "Games won",
            "Games lost",
            "Average guesses",
    };

    public void updateScores(int status, int currentRow) {
        Timer.stop();
        Timer.addTotalTime();
        Timer.setBestTime();
        Timer.start();

        if (status == 1) { // win
            gamesPlayed++;
            totalGuesses += currentRow-1;
            gamesWon++;
            streak ++;
        } else if (status == 0) { // lose
            gamesPlayed++;
            totalGuesses += currentRow-1;
            gamesLost++;
            streak = 0;
        }
    }

    public void display() throws IOException{
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("scoreboard.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 460, 580);

        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        stage.setTitle("Scoreboard");
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

        GridPane table = (GridPane) root.lookup("#table");

        if (gamesPlayed == 0) gamesPlayed = 1; // to prevent /0 error
        double averageGuesses = Math.floor(10.0*totalGuesses/gamesPlayed)/10.0;
        String averageTime = Timer.format((long) (Timer.totalTime*1.0/gamesPlayed));

        // adds statlines
        for (int i = 0; i < 9; i++)
            table.add(new Label(stats[i]), 0, i);

        // adds times
        table.add(new Label(Timer.format(Timer.elapsedTime)), 1, 0);
        table.add(new Label(Timer.format(Timer.bestTime)), 1, 1);
        table.add(new Label(averageTime), 1, 2);
        // adds scores
        table.add(new Label(String.valueOf(streak)), 1, 4);
        table.add(new Label(String.valueOf(gamesPlayed)), 1, 5);
        table.add(new Label(String.valueOf(gamesWon)), 1, 6);
        table.add(new Label(String.valueOf(gamesLost)), 1, 7);
        table.add(new Label(String.valueOf(averageGuesses)), 1, 8);
    }
}