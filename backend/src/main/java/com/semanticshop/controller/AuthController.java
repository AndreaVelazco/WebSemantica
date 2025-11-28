package com.semanticshop.controller;

import com.semanticshop.dto.AuthResponse;
import com.semanticshop.dto.LoginRequest;
import com.semanticshop.dto.RegistroRequest;
import com.semanticshop.dto.UsuarioDTO;
import com.semanticshop.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para registro, login y gestión de usuarios")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    @Operation(summary = "Registrar nuevo usuario")
    public ResponseEntity<AuthResponse> registro(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.ok(authService.registro(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/perfil")
    @Operation(summary = "Obtener perfil del usuario actual")
    public ResponseEntity<UsuarioDTO> obtenerPerfil() {
        return ResponseEntity.ok(authService.obtenerUsuarioActual());
    }

    @PutMapping("/perfil")
    @Operation(summary = "Actualizar perfil del usuario")
    public ResponseEntity<UsuarioDTO> actualizarPerfil(@RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(authService.actualizarPerfil(usuarioDTO));
    }
}