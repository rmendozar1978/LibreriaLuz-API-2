package com.librerialuz.repository;

import com.librerialuz.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Acceso a datos de libros, incluida la búsqueda por texto y por categoría.
 */
public interface LibroRepository extends JpaRepository<Libro, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    boolean existsByCategoriaId(Long categoriaId);

    /** Filtra por título o autor (sin distinguir mayúsculas) y, si se indica, por categoría. */
    @Query("SELECT l FROM Libro l WHERE "
            + "(:texto IS NULL OR LOWER(l.titulo) LIKE LOWER(CONCAT('%', :texto, '%')) "
            + "OR LOWER(l.autor) LIKE LOWER(CONCAT('%', :texto, '%'))) "
            + "AND (:categoriaId IS NULL OR l.categoria.id = :categoriaId) "
            + "ORDER BY l.titulo")
    List<Libro> buscar(@Param("texto") String texto, @Param("categoriaId") Long categoriaId);
}
