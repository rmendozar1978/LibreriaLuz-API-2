package com.librerialuz.repository;

import com.librerialuz.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a datos de categorías.
 */
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
