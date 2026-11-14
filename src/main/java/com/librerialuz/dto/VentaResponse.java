package com.librerialuz.dto;

import com.librerialuz.model.DetalleVenta;
import com.librerialuz.model.Venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Venta tal como se devuelve en JSON: comprador, total y detalle de libros.
 */
public class VentaResponse {

    private final Long id;
    private final LocalDateTime fecha;
    private final UsuarioResponse usuario;
    private final BigDecimal total;
    private final List<Detalle> detalles;

    public VentaResponse(Venta venta) {
        this.id = venta.getId();
        this.fecha = venta.getFecha();
        this.usuario = new UsuarioResponse(venta.getUsuario());
        this.total = venta.getTotal();
        this.detalles = venta.getDetalles().stream().map(Detalle::new).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public UsuarioResponse getUsuario() {
        return usuario;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public List<Detalle> getDetalles() {
        return detalles;
    }

    /** Línea de la venta. */
    public static class Detalle {

        private final Long libroId;
        private final String titulo;
        private final Integer cantidad;
        private final BigDecimal precioUnitario;
        private final BigDecimal subtotal;

        public Detalle(DetalleVenta detalle) {
            this.libroId = detalle.getLibro().getId();
            this.titulo = detalle.getLibro().getTitulo();
            this.cantidad = detalle.getCantidad();
            this.precioUnitario = detalle.getPrecioUnitario();
            this.subtotal = detalle.getSubtotal();
        }

        public Long getLibroId() {
            return libroId;
        }

        public String getTitulo() {
            return titulo;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }
    }
}
