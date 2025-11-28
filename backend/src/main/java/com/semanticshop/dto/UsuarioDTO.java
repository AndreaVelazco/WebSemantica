package com.semanticshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data                    // ← IMPORTANTE
@Builder                 // ← IMPORTANTE
@NoArgsConstructor       // ← IMPORTANTE
@AllArgsConstructor      // ← IMPORTANTE
public class UsuarioDTO {
    private Long id;
    private String username;
    private String email;
    private String nombreCompleto;
    private String telefono;
    private String direccion;
    private String role;
    private String clienteIdOntologia;
    private LocalDateTime fechaRegistro;
    private boolean activo;
    
    // Preferencias
    private String marcaPreferida;
    private String sistemaOperativoPreferido;
    private Double rangoPrecioMin;
    private Double rangoPrecioMax;
}