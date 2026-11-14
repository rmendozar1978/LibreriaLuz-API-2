package com.librerialuz.dto;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Datos para crear o actualizar un libro del catálogo.
 */
public class LibroRequest {

    @NotBlank(message = "El título es obligatorio.")
    @Size(max = 150, message = "El título no puede superar 150 caracteres.")
    private String titulo;

    @NotBlank(message = "El autor es obligatorio.")
    @Size(max = 100, message = "El autor no puede superar 100 caracteres.")
    private String autor;

    @NotBlank(message = "El ISBN es obligatorio.")
    @Pattern(regexp = "[0-9Xx-]{4,20}", message = "El ISBN solo admite dígitos, guiones o X (4 a 20 caracteres).")
    private String isbn;

    @NotNull(message = "El precio es obligatorio.")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que cero.")
    @Digits(integer = 10, fraction = 2, message = "El precio admite máximo 10 enteros y 2 decimales.")
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio.")
    @Min(value = 0, message = "El stock no puede ser negativo.")
    private Integer stock;

    /** Opcional: identificador de una categoría existente. */
    private Long categoriaId;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }
}
