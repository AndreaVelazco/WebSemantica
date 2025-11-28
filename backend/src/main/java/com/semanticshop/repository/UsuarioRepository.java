package com.semanticshop.repository;

import com.semanticshop.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByUsername(String username);
    
    Optional<Usuario> findByEmail(String email);
    
    Optional<Usuario> findByClienteIdOntologia(String clienteIdOntologia);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}