package app.wordle;

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
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;

// apply 2d array of buttons or hashmap
// change to 1d array, change enter and del to symbol
public class Controller {
    private HashMap<String, Button> buttonMap;

    private 

    private final String[] keyboardLetters = {
        "QWERTYUIOP", "ASDFGHJKL", "ENTER", "ZXCVBNM", "DEL"
    }

    public void loadKeyboard() {
        for (int i = 0; i < 5; i++) {
            String keyLetter = keyboardLetters[i];
            // Adding large keys
            if (i == 2 || i == 4) {
                Button key = new Button();
                key.getStyleClass().add("key");
                key.getStyleClass().add("largeKey");
                key.setText(keyLetter);
                key.setId(keyLetter);
                key.setOnAction(this::virtualKeyboardInput);

                keyboard3.add(key, j, i);
            }
            // adding normal keys
            else for (int j = 0; j < keyboardLetters[i].length(); j++) {
                Button key = new Button();
                keyLetter = String.valueOf(keyboardLetters[i].charAt(j));

                key.getStyleClass().add("key");
                key.setText(keyLetter);
                key.setId(keyLetter);
                key.setOnAction(this::virtualKeyboardInput);

                if (i == 0)
                    keyboard1.add(key, j, i);
                else if (i == 1)
                    keyboard2.add(key, j, i);
                else 
                    keyboard3.add(key, j, i);
            }
        }
    }

    private static ArrayList<String> dictionary = new ArrayList<String>();
    private static ArrayList<String> guessedWords = new ArrayList<String>();

    private String correctWord;
    private int maxRows = 6, maxColumns = 5;
    private String currentWord = "";
    private int currentRow = 1, currentColumn = 1;
    private int gamesWon = 0, gameslost = 0, gamesPlayed = 0, totalGusses = 0;

    @FXML private VBox root;
    @FXML private GridPane tileGrid;
    @FXML private GridPane keyboard1, keyboard2, keyboard3;


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

                if (i == 0)
                    keyboard1.add(key, j, i);
                else if (i == 1)
                    keyboard2.add(key, j, i);
                else {
                    if (j == 0 || j == 8)
                        key.getStyleClass().add("largeKey");
                    keyboard3.add(key, j, 0);
                }
            }
        }
    }

    public void physicalKeyboardInput(KeyEvent keyEvent) {
        KeyCode keycode = keyEvent.getCode();
        if (currentRow <= maxRows)
            if (keycode.isLetterKey())
                onLetter(keyEvent.getText());
            else if (keycode == KeyCode.BACK_SPACE)
                onDelete();
            else if (keycode == KeyCode.ENTER)
                onEnter();
    }

    public void virtualKeyboardInput(ActionEvent event) {
        Button button = (Button) event.getSource();
        String id = button.getId();

        if (currentRow <= maxRows)
            if (id.length() == 1)
                onLetter(id);
            else if (id.equals("DEL"))
                onDelete();
            else if (id.equals("ENTER"))
                onEnter();

    }

    public void onLetter(String letter) {
        if (currentColumn <= maxColumns) {
            ((Label) tileGrid.lookup("#" + currentColumn + "-" + currentRow)).setText(letter.toUpperCase());
            currentWord += letter;
            currentColumn++;
        }
    }

    public void onDelete() {
        currentColumn = Math.max(currentColumn-1, 1);
        Label tile = (Label) tileGrid.lookup("#" +currentColumn + "-" + currentRow);
        tile.setText("");
        currentWord = currentWord.substring(0, currentColumn-1);
    }

    public void onEnter() {
        String isValid = checkWord(currentWord);
        if (isValid.equals("valid")) {
            System.out.println(currentWord); // debug
            guessedWords.add(currentWord);
            setColors(currentWord);
            currentRow++;
            currentColumn = 1;


            if (correctWord.equals(currentWord))
                endgame(1);
            else if (currentRow > maxRows)
                endgame(0);

            currentWord = "";
        } else {
            System.out.println(isValid);
        }
    }

    public String checkWord(String currentWord) {
        currentWord = currentWord.toLowerCase();
        if (currentWord.length() != maxColumns)
            return "Not enough letters";
        else if (!search(dictionary, currentWord))
            return "Not in word list";
        else if (guessedWords.contains(currentWord))
            return "Word already tried";
        else
            return "valid";
    }

    // Binary search
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

    public void setColors(String currentWord) {
        for (int i = 0; i < 5; i++) {
            String letter = String.valueOf(currentWord.toLowerCase().charAt(i));
            Label tile = (Label) tileGrid.lookup("#" + (i+1) + "-" + currentRow);

            /*
            Label key;
            if (keyboardLetters[0])
                key = (Label) keyboard1.lookup("#" + letter);
            else if (keyboardLetters[1])
                key = (Label) keyboard2.lookup("#" + letter);
            else if (keyboardLetters[2])
                key = (Label) keyboard3.lookup("#" + letter);
            */

            if (correctWord.charAt(i) == letter.charAt(0))
                tile.getStyleClass().add("green");
            else if (correctWord.contains(letter))
                tile.getStyleClass().add("yellow");
            else
                tile.getStyleClass().add("gray");
        }
    }

    public void endgame(int game) {
        totalGusses += currentRow-1;
        gamesPlayed++;
        if (game == 1) {
            gamesWon++;
        }
        else {
            gameslost++;
        }

        // debug
        System.out.println("Games played:    " + gamesPlayed);
        System.out.println("Games won:       " + gamesWon);
        System.out.println("Games lost:      " + gameslost);
        System.out.println("Average guesses: " + Math.floor(10.0*totalGusses/gamesPlayed)/10.0);
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

        /*for (int i = 0; i < 3; i++) {
            for (int j = 0; j < keyboardLetters[i].length; j++) {
                Button key;
                String keyLetter = keyboardLetters[i][j];

                if (i == 0)
                    key = (Button) keyboard1.lookup("#" +keyLetter);
                else if (i == 1)
                    key = (Button) keyboard2.lookup("#" +keyLetter);
                else
                    key = (Button) keyboard3.lookup("#" +keyLetter);

                key.getStyleClass().clear();
                key.getStyleClass().add("key");

                if (i == 2 && (j == 0 || j == 8))
                    key.getStyleClass().add("largeKey");
            }
        }*/
        tileGrid.requestFocus();
    }

    @FXML protected void help() {
    }
    @FXML protected void scoreboard() {
    }
    @FXML protected void settings() {
    }
}
