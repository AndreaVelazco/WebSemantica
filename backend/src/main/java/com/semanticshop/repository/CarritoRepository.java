package com.semanticshop.repository;

import com.semanticshop.model.Carrito;
import com.semanticshop.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Carrito
 */
@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    
    /**
     * Buscar todos los items del carrito de un usuario
     */
    List<Carrito> findByUsuarioOrderByFechaAgregadoDesc(Usuario usuario);
    
    /**
     * Buscar un item específico en el carrito
     */
    Optional<Carrito> findByUsuarioAndProductoId(Usuario usuario, String productoId);
    
    /**
     * Eliminar un item del carrito
     */
    @Transactional
    void deleteByUsuarioAndProductoId(Usuario usuario, String productoId);
    
    /**
     * Eliminar todos los items del carrito de un usuario
     */
    @Transactional
    void deleteByUsuario(Usuario usuario);
    
    /**
     * Contar items en el carrito de un usuario
     */
    long countByUsuario(Usuario usuario);
    
    /**
     * Verificar si un producto está en el carrito del usuario
     */
    boolean existsByUsuarioAndProductoId(Usuario usuario, String productoId);
    
    /**
     * Obtener cantidad total de productos en el carrito
     */
    @Query("SELECT SUM(c.cantidad) FROM Carrito c WHERE c.usuario = :usuario")
    Integer sumCantidadByUsuario(Usuario usuario);
}