package org.infantaelena.ies.Controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.infantaelena.ies.Logica.Cliente;
import org.infantaelena.ies.Logica.RunApp;
import org.infantaelena.ies.Entidades.Usuario;
import org.infantaelena.ies.Modelo.UserCSV;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;

/**
 * org.infantalena.ies.Controlador para la ventana de inicio de sesión.
 * Gestiona la creación del usuario, la conexión al servidor y la transición a la ventana de chat.
 */
public class ControladorLogin {
    private static final int longitudMaxima = 25;

    @FXML
    private TextField campoUsuario, campoContrasena, campoPuerto, campoServidor;
    private Cliente cliente; // El cliente asociado a este controlador
    private Stage stagePrincipal; // La ventana principal de la aplicación

    // Getters y setters

    /**
     * Obtiene la ventana principal de la aplicación.
     *
     * @return La ventana principal.
     */
    public Stage getStagePrincipal() {
        return stagePrincipal;
    }

    /**
     * Establece la ventana principal de la aplicación.
     *
     * @param stagePrincipal La ventana principal.
     */
    public void setStagePrincipal(Stage stagePrincipal) {
        this.stagePrincipal = stagePrincipal;
    }

    // Método de inicialización

    /**
     * Inicializa el controlador.
     * Configura validaciones para los campos de texto.
     */
    public void initialize() {
        validacionTexto(campoUsuario, "^[a-zA-Z0-9_]*$", longitudMaxima);
        validacionTexto(campoContrasena, "^[a-zA-Z0-9_]*$", longitudMaxima);
        validacionTexto(campoPuerto, "\\d*", Integer.MAX_VALUE);
    }

    // Métodos de interacción con el servidor

    /**
     * Proceso de inicio de sesión.
     * Crea el usuario, se conecta al servidor y cambia a la ventana de chat si la conexión es exitosa.
     */
    @FXML
    private void inicioSesion() {
        try {
            boolean logged = false;
            if(pasoUsuario()) {
                logged = true; // Proceso de creación del usuario y conexión al servidor. Mientras no devuelva true, no se podrá continuar.
            }
            if(logged){

                boolean estaConectado = login(cliente); // Proceso de inicio de sesión

                // Cambiar a la ventana de chat si la conexión es exitosa
                if (estaConectado) {
                    pasoVentanaChat();
                } else {
                    mostrarAlerta("Error de conexión", "El nombre de usuario ya está registrado");
                }
            }
        } catch (InvalidParameterException e) {
            mostrarAlerta("Error en el nombre", "El campo nombre no puede estar vacío.");
        } catch (ConnectException | IllegalArgumentException e) {
            mostrarAlerta("Error de conexión", "No se puede establecer conexión al puerto.");
        } catch (UnknownHostException e) {
            mostrarAlerta("Error de Host", "No se ha podido conectar con el host.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Cambia a la ventana de chat.
     * Carga la ventana de chat y configura el controlador de chat con el cliente asociado.
     */
    private void pasoVentanaChat() {
        FXMLLoader loader = new FXMLLoader(RunApp.class.getResource("/Vista/Chat.fxml"));
        Scene scene;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ControladorChat controladorChat = loader.getController();
        controladorChat.setCliente(cliente);
        stagePrincipal.setScene(scene);
        stagePrincipal.show();
        stagePrincipal.centerOnScreen(); // Centra en la pantalla
    }

    /**
     * Proceso de creación del usuario y conexión al servidor.
     * Crea el usuario a partir de los datos ingresados por el usuario y establece la conexión con el servidor.
     *
     * Utiliza la clase UserCSV para autenticar/crear la cuenta.
     *
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    private boolean pasoUsuario() throws IOException {
        String nombreUsuario = campoUsuario.getText();
        String contrasena = campoContrasena.getText();
        int puerto = Integer.parseInt(campoPuerto.getText());
        String servidor = campoServidor.getText();
        if (nombreUsuario.isEmpty()){
            throw new InvalidParameterException();
        }
        else if(!UserCSV.authenticate(nombreUsuario, contrasena)){
            //Si no se ha podido autenticar, pero existe el usuario, creará el usuario y lo registrará.
            if(UserCSV.userExists(nombreUsuario)){
                mostrarAlerta("Error de inicio de sesión" , "La contraseña introducida no es válida.");
            }
            else{ //Si al contrario, no existe el usuario, se registrará ese usuario y pedirá al usuario que le vuelva a dar.
                if(UserCSV.createUser(nombreUsuario, contrasena)){
                    mostrarAlerta("Usuario no existe." , "Como no existe, se ha creado automáticamente. Vuelva a pulsar con esas credenciales para iniciar sesión.");
                }
            }
        }
        else {
            Usuario usuario = new Usuario(nombreUsuario);
            if (cliente == null) {
                Socket socket = new Socket(servidor, puerto);
                cliente = new Cliente(socket, usuario);
                return true;
            }
        }
        return false;
    }

    /**
     * Proceso de inicio de sesión.
     * Envía el usuario al servidor y lee la respuesta del servidor.
     * @param cliente El cliente asociado al controlador.
     * @return true si la conexión fue exitosa, false de lo contrario.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    private boolean login(Cliente cliente) throws IOException {
        ObjectOutputStream clientOutputStream = cliente.getOut();
        ObjectInputStream clientInputStream = cliente.getIn();
        Usuario usuario = cliente.getUsuario();

        // Enviar el objeto Usuario al servidor
        clientOutputStream.writeObject(usuario);
        clientOutputStream.flush();

        // Leer la respuesta del servidor
        boolean estaConexEstablecida = clientInputStream.readBoolean();

        // Si la conexión se estableció correctamente, iniciar el escuchador del cliente
        if (estaConexEstablecida){
            cliente.startListener();
        }

        return estaConexEstablecida;
    }

    // Otros métodos

    /**
     * Muestra una alerta en la interfaz gráfica.
     *
     * @param titulo El título de la alerta.
     * @param mensaje El mensaje de la alerta.
     */
    private void mostrarAlerta(String titulo, String mensaje){
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Configura validaciones para el texto ingresado en un campo de texto.
     *
     * @param textField El campo de texto.
     * @param regex La expresión regular para la validación.
     * @param maxLength La longitud máxima permitida para el texto.
     */
    private void validacionTexto(TextField textField, String regex, int maxLength) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches(regex) || newValue.length() > maxLength) {
                textField.setText(oldValue);
            }
        });
    }
}
