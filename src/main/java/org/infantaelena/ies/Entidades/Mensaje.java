package org.infantaelena.ies.Entidades;

import java.io.Serializable;

/**
 * Clase que representa un mensaje enviado entre usuarios.
 */
public class Mensaje implements Serializable {
    private String mensaje; // El contenido del mensaje
    private Usuario usuario; // El usuario que envía el mensaje
    private TipoMensaje tipoMensaje; // El tipo de mensaje

    /**
     * Constructor de la clase Mensaje.
     *
     * @param tipoMensaje El tipo de mensaje.
     * @param usuario     El usuario que envía el mensaje.
     * @param mensaje     El contenido del mensaje.
     */
    public Mensaje(TipoMensaje tipoMensaje, Usuario usuario, String mensaje) {
        this.mensaje = mensaje;
        this.usuario = usuario;
        this.tipoMensaje = tipoMensaje;
    }

    /**
     * Método getter para obtener el contenido del mensaje.
     *
     * @return El contenido del mensaje.
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Método setter para establecer el contenido del mensaje.
     *
     * @param mensaje El contenido del mensaje.
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * Método getter para obtener el tipo de mensaje.
     *
     * @return El tipo de mensaje.
     */
    public TipoMensaje getTipoMensaje() {
        return tipoMensaje;
    }

    /**
     * Método setter para establecer el tipo de mensaje.
     *
     * @param tipoMensaje El tipo de mensaje.
     */
    public void setTipoMensaje(TipoMensaje tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }

    /**
     * Método getter para obtener el usuario que envía el mensaje.
     *
     * @return El usuario que envía el mensaje.
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Método setter para establecer el usuario que envía el mensaje.
     *
     * @param usuario El usuario que envía el mensaje.
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
