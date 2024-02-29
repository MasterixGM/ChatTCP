package org.infantaelena.ies.Logica;

import org.infantaelena.ies.Entidades.Mensaje;
import org.infantaelena.ies.Entidades.Usuario;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Clase que representa el servidor de la aplicación de mensajería.
 */
public class Servidor {
    // HashMap para almacenar usuarios y sus ObjectOutputStream asociados
    private static final HashMap<Usuario, ObjectOutputStream> usuarios = new HashMap<>();

    /**
     * Método principal que inicia el servidor y gestiona las conexiones de los clientes.
     *
     * @param args Los argumentos de línea de comandos (no se utilizan en este caso).
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Parte de Introducción de Puerto.
        int port;
        int nUsuarios;

        // Bucle para asegurar que se elija un puerto disponible
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

        try (ServerSocket listener = new ServerSocket(port)) {
            int clientesConectados = 0;
            while (clientesConectados < nUsuarios) {
                new Handler(listener.accept()).start(); // Acepta conexiones entrantes y crea un nuevo hilo de manejo
                clientesConectados++;
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    /**
     * Clase interna para manejar las conexiones de los clientes.
     */
    private static class Handler extends Thread {
        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        // Constructor de Handler
        public Handler(Socket socket) {
            setSocket(socket);
            System.out.println("Cliente Conectado");
            try {
                setIn(socket);
                setOut(socket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Método principal del hilo de manejo
        @Override
        public void run() {
            boolean isLogged;
            try {
                // Bucle para el proceso de inicio de sesión
                do {
                    isLogged = login();
                } while (!isLogged);

            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            // Bucle para manejar la comunicación con el cliente
            while (socket.isConnected()) {
                try {
                    Mensaje mensaje = (Mensaje) in.readObject();
                    procesoOutputCliente(mensaje);
                } catch (IOException | ClassNotFoundException e) {
                    cerrar();
                    e.fillInStackTrace();
                    break;
                }
            }
        }

        // Proceso de salida para el cliente
        private void procesoOutputCliente(Mensaje mensaje) throws IOException {
            switch (mensaje.getTipoMensaje()) {
                case MENSAJE:
                    procesoMensaje(mensaje);
                    break;
                case DESCONECTADO:
                    procesoDesconectar(mensaje);
                    break;
            }
        }

        // Proceso para desconectar al cliente
        private void procesoDesconectar(Mensaje mensaje) throws IOException {
            Usuario usuario = mensaje.getUsuario();
            usuarios.remove(usuario);
            out.writeObject(mensaje);
            out.flush();
            cerrar();
        }

        // Proceso para enviar mensaje a todos los usuarios conectados
        private void procesoMensaje(Mensaje mensaje) throws IOException {
            for (ObjectOutputStream out : Servidor.usuarios.values()) {
                out.writeObject(mensaje);
                out.flush();
            }
        }

        // Método para el proceso de inicio de sesión
        private boolean login() throws IOException, ClassNotFoundException {
            Usuario usuario = (Usuario) in.readObject();
            if (usuarios.containsKey(usuario)) {
                this.out.writeBoolean(false);
                this.out.flush();
                return false;
            } else {
                usuarios.put(usuario, out);
                System.out.println(usuario.getUsername() + " añadido");
                this.out.writeBoolean(true);
                this.out.flush();
                return true;
            }
        }

        // Método para cerrar los streams y el socket
        public void cerrar() {
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

        // Getters y Setters
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
    }
}
