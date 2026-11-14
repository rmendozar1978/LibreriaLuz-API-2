package com.librerialuz.controller;

import com.librerialuz.dto.ApiResponse;
import com.librerialuz.dto.LoginRequest;
import com.librerialuz.dto.LoginResponse;
import com.librerialuz.dto.RegistroRequest;
import com.librerialuz.dto.UsuarioResponse;
import com.librerialuz.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Servicios de registro e inicio de sesión de usuarios.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro de usuarios e inicio de sesión")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar usuario",
            description = "Crea una cuenta. Respuestas: 201 creado, 400 datos inválidos, 409 correo ya registrado.")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UsuarioResponse(authService.registrar(solicitud)));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión",
            description = "Valida correo y contraseña. Respuestas: 200 autenticado, 400 datos faltantes, 401 credenciales incorrectas.")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest solicitud) {
        return authService.autenticar(solicitud)
                .<ResponseEntity<?>>map(usuario -> ResponseEntity.ok(
                        new LoginResponse("Autenticación satisfactoria.", new UsuarioResponse(usuario))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse("Error en la autenticación.")));
    }
}
