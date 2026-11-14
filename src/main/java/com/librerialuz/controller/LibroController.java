package com.librerialuz.controller;

import com.librerialuz.dto.ApiResponse;
import com.librerialuz.dto.LibroRequest;
import com.librerialuz.model.Libro;
import com.librerialuz.service.LibroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * CRUD y búsqueda del catálogo de libros.
 */
@RestController
@RequestMapping("/api/libros")
@Tag(name = "Libros", description = "Gestión y búsqueda del catálogo de libros")
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    @Operation(summary = "Listar o buscar libros",
            description = "Parámetros opcionales: buscar (texto en título o autor) y categoriaId. Respuesta: 200.")
    public List<Libro> listar(@RequestParam(required = false) String buscar,
                              @RequestParam(required = false) Long categoriaId) {
        return libroService.buscar(buscar, categoriaId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar libro", description = "Respuestas: 200 encontrado, 404 no existe.")
    public Libro obtener(@PathVariable Long id) {
        return libroService.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crear libro",
            description = "Respuestas: 201 creado, 400 datos inválidos, 404 categoría inexistente, 409 ISBN repetido.")
    public ResponseEntity<Libro> crear(@Valid @RequestBody LibroRequest solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libroService.crear(solicitud));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar libro",
            description = "Respuestas: 200 actualizado, 400 datos inválidos, 404 libro o categoría inexistente, 409 ISBN repetido.")
    public Libro actualizar(@PathVariable Long id, @Valid @RequestBody LibroRequest solicitud) {
        return libroService.actualizar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar libro",
            description = "Respuestas: 200 eliminado, 404 no existe, 409 el libro ya tiene ventas.")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Long id) {
        libroService.eliminar(id);
        return ResponseEntity.ok(new ApiResponse("Libro eliminado."));
    }
}
