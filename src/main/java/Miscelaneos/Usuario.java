package Miscelaneos;

import java.io.Serializable;


public class Usuario implements Serializable {
    private String username;

    public Usuario(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}