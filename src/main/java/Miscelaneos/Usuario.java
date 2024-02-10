package Miscelaneos;

import java.io.Serializable;

/**
 * La clase Usuario representa un usuario en el sistema de chat.
 * Cada usuario tiene un nombre de usuario único.
 */
public class Usuario implements Serializable {
    private String username; // El nombre de usuario del usuario

    // Constructor

    /**
     * Constructor de la clase Usuario.
     * Crea un nuevo usuario con el nombre de usuario especificado.
     *
     * @param username El nombre de usuario del usuario.
     */
    public Usuario(String username) {
        this.username = username;
    }

    // Métodos

    /**
     * Obtiene el nombre de usuario del usuario.
     *
     * @return El nombre de usuario del usuario.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Establece el nombre de usuario del usuario.
     *
     * @param username El nuevo nombre de usuario del usuario.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
