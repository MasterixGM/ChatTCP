package Controlador;

import Miscelaneos.Mensaje;
import Miscelaneos.TipoMensaje;
import Modelo.Main;
import Modelo.Cliente;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ControladorChat {
    private Cliente cliente;
    private static ControladorChat controlador;

    @FXML
    private VBox chatBox;

    @FXML
    private ScrollPane chatScrollPane;

    @FXML
    private TextArea mensajeTextField;

    public ControladorChat() {
        controlador = this;
    }

    public static ControladorChat getController(){
        return controlador;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void initialize() {
        // Configurar el scroll pane para que siempre muestre el contenido en la parte inferior
        chatScrollPane.vvalueProperty().bind(chatBox.heightProperty());
        Main.stagePrincipal.setOnCloseRequest(e -> {
            cliente.close();
            Platform.exit();
        });
    }
    
    public void enviarMensaje() throws IOException {
        String msg = mensajeTextField.getText();
        if (!mensajeTextField.getText().trim().isEmpty()) {
            Mensaje mensaje = new Mensaje(TipoMensaje.MESSAGE, cliente.getUsuario(), msg);
            cliente.sendMensaje(mensaje);
            mensajeTextField.clear();
        }
    }

    public void processMensaje(Mensaje mensaje){
        Platform.runLater(() -> {
            Label labelMensaje = new Label(mensaje.getUsuario().getUsername() + ": " + mensaje.getMensaje());
            chatBox.getChildren().add(labelMensaje);
        });
    }

    public void processDisconnect(Mensaje message) {

    }
}