package app.wordle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.net.URL;
import java.io.*;
import java.util.*;

public class Controller {
    private final String[][] keyboardLetter = {
            {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
            {"A", "S", "D", "F", "G", "H", "J", "K", "L"},
            {"ENTER", "Z", "X", "C", "V", "B", "N", "M", "⌫"}};
    public static ArrayList<String> dictionary = new ArrayList<String>();
    public static ArrayList<String> guessedWords = new ArrayList<String>();

    public String correctWord;
    private int rows = 6, columns = 5;
    private String currentWord;
    private int currentRow = 1, currentColumn = 1;

    // private int gamesWon = 0, gameslost = 0, gamesPlayed = 0;

    @FXML public Label alertText; // debug
    @FXML public GridPane tileGrid;
    @FXML public GridPane keyboard1, keyboard2, keyboard3;



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

    public void getWord() {
        correctWord = dictionary.get((int) (Math.random() * (dictionary.size() + 1)));
        System.out.println("correctWord = " + correctWord); // debug
    }

    public void loadTileGrid () {
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= columns; j++) {
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
            for (int j = 0; j < keyboardLetter[i].length; j++) {
                Button key = new Button();
                String keyLetter = keyboardLetter[i][j];
                key.setText(keyLetter);
                key.setId(keyLetter);
                key.getStyleClass().add("key");

                if (i == 0) {
                    keyboard1.add(key, j, i);
                }
                else if (i == 1)
                    keyboard2.add(key, j, i);
                else
                    if (j == 0) {
                        key.setMinWidth(69.0);
                    } else if (j == 8) {
                        key.setMinWidth(69.0);
                    }
                    keyboard3.add(key, j, i);

            }
        }
    }

    @FXML protected void physicalKeyboardInput(KeyEvent keyEvent) {
        String input = keyEvent.getText();
        if (keyEvent.getCode().isLetterKey())
            letterInput(input);
        else if (keyEvent.getCode() == KeyCode.BACK_SPACE)
            backspace();
        else if (keyEvent.getCode() == KeyCode.ENTER)
            enter();
    }

    public void virtualKeyboardInput(ActionEvent event) {
        Button button = (Button) event.getSource();
        String id = button.getId();
        Label tile = (Label) tileGrid.lookup("#" + currentColumn + "-" + currentRow);

    }

    public void letterInput(String letter) {
        System.out.println(letter); // debug

        Label tile = (Label) tileGrid.lookup("#" + currentColumn + "-" + currentRow);

        // will only add letter if tile is empty
        // Objects.equals(tile.getText().toLowerCase(), "")
        if (currentColumn < 5) {
            tile.setText(letter);
            // tile.getStyleClass().add("");
            currentWord += letter;
            currentColumn++;
        }
    }

    public void backspace() {

    }

    public void enter() {
        String valid = validWord(guessedWords.get(currentRow));
        if (valid.equals("valid"))
            currentRow++;
        else
            alert(valid);
    }

    public void alert(String message) {
        alertText.setText(message);
    }

    public String validWord(String inputWord) {
        if (inputWord.length() != 5)
            return "word is not a 5 letter word";
        else if (!dictionary.contains(inputWord)) // better word search algorithm
            return "word is not in dictionary";
        else if (guessedWords.contains(inputWord))
            return "word is already tried";
        else
            return "valid";
    }

    public void reset() {
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= columns; j++) {
                Label tile = (Label) tileGrid.lookup(j + "," + i);
                tile.setText(" ");
                tile.getStyleClass().clear();
                tile.getStyleClass().add("tile");
            }
        }

        getWord();
        guessedWords.clear();
        currentRow = 1; currentColumn = 1;
    }
}