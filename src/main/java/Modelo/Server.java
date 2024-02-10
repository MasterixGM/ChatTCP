package Modelo;

import Miscelaneos.Mensaje;
import Miscelaneos.Usuario;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private static final HashMap<Usuario, ObjectOutputStream> usuarios = new HashMap<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Parte de Introducción de Puerto.
        int port;
        int nUsuarios;

        while (true) {
            try {
                System.out.println("Elige el numero de puerto deseado: ");
                port = sc.nextInt();
                try (ServerSocket testSocket = new ServerSocket(port)) {
                    break; // Si se detecta un puerto en uso se corta y sigue el bucle
                } catch (BindException errorPuerto) {
                    System.err.println("El puerto: " + port + " ya esta siendo usado, escoje otro puerto");
                }
            } catch (Exception errorPuertoNulo) {
                System.err.println("Por favor ingresa un numero de puerto valido.");
                sc.nextLine();
            }
        }

        // Parte de Introducción de Usuarios.
        System.out.println("Elige el numero de conexiones SIMULTANEAS que deseas: ");
        while (true) {
            try {
                nUsuarios = sc.nextInt();
                if (nUsuarios > 0) {
                    break;
                } else {
                    System.err.println("Introduce un numero de Conexiones Simultaneas adecuada");
                }
            } catch (Exception errorNumerico) {
                System.err.println("Por favor, Ingresa un número válido.");
                sc.nextLine();
            }
        }

        try (ServerSocket listener = new ServerSocket(port)) { //TODO: Si se meten mas usuarios de los establecidos la conexion palma muy hard
            int clientesConectados = 0;
            while (clientesConectados < nUsuarios) {
                new Handler(listener.accept()).start();
                clientesConectados++;
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    private static class Handler extends Thread {
        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public Handler(Socket socket) {
            setSocket(socket);
            System.out.println("Cliente Conectado");
            try {
                setIn(socket);
                setOut(socket);
                System.out.println("Streams Creados");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

        public void setIn(Socket in) throws IOException {
            this.in = new ObjectInputStream(this.socket.getInputStream());
        }

        public ObjectOutputStream getOut() {
            return out;
        }

        public void setOut(Socket out) throws IOException {
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
        }

        public void run() {
            boolean isLogged;
            try {
                do {
                    isLogged = login();
                } while (!isLogged);

            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }


            while (socket.isConnected()) {
                try {
                    Mensaje mensaje = (Mensaje) in.readObject();
                    processClientOutput(mensaje);
                } catch (IOException | ClassNotFoundException e) {
                    close();
                    //e.printStackTrace();
                    break;
                }
            }
        }

        private void processClientOutput(Mensaje mensaje) throws IOException {
            switch (mensaje.getTipoMensaje()) {
                case MESSAGE:
                    processMessage(mensaje);
                    break;
                case DISCONNECT:
                    processDisconnect(mensaje);
                    break;
            }
        }

        private void processDisconnect(Mensaje message) throws IOException {
            Usuario usuario = message.getUsuario();
            Server.usuarios.remove(usuario);
            out.writeObject(message);
            out.flush();
            close();
        }

        private void processMessage(Mensaje message) throws IOException {
            for (ObjectOutputStream out : Server.usuarios.values()) {
                out.writeObject(message);
                out.flush();
            }
        }

        private boolean login() throws IOException, ClassNotFoundException {
            Usuario usuario = (Usuario) in.readObject();
            if (Server.usuarios.containsKey(usuario)) {
                this.out.writeBoolean(false);
                this.out.flush();
                return false;
            } else {
                Server.usuarios.put(usuario, out);
                System.out.println(usuario.getUsername() + " añadido");
                this.out.writeBoolean(true);
                this.out.flush();
                return true;
            }
        }

        public void close() {
            try {
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
    }
}
