package Modelo;

import Controlador.ControladorChat;
import Miscelaneos.Mensaje;
import Miscelaneos.TipoMensaje;
import Miscelaneos.Usuario;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * La clase Cliente representa un cliente en el sistema de chat.
 */
public class Cliente {
    // Atributos
    private Usuario usuario; // El usuario del cliente
    private Socket socket; // El socket del cliente para la conexión al servidor
    private ObjectInputStream in; // Flujo de entrada para recibir mensajes del servidor
    private ObjectOutputStream out; // Flujo de salida para enviar mensajes al servidor

    // Constructores

    /**
     * Constructor de la clase Cliente.
     * Este constructor inicializa los flujos de entrada y salida del cliente utilizando el socket proporcionado.
     *
     * @param socket  El socket para la conexión al servidor.
     * @param usuario El usuario del cliente.
     * @throws IOException Si ocurre un error de entrada/salida durante la creación de los flujos.
     */
    public Cliente(Socket socket, Usuario usuario) throws IOException {
        setSocket(socket);
        setUsuario(usuario);
        setOut(socket);
        setIn(socket);
    }

    // Getters y setters

    /**
     * Obtiene el usuario del cliente.
     *
     * @return El usuario del cliente.
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Establece el usuario del cliente.
     *
     * @param usuario El usuario del cliente.
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Obtiene el socket del cliente.
     *
     * @return El socket del cliente.
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Establece el socket del cliente.
     *
     * @param socket El socket del cliente.
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * Obtiene el flujo de entrada del cliente.
     *
     * @return El flujo de entrada del cliente.
     */
    public ObjectInputStream getIn() {
        return in;
    }

    /**
     * Establece el flujo de entrada del cliente utilizando el socket proporcionado.
     *
     * @param socket El socket del cliente.
     * @throws IOException Si ocurre un error de entrada/salida durante la creación del flujo de entrada.
     */
    public void setIn(Socket socket) throws IOException {
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Obtiene el flujo de salida del cliente.
     *
     * @return El flujo de salida del cliente.
     */
    public ObjectOutputStream getOut() {
        return out;
    }

    /**
     * Establece el flujo de salida del cliente utilizando el socket proporcionado.
     *
     * @param socket El socket del cliente.
     * @throws IOException Si ocurre un error de entrada/salida durante la creación del flujo de salida.
     */
    public void setOut(Socket socket) throws IOException {
        this.out = new ObjectOutputStream(socket.getOutputStream());
    }

    // Métodos

    /**
     * Envía un mensaje al servidor.
     *
     * @param mensaje El mensaje a enviar.
     * @throws IOException Si ocurre un error de entrada/salida durante el envío del mensaje.
     */
    public void mandarMensaje(Mensaje mensaje) throws IOException {
        this.out.writeObject(mensaje);
        this.out.flush();
    }

    /**
     * Procesa un mensaje recibido del servidor.
     *
     * @param mensaje El mensaje recibido del servidor.
     */
    public void processMensaje(Mensaje mensaje){
        ControladorChat.getController().procesarMensaje(mensaje);
    }

    /**
     * Inicia el hilo del escuchador del cliente.
     * Este método crea y arranca un nuevo hilo para escuchar los mensajes entrantes del servidor.
     */
    public void startListener(){
        Listener listener = new Listener(this);
        listener.start();
    }

    /**
     * Detiene el escuchador del cliente y envía un mensaje de desconexión al servidor.
     *
     * @throws IOException Si ocurre un error de entrada/salida durante el envío del mensaje de desconexión.
     */
    public void stopListener() throws IOException {
        Mensaje mensaje = new Mensaje(TipoMensaje.DESCONECTADO, usuario, null);
        out.writeObject(mensaje);
        out.flush();
    }

    /**
     * Cierra los flujos y el socket del cliente.
     * Este método cierra todos los flujos de entrada/salida y el socket del cliente.
     */
    public void close() {
        try {
            stopListener();

            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Procesa la entrada del servidor.
     * Este método procesa un mensaje recibido del servidor y realiza acciones según el tipo de mensaje.
     *
     * @param mensaje El mensaje recibido del servidor.
     */
    public void processServerInput(Mensaje mensaje) {
        switch (mensaje.getTipoMensaje()){
            case MENSAJE:
                processMensaje(mensaje);
                break;

            case DESCONECTADO:
                processDisconnect(mensaje);
                break;

            default:
                break;
        }
    }

    // Métodos privados

    /**
     * Procesa un mensaje de desconexión del servidor.
     * Este método maneja un mensaje de desconexión del servidor y realiza las acciones necesarias.
     *
     * @param mensaje El mensaje de desconexión recibido del servidor.
     */
    private void processDisconnect(Mensaje mensaje) {
        if (!mensaje.getUsuario().equals(usuario)){
            ControladorChat.getController().processDisconnect();
        }
    }

    // Clase interna para el escuchador del cliente

    /**
     * La clase Listener representa un hilo que escucha los mensajes entrantes del servidor.
     */
    public static class Listener extends Thread{
        // Atributos
        private final Cliente cliente; // El cliente al que pertenece este escuchador
        private boolean estaCerrado; // Indica si el escuchador está cerrado o no

        // Constructor

        /**
         * Constructor de la clase Listener.
         *
         * @param cliente El cliente al que pertenece este escuchador.
         */
        public Listener(Cliente cliente) {
            this.cliente = cliente;
        }

        // Métodos

        /**
         * Ejecuta el hilo del escuchador.
         * Este método se ejecuta cuando se inicia el hilo del escuchador y se encarga de escuchar los mensajes entrantes del servidor.
         */
        @Override
        public void run() {
            while (!estaCerrado){
                try {
                    Mensaje mensaje = (Mensaje) cliente.getIn().readObject();
                    checkDesconectado(mensaje);
                    cliente.processServerInput(mensaje);
                } catch (IOException | ClassNotFoundException e) {
                    estaCerrado = true;
                    //e.printStackTrace();
                }
            }
        }

        /**
         * Verifica si el cliente ha sido desconectado.
         * Este método verifica si el cliente ha sido desconectado mediante la recepción de un mensaje de desconexión del servidor.
         *
         * @param mensaje El mensaje recibido del servidor.
         */
        private void checkDesconectado(Mensaje mensaje) {
            if (mensaje.getTipoMensaje() == TipoMensaje.DESCONECTADO && mensaje.getUsuario().equals(cliente.getUsuario())){
                estaCerrado = true;
            }
        }
    }
}