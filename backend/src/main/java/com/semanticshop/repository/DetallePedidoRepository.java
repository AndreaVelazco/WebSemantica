package com.semanticshop.repository;

import com.semanticshop.model.DetallePedido;
import com.semanticshop.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad DetallePedido
 */
@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    
    /**
     * Buscar detalles por pedido
     */
    List<DetallePedido> findByPedido(Pedido pedido);
    
    /**
     * Buscar detalles por ID de producto
     */
    List<DetallePedido> findByProductoId(String productoId);
}