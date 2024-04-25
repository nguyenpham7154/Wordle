package app.wordle;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.collections.ObservableList;
import javafx.css.StyleClass;
import javafx.fxml.FXML;
import javafx.geometry.Pos;

import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.util.Duration;

public class Controller {
    private final String[][] keyboardLetters = {
            {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
            {"A", "S", "D", "F", "G", "H", "J", "K", "L"},
            {"ENTER", "Z", "X", "C", "V", "B", "N", "M", "DEL"}};
    private static HashMap<String, Button> keyHashMap = new HashMap<String, Button>();

    private static ArrayList<String> dictionary = new ArrayList<String>();
    private static ArrayList<String> guessedWords = new ArrayList<String>();

    private String correctWord;
    private String currentWord = "";
    private int maxRows = 6, maxColumns = 5;
    private int currentRow = 1, currentColumn = 1;
    private int gamesWon = 0, gamesLost = 0, gamesPlayed = 0, totalGusses = 0, streak = 0;
    private Boolean disabled = false;

    @FXML private VBox root;
    @FXML private VBox notificationStack;
    @FXML private GridPane tileGrid;
    @FXML private GridPane keyboard1, keyboard2, keyboard3;

    // loads txt file into string array
    public void loadDictonary() {
        try {
            Scanner scanner = new Scanner(new File("src/main/resources/app/wordle/dictionary.txt"));
            while (scanner.hasNextLine()) dictionary.add(scanner.next());
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    // selects random word from dictionary as correctWord
    public void getWord() {
        //correctWord = dictionary.get((int) (Math.random() * (dictionary.size() + 1))).toUpperCase();
        correctWord = "APPLE";
        System.out.println("correctWord = " + correctWord); // debug
    }

    public void loadTileGrid () {
        //request focus to get physical keyboard input
        tileGrid.requestFocus();
        tileGrid.setOnMouseClicked(e -> tileGrid.requestFocus());
        tileGrid.setOnKeyPressed(this::physicalKeyboardInput);

        for (int i = 1; i <= maxRows; i++) {
            for (int j = 1; j <= maxColumns; j++) {
                Label tile = new Label();
                tile.setText("");
                tile.setId(j + "-" + i);
                tile.getStyleClass().add("tile");
                tileGrid.add(tile, j, i);
            }
        }
    }

    public void loadKeyboard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < keyboardLetters[i].length; j++) {
                Button key = new Button();
                String keyLetter = keyboardLetters[i][j];

                key.getStyleClass().add("key");
                key.setText(keyLetter);
                key.setId(keyLetter);
                key.setOnAction(this::virtualKeyboardInput);
                keyHashMap.put(keyLetter, key);

                if (i == 0)
                    keyboard1.add(key, j, 0);
                else if (i == 1)
                    keyboard2.add(key, j, 1);
                else {
                    if (j == 0 || j == 8) // enter and del keys
                        key.getStyleClass().add("largeKey");
                    keyboard3.add(key, j, 2);
                }

            }
        }
    }

    public void physicalKeyboardInput(KeyEvent keyEvent) {
        KeyCode keycode = keyEvent.getCode();
        if (!disabled) {
            if (keycode.isLetterKey())
                onLetter(keyEvent.getText().toUpperCase());
            else if (keycode == KeyCode.BACK_SPACE)
                onDelete();
            else if (keycode == KeyCode.ENTER)
                onEnter();
        }
    }

    public void virtualKeyboardInput(ActionEvent event) {
        Button button = (Button) event.getSource();
        String id = button.getId();

        if (!disabled) {
            if (id.length() == 1)
                onLetter(id);
            else if (id.equals("DEL"))
                onDelete();
            else if (id.equals("ENTER"))
                onEnter();
        }

    }

    public void onLetter(String letter) {
        if (currentColumn <= maxColumns) {
            ((Label) tileGrid.lookup("#" + currentColumn + "-" + currentRow)).setText(letter);
            currentWord += letter;
            currentColumn++;
        }
    }

    public void onDelete() {
        // decrements currentColumn but keeps it above 1
        currentColumn = Math.max(currentColumn-1, 1);
        Label tile = (Label) tileGrid.lookup("#" +currentColumn + "-" + currentRow);
        tile.setText("");
        // currentWord reassigned excluding deleted character
        currentWord = currentWord.substring(0, currentColumn-1);
    }

    public void onEnter() {
        // if word is not valid, runs notification with message
        if (currentWord.length() != maxColumns)
            notification("Not enough letters");
        /*else if (!search(dictionary, currentWord.toLowerCase()))
            notification("Not in word list");*/
        else if (guessedWords.contains(currentWord))
            notification("Word already tried");

        // else processes the input
        else {
            guessedWords.add(currentWord);
            setColors();
            currentRow++;
            currentColumn = 1;

            // ends game if either input is correct or no more guesses left
            if (correctWord.equals(currentWord))
                endgame(1);
            else if (currentRow > maxRows)
                endgame(0);

            currentWord = "";
        }
    }

    private int n = 0;

    public void notification(String message) {
        // limits number of labels to prevent overflow
        if (n < 8) {
            Label popup = new Label(message);
            popup.getStyleClass().add("popup");
            // adds labels to top of vbox
            notificationStack.getChildren().add(0, popup); n++;
            // fades completely in 0.5s
            FadeTransition fade = new FadeTransition(Duration.millis(500), popup);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);

            Timeline timeline = new Timeline(
                // starts fading at 1.5s
                new KeyFrame(Duration.millis(1500), event -> {
                    fade.play();
                }),
                // label fades completely and gets removed at 2s
                new KeyFrame(Duration.millis(2000), event -> {
                    notificationStack.getChildren().remove(popup); n--;
                })
            );
            timeline.play();
        }
    }

    // binary search algorithm
    private boolean search(ArrayList<String> list, String target) {
        int low = 0, high = list.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comparison = target.compareTo(list.get(mid));
            if (comparison == 0)
                return true;
            if (comparison > 0)
                low = mid + 1;
            else
                high = mid - 1;
        }
        return false;
    }

   public void setColors() {
        String word = "";
        for (int i = 0; i < 5; i++) {
            char letter = currentWord.charAt(i);
            if (correctWord.charAt(i) == letter) {
                word += '*';
                if (word.contains(String.valueOf(letter)))
                    word = word.replaceFirst(String.valueOf(letter), "_");
            }
            else
                word += letter;
        }

        for (int i = 0; i < 5; i++) {
            String letter = String.valueOf(currentWord.charAt(i));
            Label tile = (Label) tileGrid.lookup("#" + (i+1) + "-" + currentRow);
            Button key = keyHashMap.get(letter);
            ObservableList<String> keystyle = key.getStyleClass();

            if (word.charAt(i) == '*') {
                tile.getStyleClass().add("greenTile");
                keystyle.add("greenKey");
            }
            else if (word.charAt(i) != '_') {
                tile.getStyleClass().add("yellowTile");
                keystyle.add("yellowKey");
            }
            else {
                tile.getStyleClass().add("grayTile");
                keystyle.add("grayKey");
            }
        }
    }

    public void endgame(int game) {
        gamesPlayed++;
        if (game == 1) {
            totalGusses += currentRow-1;
            gamesWon++;
            streak ++;
        } else {
            gamesLost++;
            streak = 0;
            notification(correctWord);
        }
        disabled = true;
    }

    @FXML protected void help() {
        Tutorial.display();
    }
    @FXML protected void scoreboard() throws IOException {
        Scoreboard.display();

        double averageGuesses = (gamesWon == 0)? 0 : Math.floor(10.0*totalGusses/gamesWon)/10.0;
        System.out.println("\n--- Scoreboard ---");
        System.out.println("Games played:     " + gamesPlayed);
        System.out.println("Games won:        " + gamesWon);
        System.out.println("Games lost:       " + gamesLost);;
        System.out.println("Streak:           " + streak);
        System.out.println("Average guesses:  " + averageGuesses + "\n");
    }

    @FXML protected void reset() {
        getWord();
        guessedWords.clear();
        currentWord = "";
        currentColumn = 1;
        currentRow = 1;

        for (int i = 1; i <= maxRows; i++) {
            for (int j = 1; j <= maxColumns; j++) {
                Label tile = (Label) tileGrid.lookup("#" + j + "-" + i);
                tile.setText("");
                tile.getStyleClass().clear();
                tile.getStyleClass().add("tile");
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < keyboardLetters[i].length; j++) {
                String keyLetter = keyboardLetters[i][j];
                Button key = keyHashMap.get(keyLetter);

                key.getStyleClass().clear();
                key.getStyleClass().add("key");
                if (i == 2 && (j == 0 || j == 8))
                    key.getStyleClass().add("largeKey");
            }
        }

        disabled = false;
        tileGrid.requestFocus();
        tileGrid.setOnMouseClicked(e -> tileGrid.requestFocus());
    }

    @FXML protected void settings() {
    }
}