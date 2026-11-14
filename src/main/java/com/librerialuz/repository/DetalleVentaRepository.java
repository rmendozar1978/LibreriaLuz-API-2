package com.librerialuz.repository;

import com.librerialuz.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a los detalles de venta; se usa para impedir borrar libros ya vendidos.
 */
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    boolean existsByLibroId(Long libroId);
}
