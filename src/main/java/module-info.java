module org.example.chattcp {
    requires javafx.controls;
    requires javafx.fxml;


    opens Modelo to javafx.fxml;
    exports Modelo;
}