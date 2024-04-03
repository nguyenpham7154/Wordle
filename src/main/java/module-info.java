module com.example.wordle {
    requires javafx.controls;
    requires javafx.fxml;

    opens app.wordle to javafx.fxml;
    exports app.wordle;
}