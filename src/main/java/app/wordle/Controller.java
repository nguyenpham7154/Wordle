package app.wordle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;

import java.io.*;
import java.util.*;

public class Controller {
    private final String[][] keyboardLetters = {
            {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
            {"A", "S", "D", "F", "G", "H", "J", "K", "L"},
            {"ENTER", "Z", "X", "C", "V", "B", "N", "M", "DEL"}};
    private static ArrayList<String> dictionary = new ArrayList<String>();
    private static ArrayList<String> guessedWords = new ArrayList<String>();

    private String correctWord;
    private int rows = 6, columns = 5;
    private String currentWord = "";
    private int currentRow = 1, currentColumn = 1;
    private int gamesWon = 0, gameslost = 0, gamesPlayed = 0;

    @FXML public VBox root;
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
        //focus scene to get physical keyboard input
        root.setOnMouseClicked(e -> root.requestFocus());
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < keyboardLetters[i].length; j++) {
                Button key = new Button();
                String keyLetter = keyboardLetters[i][j];
                key.getStyleClass().add("key");
                key.setText(keyLetter);
                key.setId(keyLetter);
                //
                key.setOnAction(e -> virtualKeyboardInput(e));

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
        if (keyEvent.getCode().isLetterKey())
            onLetter(keyEvent.getText());
        else if (keyEvent.getCode() == KeyCode.BACK_SPACE)
            onDelete();
        else if (keyEvent.getCode() == KeyCode.ENTER)
            onEnter();
    }

    public void virtualKeyboardInput(ActionEvent event) {
        Button button = (Button) event.getSource();
        String id = button.getId();

        if (currentRow <= 6)
            if (id.length() == 1)
                onLetter(id);
            else if (id.equals("DEL"))
                onDelete();
            else if (id.equals("ENTER"))
                onEnter();

        // System.out.println(id); // debug
    }

    public void onLetter(String letter) {
        if (currentColumn <= 5) {
            Label tile = (Label) tileGrid.lookup("#" + currentColumn + "-" + currentRow);
            tile.setText(letter);
            currentWord += letter;
            currentColumn++;
            System.out.println(currentColumn + " " + currentWord); // debug
        }
    }

    public void onDelete() {
        currentColumn = Math.min(currentColumn-1, 1);
        Label tile = (Label) tileGrid.lookup("#" +currentColumn + "-" + currentRow);
        tile.setText("");
        currentWord = currentWord.substring(0, currentColumn-1);
        System.out.println(currentColumn + " " + currentWord); // debug
    }

    public void onEnter() {
        String isValid = checkWord(currentWord);
        if (isValid.equals("valid")) {
            guessedWords.add(currentWord);
            setColors(currentWord);
            currentRow++;
            currentColumn = 1;

            if (currentRow > 6)
                endgame(0);
            else if (correctWord.equals(currentWord))
                endgame(1);

            currentWord = "";
        } else {
            System.out.println(isValid);
        }


        System.out.println(currentColumn + " " + currentWord); // debug
    }

    public String checkWord(String currentWord) {
        currentWord = currentWord.toLowerCase();
        if (currentWord.length() != 5)
            return "Not enough letters";
        else if (!search(dictionary, currentWord)) // better word search algorithm
            return "Not in word list";
        else if (search(guessedWords, currentWord))
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
        gamesPlayed++;
        if (game == 1) {
            gamesWon++;
        }
        else {
            gameslost++;
        }
        // debug
        System.out.println("Games played: " + gamesPlayed);
        System.out.println("Games won:    " + gamesWon);
        System.out.println("Games lost:   " + gameslost);
    }

    @FXML protected void reset() {
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= columns; j++) {
                Label tile = (Label) tileGrid.lookup("#" + j + "-" + i);
                tile.setText(" ");
                tile.getStyleClass().clear();
                tile.getStyleClass().add("tile");
            }
        }

        for (int i = 0; i < 3; i++) {
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
        }

        getWord();
        guessedWords.clear();
        currentWord = "";
        currentRow = 1;
        currentColumn = 1;
    }

    @FXML protected void help() {
    }
    @FXML protected void scoreboard() {
    }
    @FXML protected void settings() {
    }
}
