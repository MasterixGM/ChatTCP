package Miscelaneos;

import java.io.Serializable;

public class Mensaje implements Serializable {
    private String message;
    private Usuario usuario;
    private TipoMensaje messageType;

    public Mensaje(TipoMensaje messageType, Usuario usuario, String message) {
        this.message = message;
        this.usuario = usuario;
        this.messageType = messageType;
    }

    public String getMensaje() {
        return message;
    }

    public void setMensaje(String message) {
        this.message = message;
    }

    public TipoMensaje getTipoMensaje() {
        return messageType;
    }

    public void setTipoMensaje(TipoMensaje messageType) {
        this.messageType = messageType;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}