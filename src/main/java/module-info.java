module org.example.chattcp {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.infantaelena.ies.Modelo to javafx.fxml;
    opens org.infantaelena.ies.Controlador to javafx.fxml;

    exports org.infantaelena.ies.Modelo;
    exports org.infantaelena.ies.Controlador;
    exports org.infantaelena.ies;
    opens org.infantaelena.ies to javafx.fxml;
    exports org.infantaelena.ies.Entidades;
    opens org.infantaelena.ies.Entidades to javafx.fxml;
    exports org.infantaelena.ies.Logica;
    opens org.infantaelena.ies.Logica to javafx.fxml;
}
