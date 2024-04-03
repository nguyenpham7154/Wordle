package app.wordle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

import java.io.*;
import java.util.*;

public class Controller {
    public static ArrayList<String> dictionary = new ArrayList<String>();
    public static ArrayList<String> guessedWords = new ArrayList<String>();
    public static String correctWord;
    public void getWords() {
        try {
            Scanner scanner = new Scanner(new File("src/main/resources/app/wordle/dictionary.txt"));
            while (scanner.hasNextLine()) dictionary.add(scanner.next());
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
        correctWord = dictionary.get((int) (Math.random() * (dictionary.size() - 1)));
        debugText.setText("correctword: " + correctWord);
    }
    public String validWord(String inputWord) {
        if (inputWord.length() != 5)
            return "word is not a 5 letter word";
        else if (!dictionary.contains(inputWord))
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

    @FXML
    private Label debugText;
    @FXML
    protected void debugButton() {
        getWords();
    }
}