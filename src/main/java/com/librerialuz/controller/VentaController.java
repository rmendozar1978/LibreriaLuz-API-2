package com.librerialuz.controller;

import com.librerialuz.dto.VentaRequest;
import com.librerialuz.dto.VentaResponse;
import com.librerialuz.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Registro y consulta de ventas de libros.
 */
@RestController
@RequestMapping("/api/ventas")
@Tag(name = "Ventas", description = "Registro de ventas con control de stock")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    @Operation(summary = "Registrar venta",
            description = "Descuenta el stock y calcula el total. Respuestas: 201 creada, 400 datos inválidos, "
                    + "404 usuario o libro inexistente, 409 stock insuficiente.")
    public ResponseEntity<VentaResponse> registrar(@Valid @RequestBody VentaRequest solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new VentaResponse(ventaService.registrar(solicitud)));
    }

    @GetMapping
    @Operation(summary = "Listar ventas",
            description = "Parámetro opcional usuarioId para ver las ventas de un usuario. Respuestas: 200, 404 usuario inexistente.")
    public List<VentaResponse> listar(@RequestParam(required = false) Long usuarioId) {
        return ventaService.listar(usuarioId).stream().map(VentaResponse::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar venta", description = "Respuestas: 200 encontrada, 404 no existe.")
    public VentaResponse obtener(@PathVariable Long id) {
        return new VentaResponse(ventaService.obtener(id));
    }
}
