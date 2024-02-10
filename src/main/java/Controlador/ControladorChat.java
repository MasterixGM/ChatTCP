package Controlador;

import Miscelaneos.Mensaje;
import Miscelaneos.TipoMensaje;
import Modelo.RunApp;
import Modelo.Cliente;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * El ControladorChat gestiona la interfaz de usuario del chat y la interacción con el cliente.
 */
public class ControladorChat {
    private Cliente cliente; // El cliente asociado a este controlador
    private static ControladorChat controlador; // Instancia estática del controlador

    @FXML
    private VBox chatBox; // Contenedor de mensajes en la interfaz gráfica

    @FXML
    private ScrollPane chatScrollPane; // ScrollPane para mostrar los mensajes

    @FXML
    private TextArea mensajeTextField; // Campo de texto para ingresar mensajes

    // Constructor

    /**
     * Constructor de la clase ControladorChat.
     * Crea una nueva instancia del controlador.
     */
    public ControladorChat() {
        controlador = this;
    }

    // Métodos estáticos

    /**
     * Obtiene la instancia del controlador.
     *
     * @return La instancia del controlador.
     */
    public static ControladorChat getController(){
        return controlador;
    }

    // Getters y setters

    /**
     * Establece el cliente asociado al controlador.
     *
     * @param cliente El cliente asociado al controlador.
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    // Método de inicialización

    /**
     * Inicializa el controlador.
     * Configura el scroll pane para que siempre muestre el contenido en la parte inferior.
     * Configura el evento de cierre de la ventana principal para cerrar la conexión del cliente y salir de la aplicación.
     */
    public void initialize() {
        chatScrollPane.vvalueProperty().bind(chatBox.heightProperty());
        RunApp.stagePrincipal.setOnCloseRequest(e -> {
            cliente.close();
            Platform.exit();
        });
    }

    // Métodos de interacción con el servidor

    /**
     * Envía un mensaje al servidor.
     *
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    public void enviarMensaje() throws IOException {
        String msg = mensajeTextField.getText();
        if (!mensajeTextField.getText().trim().isEmpty()) {
            Mensaje mensaje = new Mensaje(TipoMensaje.MENSAJE, cliente.getUsuario(), msg);
            cliente.mandarMensaje(mensaje);
            mensajeTextField.clear();
        }
    }

    /**
     * Procesa un mensaje recibido del servidor.
     *
     * @param mensaje El mensaje recibido del servidor.
     */
    public void procesarMensaje(Mensaje mensaje){
        Platform.runLater(() -> {
            Label labelMensaje = new Label(mensaje.getUsuario().getUsername() + ": " + mensaje.getMensaje());
            chatBox.getChildren().add(labelMensaje);
        });
    }

    /**
     * Maneja un mensaje de desconexión del servidor.
     *
     */
    public void processDisconnect() {
        // Método vacío ya que no se implementa en este momento
    }
}
