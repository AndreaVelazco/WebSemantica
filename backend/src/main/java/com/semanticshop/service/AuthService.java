package com.semanticshop.service;

import com.semanticshop.dto.AuthResponse;
import com.semanticshop.dto.LoginRequest;
import com.semanticshop.dto.RegistroRequest;
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
    private final OntologyService ontologyService;
    
    @Transactional
    public AuthResponse registro(RegistroRequest request) {
        // Validar que el username no exista
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        
        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Crear cliente en la ontología
        String clienteIdOntologia = crearClienteEnOntologia(request);
        
        // Crear usuario en base de datos
        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nombreCompleto(request.getNombreCompleto())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .role(Role.USER)
                .clienteIdOntologia(clienteIdOntologia)
                .marcaPreferida(request.getMarcaPreferida())
                .sistemaOperativoPreferido(request.getSistemaOperativoPreferido())
                .rangoPrecioMin(request.getRangoPrecioMin())
                .rangoPrecioMax(request.getRangoPrecioMax())
                .build();
        
        usuario = usuarioRepository.save(usuario);
        
        // Generar token JWT
        String token = jwtUtil.generateToken(usuario);
        
        log.info("Usuario registrado exitosamente: {}", usuario.getUsername());
        
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
        // Autenticar usuario
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        // Obtener usuario
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Generar token JWT
        String token = jwtUtil.generateToken(usuario);
        
        log.info("Usuario autenticado exitosamente: {}", usuario.getUsername());
        
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
        
        return convertirADTO(usuario);
    }
    
    @Transactional
    public UsuarioDTO actualizarPerfil(UsuarioDTO usuarioDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Actualizar campos
        if (usuarioDTO.getEmail() != null && !usuarioDTO.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
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
        usuario.setMarcaPreferida(usuarioDTO.getMarcaPreferida());
        usuario.setSistemaOperativoPreferido(usuarioDTO.getSistemaOperativoPreferido());
        usuario.setRangoPrecioMin(usuarioDTO.getRangoPrecioMin());
        usuario.setRangoPrecioMax(usuarioDTO.getRangoPrecioMax());
        
        usuario = usuarioRepository.save(usuario);
        
        // Actualizar preferencias en ontología
        actualizarPreferenciasEnOntologia(usuario);
        
        log.info("Perfil actualizado para usuario: {}", usuario.getUsername());
        
        return convertirADTO(usuario);
    }
    
    private String crearClienteEnOntologia(RegistroRequest request) {
        try {
            // Crear un ID único para el cliente en la ontología
            String clienteId = "Cliente_" + request.getUsername().replaceAll("[^a-zA-Z0-9]", "");
            
            // Aquí se crearía el cliente en la ontología OWL
            // Por ahora retornamos el ID generado
            log.info("Cliente creado en ontología: {}", clienteId);
            
            return clienteId;
        } catch (Exception e) {
            log.error("Error al crear cliente en ontología: {}", e.getMessage());
            return "Cliente_" + request.getUsername().replaceAll("[^a-zA-Z0-9]", "");
        }
    }
    
    private void actualizarPreferenciasEnOntologia(Usuario usuario) {
        try {
            if (usuario.getClienteIdOntologia() != null) {
                // Aquí se actualizarían las preferencias en la ontología
                log.info("Preferencias actualizadas en ontología para: {}", usuario.getClienteIdOntologia());
            }
        } catch (Exception e) {
            log.error("Error al actualizar preferencias en ontología: {}", e.getMessage());
        }
    }
    
    private UsuarioDTO convertirADTO(Usuario usuario) {
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