package com.librerialuz.service;

import com.librerialuz.dto.CategoriaRequest;
import com.librerialuz.exception.ConflictoException;
import com.librerialuz.exception.RecursoNoEncontradoException;
import com.librerialuz.model.Categoria;
import com.librerialuz.repository.CategoriaRepository;
import com.librerialuz.repository.LibroRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reglas de negocio de las categorías: nombre único y no borrar categorías en uso.
 */
@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final LibroRepository libroRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, LibroRepository libroRepository) {
        this.categoriaRepository = categoriaRepository;
        this.libroRepository = libroRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listar() {
        return categoriaRepository.findAll(Sort.by("nombre"));
    }

    @Transactional(readOnly = true)
    public Categoria obtener(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una categoría con id " + id + "."));
    }

    @Transactional
    public Categoria crear(CategoriaRequest solicitud) {
        String nombre = solicitud.getNombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictoException("Ya existe una categoría con el nombre '" + nombre + "'.");
        }
        return categoriaRepository.save(new Categoria(nombre, solicitud.getDescripcion()));
    }

    @Transactional
    public Categoria actualizar(Long id, CategoriaRequest solicitud) {
        Categoria categoria = obtener(id);
        String nombre = solicitud.getNombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ConflictoException("Ya existe una categoría con el nombre '" + nombre + "'.");
        }
        categoria.setNombre(nombre);
        categoria.setDescripcion(solicitud.getDescripcion());
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = obtener(id);
        if (libroRepository.existsByCategoriaId(id)) {
            throw new ConflictoException("No se puede eliminar la categoría porque tiene libros asociados.");
        }
        categoriaRepository.delete(categoria);
    }
}
