package com.librerialuz.model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Línea de una venta: libro vendido, cantidad y precio unitario al momento de la venta.
 */
@Entity
@Table(name = "detalles_venta")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    public DetalleVenta() {
    }

    public DetalleVenta(Libro libro, Integer cantidad, BigDecimal precioUnitario) {
        this.libro = libro;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    /** Subtotal calculado: precio unitario por cantidad. */
    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    public Long getId() {
        return id;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Libro getLibro() {
        return libro;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
}
