package app.wordle;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
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
    // hashmap to reference keyboard buttons quickly
    private static HashMap<String, Button> keyHashMap = new HashMap<String, Button>();

    private static ArrayList<String> dictionary = new ArrayList<String>();
    private static ArrayList<String> guessedWords = new ArrayList<String>();

    private String correctWord;
    private String currentWord = "";
    // presets for max word length and guesses (for later feature)
    private int maxRows = 6, maxColumns = 5;
    // keeps track of which column and row currently on, used to identify corresponding tiles on gridpane
    private int currentRow = 1, currentColumn = 1;
    // input functions doesn't run when true
    public Boolean disabled = false;

    // references fxml controller class members and handler methods
    @FXML private VBox root;
    @FXML private VBox notificationStack;
    @FXML private GridPane tileGrid;
    @FXML private GridPane keyboard1, keyboard2, keyboard3;

    Scoreboard scoreboard = new Scoreboard();
    Tutorial tutorial = new Tutorial();

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

    // assigns a random word from dictionary as correctWord
    public void getWord() {
        correctWord = dictionary.get((int) (Math.random() * (dictionary.size() + 1))).toUpperCase();
        System.out.println("correctWord = " + correctWord); // debug
    }

    public void loadTileGrid () {
        // request focus to get physical keyboard input for word grid
        tileGrid.requestFocus();
        tileGrid.setOnKeyPressed(this::physicalKeyboardInput);
        root.setOnMouseClicked(e -> tileGrid.requestFocus());


        // creates tiles (labels) to add to word grid
        for (int i = 1; i <= maxRows; i++) {
            for (int j = 1; j <= maxColumns; j++) {
                Label tile = new Label();
                tile.setText("");
                tile.setId(j + "-" + i); // id set to tile's column-row coordinates
                tile.getStyleClass().add("tile");
                tileGrid.add(tile, j, i);
            }
        }
    }

    // creates virtual keyboard
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
            if (id.length() == 1) // not "DEL" and "ENTER" basically
                onLetter(id);
            else if (id.equals("DEL"))
                onDelete();
            else if (id.equals("ENTER"))
                onEnter();
        }

    }

    public void onLetter(String letter) {
        // does not run if row is already filled
        if (currentColumn <= maxColumns) {
            // updates tile text, increments column tracker
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
        else if (!search(dictionary, currentWord.toLowerCase()))
            notification("Not in word list");
        else if (guessedWords.contains(currentWord))
            notification("Word already tried");
        else {
            guessedWords.add(currentWord);
            setColors();
            currentRow++;
            currentColumn = 1;

            // ends game and disables input if either input is correct or no more guesses left
            if (currentWord.equals(correctWord)) {
                scoreboard.updateScores(1, currentRow);
                disabled = true;
            }
            else if (currentRow > maxRows) {
                scoreboard.updateScores(0, currentRow);
                notification(correctWord); // notifies user of correct word
                disabled = true;
            }
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
                // starts fading at 1.5s for 0.5s
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
        String correctword = ""; // temp variable
        // marks letters in correct positions
        for (int i = 0; i < 5; i++) {
            if (currentWord.charAt(i) == correctWord.charAt(i))
                correctword += 'g';
            else
                correctword += correctWord.charAt(i);
        }

        for (int i = 0; i < 5; i++) {
            // loops through each letter of the user-inputted word
            String letter = String.valueOf(currentWord.charAt(i));
            Label tile = (Label) tileGrid.lookup("#" + (i+1) + "-" + currentRow);
            Button key = keyHashMap.get(letter);

            if (correctword.charAt(i) == 'g') {
                tile.getStyleClass().add("greenTile");
                key.getStyleClass().add("greenKey");
            }
            else if (correctword.contains(letter)) {
                tile.getStyleClass().add("yellowTile");
                key.getStyleClass().add("yellowKey");
                // eliminates 1 occurance of the out-of-position letter in the correct word
                // result decreases the number of times that letter can occur to ensure the correct number of yellows
                // ex. "APPLE" has 2 'P's, so there can only be 2 yellow or green tiles of P in the inputted word
                correctword.replaceFirst(letter, "y");
            }
            else {
                tile.getStyleClass().add("grayTile");
                key.getStyleClass().add("grayKey");
            }
        }
    }


    @FXML protected void help() throws IOException {
        tutorial.display();
    }

    @FXML protected void scoreboard() throws IOException {
        scoreboard.display();
    }

    @FXML protected void reset() {
        // clears text and styling of all tiles
        for (int i = 1; i <= maxRows; i++) {
            for (int j = 1; j <= maxColumns; j++) {
                Label tile = (Label) tileGrid.lookup("#" + j + "-" + i);
                tile.getStyleClass().clear();
                tile.setText("");
                tile.getStyleClass().add("tile");
            }
        }

        // clears all styling of keys
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < keyboardLetters[i].length; j++) {
                String keyLetter = keyboardLetters[i][j];
                Button key = keyHashMap.get(keyLetter);

                if (keyLetter.length() == 1) {
                    key.getStyleClass().clear();
                    key.getStyleClass().add("key");
                }
            }
        }

        // generates new random correct word, clears and resets all tracking varibles
        getWord();
        guessedWords.clear();
        currentWord = "";
        currentColumn = 1; currentRow = 1;
        Timer.stop();

        // re-enables input
        disabled = false;
        tileGrid.requestFocus();
    }

    @FXML protected void settings() {
        Settings.display();
    }
}