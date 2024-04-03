package com.example.wordle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

import java.io.*;
import java.util.*;

public class Controller {
    public static Vector<String> words = new Vector<>();

    @FXML
    private Label debugText;
    public void getWords() {
        try {
            File file = new File ("words.txt");
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) words.add(reader.next());
            reader.close();
            debugText.setText("worked");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            debugText.setText("File not found");
            System.exit(0);
        }
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
}