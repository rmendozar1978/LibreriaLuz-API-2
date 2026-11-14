package com.librerialuz.dto;

import com.librerialuz.model.Usuario;

/**
 * Datos públicos de un usuario. Nunca incluye la contraseña ni su hash.
 */
public class UsuarioResponse {

    private final Long id;
    private final String nombre;
    private final String correo;

    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.correo = usuario.getCorreo();
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }
}
