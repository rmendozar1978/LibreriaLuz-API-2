package com.librerialuz.service;

import com.librerialuz.exception.ConflictoException;
import com.librerialuz.exception.RecursoNoEncontradoException;
import com.librerialuz.model.Usuario;
import com.librerialuz.repository.UsuarioRepository;
import com.librerialuz.repository.VentaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Consulta y eliminación de usuarios registrados.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final VentaRepository ventaRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, VentaRepository ventaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.ventaRepository = ventaRepository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll(Sort.by("id"));
    }

    @Transactional(readOnly = true)
    public Usuario obtener(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un usuario con id " + id + "."));
    }

    /** No se elimina un usuario que tenga ventas, para conservar el historial. */
    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = obtener(id);
        if (ventaRepository.existsByUsuarioId(id)) {
            throw new ConflictoException("No se puede eliminar el usuario porque tiene ventas registradas.");
        }
        usuarioRepository.delete(usuario);
    }
}
