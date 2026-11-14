package com.librerialuz.controller;

import com.librerialuz.dto.ApiResponse;
import com.librerialuz.dto.UsuarioResponse;
import com.librerialuz.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Consulta y eliminación de usuarios. Las respuestas nunca incluyen contraseñas.
 */
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Consulta y eliminación de usuarios registrados")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Respuesta: 200 con la lista de usuarios.")
    public List<UsuarioResponse> listar() {
        return usuarioService.listar().stream().map(UsuarioResponse::new).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar usuario", description = "Respuestas: 200 encontrado, 404 no existe.")
    public UsuarioResponse obtener(@PathVariable Long id) {
        return new UsuarioResponse(usuarioService.obtener(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario",
            description = "Respuestas: 200 eliminado, 404 no existe, 409 tiene ventas registradas.")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok(new ApiResponse("Usuario eliminado."));
    }
}
