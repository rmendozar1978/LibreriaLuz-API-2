package com.librerialuz.controller;

import com.librerialuz.dto.ApiResponse;
import com.librerialuz.dto.CategoriaRequest;
import com.librerialuz.model.Categoria;
import com.librerialuz.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * CRUD de categorías del catálogo.
 */
@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorías", description = "Gestión de las categorías de libros")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    @Operation(summary = "Listar categorías", description = "Respuesta: 200 con las categorías ordenadas por nombre.")
    public List<Categoria> listar() {
        return categoriaService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar categoría", description = "Respuestas: 200 encontrada, 404 no existe.")
    public Categoria obtener(@PathVariable Long id) {
        return categoriaService.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crear categoría",
            description = "Respuestas: 201 creada, 400 datos inválidos, 409 nombre repetido.")
    public ResponseEntity<Categoria> crear(@Valid @RequestBody CategoriaRequest solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crear(solicitud));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría",
            description = "Respuestas: 200 actualizada, 400 datos inválidos, 404 no existe, 409 nombre repetido.")
    public Categoria actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest solicitud) {
        return categoriaService.actualizar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría",
            description = "Respuestas: 200 eliminada, 404 no existe, 409 tiene libros asociados.")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.ok(new ApiResponse("Categoría eliminada."));
    }
}
