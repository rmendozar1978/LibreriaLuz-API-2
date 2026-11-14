package com.librerialuz.service;

import com.librerialuz.dto.LibroRequest;
import com.librerialuz.exception.ConflictoException;
import com.librerialuz.exception.RecursoNoEncontradoException;
import com.librerialuz.model.Libro;
import com.librerialuz.repository.DetalleVentaRepository;
import com.librerialuz.repository.LibroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reglas de negocio del catálogo de libros: ISBN único, categoría existente
 * y no borrar libros que ya aparecen en ventas.
 */
@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final CategoriaService categoriaService;

    public LibroService(LibroRepository libroRepository,
                        DetalleVentaRepository detalleVentaRepository,
                        CategoriaService categoriaService) {
        this.libroRepository = libroRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.categoriaService = categoriaService;
    }

    /** Lista el catálogo; el texto busca en título o autor y la categoría es opcional. */
    @Transactional(readOnly = true)
    public List<Libro> buscar(String texto, Long categoriaId) {
        String filtro = (texto == null || texto.trim().isEmpty()) ? null : texto.trim();
        return libroRepository.buscar(filtro, categoriaId);
    }

    @Transactional(readOnly = true)
    public Libro obtener(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un libro con id " + id + "."));
    }

    @Transactional
    public Libro crear(LibroRequest solicitud) {
        String isbn = solicitud.getIsbn().trim();
        if (libroRepository.existsByIsbn(isbn)) {
            throw new ConflictoException("Ya existe un libro con el ISBN " + isbn + ".");
        }
        Libro libro = new Libro();
        copiarDatos(solicitud, libro);
        return libroRepository.save(libro);
    }

    @Transactional
    public Libro actualizar(Long id, LibroRequest solicitud) {
        Libro libro = obtener(id);
        String isbn = solicitud.getIsbn().trim();
        if (libroRepository.existsByIsbnAndIdNot(isbn, id)) {
            throw new ConflictoException("Ya existe un libro con el ISBN " + isbn + ".");
        }
        copiarDatos(solicitud, libro);
        return libroRepository.save(libro);
    }

    @Transactional
    public void eliminar(Long id) {
        Libro libro = obtener(id);
        if (detalleVentaRepository.existsByLibroId(id)) {
            throw new ConflictoException("No se puede eliminar el libro porque ya tiene ventas registradas.");
        }
        libroRepository.delete(libro);
    }

    private void copiarDatos(LibroRequest solicitud, Libro libro) {
        libro.setTitulo(solicitud.getTitulo().trim());
        libro.setAutor(solicitud.getAutor().trim());
        libro.setIsbn(solicitud.getIsbn().trim());
        libro.setPrecio(solicitud.getPrecio());
        libro.setStock(solicitud.getStock());
        libro.setCategoria(solicitud.getCategoriaId() == null
                ? null
                : categoriaService.obtener(solicitud.getCategoriaId()));
    }
}
