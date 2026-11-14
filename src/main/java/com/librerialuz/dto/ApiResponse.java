package com.librerialuz.dto;

/**
 * Respuesta JSON uniforme para informar el resultado de una operación o un error.
 */
public class ApiResponse {

    private String mensaje;

    public ApiResponse() {
    }

    public ApiResponse(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
