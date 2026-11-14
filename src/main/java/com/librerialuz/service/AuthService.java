package com.librerialuz.service;

import com.librerialuz.dto.LoginRequest;
import com.librerialuz.dto.RegistroRequest;
import com.librerialuz.exception.ConflictoException;
import com.librerialuz.model.Usuario;
import com.librerialuz.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

/**
 * Reglas de negocio de registro e inicio de sesión.
 * El correo se normaliza (sin espacios y en minúsculas) para que sea único sin importar mayúsculas.
 */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Registra el usuario si el correo no está en uso; la contraseña se guarda como hash BCrypt. */
    @Transactional
    public Usuario registrar(RegistroRequest solicitud) {
        String correo = normalizarCorreo(solicitud.getCorreo());
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ConflictoException("El correo ya está registrado.");
        }
        String hash = passwordEncoder.encode(solicitud.getContrasena());
        return usuarioRepository.save(new Usuario(solicitud.getNombre().trim(), correo, hash));
    }

    /**
     * Devuelve el usuario solo si existe y BCrypt confirma la contraseña.
     * Si falla, no se indica cuál de los dos datos fue incorrecto.
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> autenticar(LoginRequest solicitud) {
        return usuarioRepository.findByCorreoIgnoreCase(normalizarCorreo(solicitud.getCorreo()))
                .filter(usuario -> passwordEncoder.matches(solicitud.getContrasena(), usuario.getContrasenaHash()));
    }

    private String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase(Locale.ROOT);
    }
}
