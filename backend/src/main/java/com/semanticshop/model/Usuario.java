package com.semanticshop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data                    // ← IMPORTANTE
@Builder                 // ← IMPORTANTE
@NoArgsConstructor       // ← IMPORTANTE
@AllArgsConstructor      // ← IMPORTANTE
public class Usuario implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(name = "nombre_completo")
    private String nombreCompleto;
    
    private String telefono;
    private String direccion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @Column(name = "cliente_id_ontologia")
    private String clienteIdOntologia;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    private boolean activo;
    
    @Column(name = "marca_preferida")
    private String marcaPreferida;
    
    @Column(name = "sistema_operativo_preferido")
    private String sistemaOperativoPreferido;
    
    @Column(name = "rango_precio_min")
    private Double rangoPrecioMin;
    
    @Column(name = "rango_precio_max")
    private Double rangoPrecioMax;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        activo = true;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return activo;
    }
}