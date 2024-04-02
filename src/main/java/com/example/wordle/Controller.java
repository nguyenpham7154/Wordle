package com.example.wordle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.*;
import java.util.*;

public class Controller {
    public static Vector<String> words = new Vector<String>();

    @FXML
    private Label debugText;

    @FXML
    protected void onHelloButtonClick() {
        debugText.setText("Wordle");
    }

     void getWords() {
        try {
            File file = new File ("words.txt");
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) words.add(reader.next());
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found");
            System.exit(0);
        }
    }
}