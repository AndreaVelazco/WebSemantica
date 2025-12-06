package com.semanticshop.repository;

import com.semanticshop.model.Pedido;
import com.semanticshop.model.EstadoPedido;
import com.semanticshop.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Pedido
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    /**
     * Buscar pedidos por usuario ordenados por fecha descendente
     */
    List<Pedido> findByUsuarioOrderByFechaPedidoDesc(Usuario usuario);
    
    /**
     * Buscar pedidos por estado
     */
    List<Pedido> findByEstadoOrderByFechaPedidoDesc(EstadoPedido estado);
    
    /**
     * Buscar pedido por ID y usuario (para verificar permisos)
     */
    Optional<Pedido> findByIdAndUsuario(Long id, Usuario usuario);
    
    /**
     * Contar pedidos por usuario
     */
    long countByUsuario(Usuario usuario);
    
    /**
     * Contar pedidos por estado
     */
    long countByEstado(EstadoPedido estado);
    
    /**
     * Obtener total de ventas por estado
     */
    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.estado = :estado")
    Double sumTotalByEstado(EstadoPedido estado);
    
    /**
     * Obtener total de ventas de un usuario
     */
    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.usuario = :usuario")
    Double sumTotalByUsuario(Usuario usuario);
}