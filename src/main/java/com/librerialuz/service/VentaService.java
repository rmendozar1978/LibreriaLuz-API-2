package com.librerialuz.service;

import com.librerialuz.dto.VentaRequest;
import com.librerialuz.exception.ConflictoException;
import com.librerialuz.exception.RecursoNoEncontradoException;
import com.librerialuz.model.DetalleVenta;
import com.librerialuz.model.Libro;
import com.librerialuz.model.Usuario;
import com.librerialuz.model.Venta;
import com.librerialuz.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registro y consulta de ventas.
 * Al vender se valida el stock de cada libro y se descuenta en la misma transacción:
 * si un libro no alcanza, no se guarda nada.
 */
@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final UsuarioService usuarioService;
    private final LibroService libroService;

    public VentaService(VentaRepository ventaRepository,
                        UsuarioService usuarioService,
                        LibroService libroService) {
        this.ventaRepository = ventaRepository;
        this.usuarioService = usuarioService;
        this.libroService = libroService;
    }

    @Transactional
    public Venta registrar(VentaRequest solicitud) {
        Usuario usuario = usuarioService.obtener(solicitud.getUsuarioId());

        // Agrupa ítems repetidos del mismo libro para validar el stock sobre la cantidad total.
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (VentaRequest.Item item : solicitud.getItems()) {
            cantidades.merge(item.getLibroId(), item.getCantidad(), Integer::sum);
        }

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setFecha(LocalDateTime.now());
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entrada : cantidades.entrySet()) {
            Libro libro = libroService.obtener(entrada.getKey());
            int cantidad = entrada.getValue();
            if (libro.getStock() < cantidad) {
                throw new ConflictoException("Stock insuficiente para '" + libro.getTitulo()
                        + "': disponible " + libro.getStock() + ", solicitado " + cantidad + ".");
            }
            libro.setStock(libro.getStock() - cantidad);
            DetalleVenta detalle = new DetalleVenta(libro, cantidad, libro.getPrecio());
            venta.agregarDetalle(detalle);
            total = total.add(detalle.getSubtotal());
        }

        venta.setTotal(total);
        return ventaRepository.save(venta);
    }

    /** Lista todas las ventas o, si se indica, solo las de un usuario; las más recientes primero. */
    @Transactional(readOnly = true)
    public List<Venta> listar(Long usuarioId) {
        if (usuarioId == null) {
            return ventaRepository.findAllByOrderByFechaDesc();
        }
        usuarioService.obtener(usuarioId);
        return ventaRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    @Transactional(readOnly = true)
    public Venta obtener(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una venta con id " + id + "."));
    }
}
