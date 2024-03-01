package org.infantaelena.ies.Controlador;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.infantaelena.ies.Entidades.Mensaje;
import org.infantaelena.ies.Entidades.TipoMensaje;
import org.infantaelena.ies.Logica.Cliente;
import org.infantaelena.ies.Logica.RunApp;
import org.infantaelena.ies.Main;

import java.io.IOException;

/**
 * El ControladorChat gestiona la interfaz de usuario del chat y la interacción con el cliente.
 */
public class ControladorChat {
    public Text nombreUserText; // Campo para cambiar en nombre del usuario al que se este escribiendo
    public Text estadoUserText; // Campo para cambiar el ESTADO del usuario al que se este escribiendo (Enum Preferible Online, Offline)
    public Button enviarButton; // Boton de Envio de mensaje
    public Button salirButton; // Boton de Menu lateral para "Salir de la app" (Te manda de vuelta al login tontito)
    public ScrollPane usersScrollPane; // ScrollPane para Usuarios
    public VBox usersBox; // Vbox para los Usuarios
    //TODO: Los Añadidos arriba se deben implementar he dejado por el FXML en caso de implementaciones futuras opciones como opcionesButton entre otros.
    private Cliente cliente; // El cliente asociado a este controlador
    private static ControladorChat controlador; // Instancia estática del controlador

    @FXML
    private VBox chatBox; // Contenedor de mensajes en la interfaz gráfica

    @FXML
    private ScrollPane chatScrollPane; // ScrollPane para mostrar los mensajes

    @FXML
    private TextArea mensajeTextArea; // Campo de texto para ingresar mensajes

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

    public void setNombreUserText (String nombreUsuario){
        nombreUserText.setText(nombreUsuario);
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

        mensajeTextArea.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {// Agregado para evitar saltos de linea al pulsar enter.
            if (ke.getCode().equals(KeyCode.ENTER)) {
                try {
                    enviarMensaje();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ke.consume(); // Consumir el evento para evitar el comportamiento predeterminado del TextArea
            }
        });
    }

    // Métodos de interacción con el servidor

    /**
     * Envía un mensaje al servidor.
     *
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    public void enviarMensaje() throws IOException {
        String msg = mensajeTextArea.getText();
        if (!mensajeTextArea.getText().trim().isEmpty()) {
            Mensaje mensaje = new Mensaje(TipoMensaje.MENSAJE, cliente.getUsuario(), msg);
            cliente.mandarMensaje(mensaje);
            mensajeTextArea.clear();
        }
    }

    /**
     * Envía un mensaje al servidor cuando pulsas ENTER.
     *
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    public void enviarMensajeTeclado(KeyEvent event) throws IOException { //TODO: Esta linkeado al FXML pero al darle al enter me da la joda y se hace un salto de linea
        if (event.getCode() == KeyCode.ENTER) {
            enviarMensaje();
        }
    }


    /**
     * Procesa un mensaje recibido del servidor.
     *
     * @param mensaje El mensaje recibido del servidor.
     */
    public void procesarMensaje(Mensaje mensaje){
        String nombreUsuario = mensaje.getUsuario().getUsername();
        String textoMensaje = mensaje.getMensaje();
        FXMLLoader textoLayout = new FXMLLoader(Main.class.getResource("/Vista/Texto.fxml"));
        try {
            AnchorPane root =  textoLayout.load();
            ControladorTexto controlador = textoLayout.getController();
            controlador.setNombreCliente(nombreUsuario);
            controlador.setMensaje(textoMensaje);
        Platform.runLater(() -> {
            chatBox.getChildren().add(root);
            chatBox.getChildren().add(new Label("\n"));
        });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Maneja un mensaje de desconexión del servidor.
     *
     */
    public void processDisconnect() {
        // Método vacío ya que no se implementa en este momento
    }
}
