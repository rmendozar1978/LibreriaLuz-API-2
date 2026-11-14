package com.librerialuz.dto;

/**
 * Respuesta de un inicio de sesión correcto: mensaje y datos públicos del usuario.
 */
public class LoginResponse {

    private final String mensaje;
    private final UsuarioResponse usuario;

    public LoginResponse(String mensaje, UsuarioResponse usuario) {
        this.mensaje = mensaje;
        this.usuario = usuario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public UsuarioResponse getUsuario() {
        return usuario;
    }
}
