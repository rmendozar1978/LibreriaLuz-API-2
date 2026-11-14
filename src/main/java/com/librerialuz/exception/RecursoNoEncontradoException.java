package com.librerialuz.exception;

/**
 * Se lanza cuando el recurso solicitado (usuario, libro, categoría o venta) no existe.
 * El manejador global la convierte en HTTP 404 Not Found.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
