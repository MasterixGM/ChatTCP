package Modelo;


import Controlador.ControladorChat;
import Miscelaneos.Mensaje;
import Miscelaneos.TipoMensaje;
import Miscelaneos.Usuario;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Cliente {
    //Attributes
    private Usuario usuario;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    //Constructors
    public Cliente(Socket socket, Usuario usuario) throws IOException {
        setSocket(socket);
        setUsuario(usuario);
        setOut(socket);
        setIn(socket);
    }

    //Getters and setters
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public void setIn(Socket socket) throws IOException {
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(Socket socket) throws IOException {
        this.out = new ObjectOutputStream(socket.getOutputStream());
    }

    //Methods
    public void sendMensaje(Mensaje message) throws IOException {
        this.out.writeObject(message);
        this.out.flush();
    }

    public void processMensaje(Mensaje message){
        ControladorChat.getController().processMensaje(message);
    }

    public void startListener(){
        ClientListener clientListener = new ClientListener(this);
        clientListener.start();
    }

    public void stopListener() throws IOException {
        Mensaje message = new Mensaje(TipoMensaje.DISCONNECT, usuario, null);
        out.writeObject(message);
        out.flush();
    }

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

    public void processServerInput(Mensaje mensaje) {
        switch (mensaje.getTipoMensaje()){
            case MESSAGE -> processMensaje(mensaje);
            case DISCONNECT -> processDisconnect(mensaje);
        }
    }

    private void processDisconnect(Mensaje message) {
        if (!message.getUsuario().equals(usuario)){
            ControladorChat.getController().processDisconnect(message);
        }
    }

    public static class ClientListener extends Thread{
        //Attributes
        private Cliente client;
        private boolean isClosed;

        //Constructors
        public ClientListener(Cliente client) {
            this.client = client;
        }

        //Getters and setters

        public Cliente getClient() {
            return client;
        }

        public void setClient(Cliente client) {
            this.client = client;
        }

        //Methods

        @Override
        public void run() {
            while (!isClosed){
                try {
                    Mensaje mensaje = (Mensaje) client.getIn().readObject();
                    checkDisconnect(mensaje);
                    client.processServerInput(mensaje);
                } catch (IOException | ClassNotFoundException e) {
                    isClosed = true;
                    //e.printStackTrace();
                }
            }
        }

        private void checkDisconnect(Mensaje mensaje) {
            if (mensaje.getTipoMensaje() == TipoMensaje.DISCONNECT && mensaje.getUsuario().equals(client.getUsuario())){
                isClosed = true;
            }
        }
    }
}