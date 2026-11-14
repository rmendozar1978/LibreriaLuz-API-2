package com.librerialuz.exception;

import com.librerialuz.dto.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Manejo centralizado de errores de la API.
 * Todas las respuestas de error usan el formato ApiResponse ({"mensaje": "..."})
 * y nunca exponen trazas ni datos internos.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** HTTP 400: un campo no cumple las validaciones declaradas en el DTO. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> manejarValidacion(MethodArgumentNotValidException ex) {
        FieldError error = ex.getBindingResult().getFieldError();
        String mensaje = (error != null && error.getDefaultMessage() != null)
                ? error.getDefaultMessage()
                : "Datos de solicitud inválidos.";
        return responder(HttpStatus.BAD_REQUEST, mensaje);
    }

    /** HTTP 400: el cuerpo falta o no es un JSON válido. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> manejarCuerpoInvalido(HttpMessageNotReadableException ex) {
        return responder(HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud es obligatorio y debe tener formato JSON válido.");
    }

    /** HTTP 400: un parámetro de ruta o de consulta tiene un tipo incorrecto (p. ej. /api/libros/abc). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> manejarTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return responder(HttpStatus.BAD_REQUEST,
                "El parámetro '" + ex.getName() + "' tiene un valor inválido.");
    }

    /** HTTP 404: el recurso solicitado no existe. */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiResponse> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** HTTP 405: el método HTTP no está permitido en la ruta. */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> manejarMetodo(HttpRequestMethodNotSupportedException ex) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED,
                "El método " + ex.getMethod() + " no está permitido en esta ruta.");
    }

    /** HTTP 409: regla de negocio que impide la operación. */
    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ApiResponse> manejarConflicto(ConflictoException ex) {
        return responder(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * HTTP 409: la base de datos rechazó la operación por una restricción de unicidad o
     * de llave foránea (por ejemplo, dos registros simultáneos con el mismo correo).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> manejarIntegridad(DataIntegrityViolationException ex) {
        return responder(HttpStatus.CONFLICT,
                "La operación entra en conflicto con datos ya registrados.");
    }

    private ResponseEntity<ApiResponse> responder(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(new ApiResponse(mensaje));
    }
}
