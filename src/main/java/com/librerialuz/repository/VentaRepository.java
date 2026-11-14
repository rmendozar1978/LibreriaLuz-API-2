package com.librerialuz.repository;

import com.librerialuz.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acceso a datos de ventas.
 */
public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    List<Venta> findAllByOrderByFechaDesc();

    boolean existsByUsuarioId(Long usuarioId);
}
