package Controlador;

import Miscelaneos.Usuario;
import Modelo.Main;
import Modelo.Cliente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;

public class ControladorLogin {
    private static final int longitudMaxima = 25;

    @FXML
    private TextField campoUsuario, campoPuerto, campoServidor;
    private Cliente cliente;
    private Stage stagePrincipal;

    public Stage getStagePrincipal() {
        return stagePrincipal;
    }

    public void setStagePrincipal(Stage stagePrincipal) {
        this.stagePrincipal = stagePrincipal;
    }


    public void initialize() {
        validacionTexto(campoUsuario, "^[a-zA-Z0-9_]*$", longitudMaxima);
        validacionTexto(campoPuerto, "\\d*", Integer.MAX_VALUE);
    }

    @FXML
    private void inicioSesion() {
        try {
            pasoUsuario();

            boolean estaConectado = login(cliente);

            if (estaConectado) {
                pasoVentanaChat();
            } else {
                mostrarAlerta("Error de conexion", "El nombre de usuario ya esta registrado");
            }
        } catch (InvalidParameterException e) {
            mostrarAlerta("Error en el nombre", "El campo nombre no puede estar vacio.");
        } catch (ConnectException | IllegalArgumentException e) {
            mostrarAlerta("Error de conexion", "No se puede establecer conexion al puerto.");
        } catch (UnknownHostException e) {
            mostrarAlerta("Error de Host", "No se ha podido conectar con el host.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void pasoVentanaChat() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/Vista/Chat.fxml"));
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
    }


    private void pasoUsuario() throws IOException {
        String nombreUsuario = campoUsuario.getText();
        int puerto = Integer.parseInt(campoPuerto.getText());
        String servidor = campoServidor.getText();
        if (nombreUsuario.isEmpty()){
            throw new InvalidParameterException();
        }
        Usuario usuario = new Usuario(nombreUsuario);
        if (cliente == null){
            Socket socket = new Socket(servidor, puerto);
            cliente = new Cliente(socket, usuario);
        }
    }
    private boolean login(Cliente cliente) throws IOException {

        ObjectOutputStream clientOutputStream = cliente.getOut();
        ObjectInputStream clientInputStream = cliente.getIn();
        Usuario usuario = cliente.getUsuario();

        clientOutputStream.writeObject(usuario);
        clientOutputStream.flush();

        boolean estaConexEstablecida = clientInputStream.readBoolean();

        if (estaConexEstablecida){
            cliente.startListener();
        }

        return estaConexEstablecida;
    }

    private void mostrarAlerta(String titulo, String mensaje){
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void validacionTexto(TextField textField, String regex, int maxLength) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches(regex) || newValue.length() > maxLength) {
                textField.setText(oldValue);
            }
        });
    }
}
