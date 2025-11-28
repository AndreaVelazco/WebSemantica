package com.semanticshop.service;

import com.semanticshop.dto.AuthResponse;
import com.semanticshop.dto.RegistroRequest;
import com.semanticshop.dto.LoginRequest;
import com.semanticshop.dto.UsuarioDTO;
import com.semanticshop.model.Role;
import com.semanticshop.model.Usuario;
import com.semanticshop.repository.UsuarioRepository;
import com.semanticshop.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final OntologySyncService ontologySyncService;  // ← NUEVO

    @Transactional
    public AuthResponse registro(RegistroRequest request) {
        log.info("Intentando registrar usuario: {}", request.getUsername());

        // Validar que el username no exista
        if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        // Validar que el email no exista
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear el usuario
        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombreCompleto(request.getNombreCompleto())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .role(Role.USER)
                .clienteIdOntologia("Cliente_" + request.getUsername())
                .marcaPreferida(request.getMarcaPreferida())
                .sistemaOperativoPreferido(request.getSistemaOperativoPreferido())
                .rangoPrecioMin(request.getRangoPrecioMin())
                .rangoPrecioMax(request.getRangoPrecioMax())
                .build();

        // Guardar en BD
        usuario = usuarioRepository.save(usuario);
        log.info("✅ Usuario guardado en BD con ID: {}", usuario.getId());

        // 🔥 SINCRONIZAR CON ONTOLOGÍA
        try {
            ontologySyncService.sincronizarUsuarioConOntologia(usuario);
            log.info("✅ Usuario sincronizado con ontología: {}", usuario.getClienteIdOntologia());
        } catch (Exception e) {
            log.error("❌ Error al sincronizar con ontología: {}", e.getMessage());
            // Continuar aunque falle la sincronización
        }

        // Generar token
        String token = jwtUtil.generateToken(usuario);

        return AuthResponse.builder()
                .token(token)
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .role(usuario.getRole().name())
                .clienteIdOntologia(usuario.getClienteIdOntologia())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Intento de login para usuario: {}", request.getUsername());

        // Autenticar
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Obtener usuario
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 🔥 VERIFICAR Y SINCRONIZAR SI NO EXISTE EN ONTOLOGÍA
        if (!ontologySyncService.clienteExisteEnOntologia(usuario.getClienteIdOntologia())) {
            log.info("Cliente no existe en ontología. Sincronizando...");
            try {
                ontologySyncService.sincronizarUsuarioConOntologia(usuario);
                log.info("✅ Usuario sincronizado con ontología en login");
            } catch (Exception e) {
                log.error("❌ Error al sincronizar en login: {}", e.getMessage());
            }
        }

        // Generar token
        String token = jwtUtil.generateToken(usuario);
        log.info("✅ Login exitoso para usuario: {}", usuario.getUsername());

        return AuthResponse.builder()
                .token(token)
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .role(usuario.getRole().name())
                .clienteIdOntologia(usuario.getClienteIdOntologia())
                .build();
    }

    public UsuarioDTO obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return convertirAUsuarioDTO(usuario);
    }

    @Transactional
    public UsuarioDTO actualizarPerfil(UsuarioDTO usuarioDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar solo los campos permitidos
        if (usuarioDTO.getEmail() != null && !usuarioDTO.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.findByEmail(usuarioDTO.getEmail()).isPresent()) {
                throw new RuntimeException("El email ya está en uso");
            }
            usuario.setEmail(usuarioDTO.getEmail());
        }

        if (usuarioDTO.getNombreCompleto() != null) {
            usuario.setNombreCompleto(usuarioDTO.getNombreCompleto());
        }
        if (usuarioDTO.getTelefono() != null) {
            usuario.setTelefono(usuarioDTO.getTelefono());
        }
        if (usuarioDTO.getDireccion() != null) {
            usuario.setDireccion(usuarioDTO.getDireccion());
        }

        // Actualizar preferencias
        if (usuarioDTO.getMarcaPreferida() != null) {
            usuario.setMarcaPreferida(usuarioDTO.getMarcaPreferida());
        }
        if (usuarioDTO.getSistemaOperativoPreferido() != null) {
            usuario.setSistemaOperativoPreferido(usuarioDTO.getSistemaOperativoPreferido());
        }
        if (usuarioDTO.getRangoPrecioMin() != null) {
            usuario.setRangoPrecioMin(usuarioDTO.getRangoPrecioMin());
        }
        if (usuarioDTO.getRangoPrecioMax() != null) {
            usuario.setRangoPrecioMax(usuarioDTO.getRangoPrecioMax());
        }

        usuario = usuarioRepository.save(usuario);
        log.info("✅ Perfil actualizado para usuario: {}", username);

        // 🔥 RE-SINCRONIZAR CON ONTOLOGÍA
        try {
            ontologySyncService.sincronizarUsuarioConOntologia(usuario);
            log.info("✅ Preferencias actualizadas en ontología");
        } catch (Exception e) {
            log.error("❌ Error al actualizar ontología: {}", e.getMessage());
        }

        return convertirAUsuarioDTO(usuario);
    }

    private UsuarioDTO convertirAUsuarioDTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .role(usuario.getRole().name())
                .clienteIdOntologia(usuario.getClienteIdOntologia())
                .fechaRegistro(usuario.getFechaRegistro())
                .activo(usuario.isActivo())
                .marcaPreferida(usuario.getMarcaPreferida())
                .sistemaOperativoPreferido(usuario.getSistemaOperativoPreferido())
                .rangoPrecioMin(usuario.getRangoPrecioMin())
                .rangoPrecioMax(usuario.getRangoPrecioMax())
                .build();
    }
}