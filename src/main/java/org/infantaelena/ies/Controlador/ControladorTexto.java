package org.infantaelena.ies.Controlador;

import javafx.scene.control.Label;

public class ControladorTexto {

    public Label labelCliente;
    public Label labelMensaje;

    public void setNombreCliente(String nombreCliente){
        labelCliente.setText(nombreCliente.toUpperCase());
    }

    public void setMensaje(String mensaje){
        labelMensaje.setText(mensaje.trim());
    }
}