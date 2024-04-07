package app.wordle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.*;
import java.util.*;

public class Controller {
    @FXML public Label debugText;
    @FXML protected void debugButton() {
        getWord();
        debugText.setText("correctword: " + correctWord);
    }
    @FXML public GridPane tileGrid;
    @FXML public GridPane keyboard;

    private ArrayList<String> dictionary = new ArrayList<String>();
    private ArrayList<String> guessedWords = new ArrayList<String>();
    private String correctWord;

    private int rows = 5;
    private int columns = 6;

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
        correctWord = dictionary.get((int) (Math.random() * (dictionary.size() - 1)));
    }


    public void loadTileGrid () {
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= columns; j++) {
                Label tile = new Label();
                // tile.getStyleClass().add("");
                tileGrid.add(tile, j, i); // can use as id
            }
        }
    }

    public void loadKeyboard() {

    }

    @FXML protected void keyboardInput(KeyEvent keyEvent) {
        if (keyEvent.getCode().isLetterKey()) {
            // letterInput()
        } else if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
            // backspace()
        } else if (keyEvent.getCode() == KeyCode.ENTER) {
            // enter()
        }
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

    public void setColor(String color) {
        switch (color) {
            case "green":
                //
            case "yellow":
                //
            case "black":
                //
        }
    }

    public void reset() {

    }
}