package com.librerialuz.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Datos para crear o actualizar una categoría.
 */
public class CategoriaRequest {

    @NotBlank(message = "El nombre de la categoría es obligatorio.")
    @Size(max = 60, message = "El nombre de la categoría no puede superar 60 caracteres.")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres.")
    private String descripcion;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
