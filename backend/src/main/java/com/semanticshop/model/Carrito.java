package com.semanticshop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un item en el carrito de compras de un usuario
 */
@Entity
@Table(name = "carrito", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "producto_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Carrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(name = "producto_id", nullable = false, length = 100)
    private String productoId;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer cantidad = 1;
    
    @Column(name = "fecha_agregado")
    private LocalDateTime fechaAgregado;
    
    @PrePersist
    protected void onCreate() {
        if (fechaAgregado == null) {
            fechaAgregado = LocalDateTime.now();
        }
        if (cantidad == null || cantidad < 1) {
            cantidad = 1;
        }
    }
    
    /**
     * Incrementa la cantidad del producto
     */
    public void incrementarCantidad(int cantidad) {
        this.cantidad += cantidad;
    }
    
    /**
     * Actualiza la cantidad del producto
     */
    public void actualizarCantidad(int nuevaCantidad) {
        if (nuevaCantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.cantidad = nuevaCantidad;
    }
}