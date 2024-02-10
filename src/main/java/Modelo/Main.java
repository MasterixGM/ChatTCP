package Modelo;

import Controlador.ControladorLogin;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static Stage stagePrincipal;
    @Override
    public void start(Stage stage) throws Exception {
        stagePrincipal = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/Vista/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ControladorLogin loginController = fxmlLoader.getController();
        loginController.setStagePrincipal(stage);
        stage.setTitle("Chat");
        stage.setScene(scene);
        stage.show();
    }
}
