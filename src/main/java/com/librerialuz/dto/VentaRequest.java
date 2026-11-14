package com.librerialuz.dto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Datos para registrar una venta: el usuario comprador y los libros con sus cantidades.
 */
public class VentaRequest {

    @NotNull(message = "El usuario de la venta es obligatorio.")
    private Long usuarioId;

    @NotEmpty(message = "La venta debe incluir al menos un libro.")
    private List<@Valid @NotNull(message = "Los ítems de la venta no pueden ser nulos.") Item> items;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    /** Libro y cantidad solicitada dentro de la venta. */
    public static class Item {

        @NotNull(message = "El libro de cada ítem es obligatorio.")
        private Long libroId;

        @NotNull(message = "La cantidad de cada ítem es obligatoria.")
        @Min(value = 1, message = "La cantidad de cada ítem debe ser al menos 1.")
        private Integer cantidad;

        public Long getLibroId() {
            return libroId;
        }

        public void setLibroId(Long libroId) {
            this.libroId = libroId;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }
}
