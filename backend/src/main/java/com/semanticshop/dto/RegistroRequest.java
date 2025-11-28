package com.semanticshop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                    // ← IMPORTANTE
@Builder                 // ← IMPORTANTE
@NoArgsConstructor       // ← IMPORTANTE
@AllArgsConstructor      // ← IMPORTANTE
public class RegistroRequest {
    
    @NotBlank(message = "El nombre de usuario es requerido")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String username;
    
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe ser válido")
    private String email;
    
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    private String nombreCompleto;
    private String telefono;
    private String direccion;
    
    // Preferencias opcionales
    private String marcaPreferida;
    private String sistemaOperativoPreferido;
    private Double rangoPrecioMin;
    private Double rangoPrecioMax;
}