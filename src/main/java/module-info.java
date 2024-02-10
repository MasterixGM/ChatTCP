module org.example.chattcp {
    requires javafx.controls;
    requires javafx.fxml;

    opens Modelo to javafx.fxml;
    opens Controlador to javafx.fxml;

    exports Modelo;
    exports Controlador;
}
