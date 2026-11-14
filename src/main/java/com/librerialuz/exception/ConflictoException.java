package com.librerialuz.exception;

/**
 * Se lanza cuando la operación choca con el estado actual de los datos:
 * correo o ISBN repetido, stock insuficiente o registros relacionados que impiden borrar.
 * El manejador global la convierte en HTTP 409 Conflict.
 */
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
