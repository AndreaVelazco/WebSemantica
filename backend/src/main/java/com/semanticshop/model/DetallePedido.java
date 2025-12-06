package com.semanticshop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa cada producto incluido en un pedido
 */
@Entity
@Table(name = "detalle_pedido")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    
    @Column(name = "producto_id", nullable = false, length = 100)
    private String productoId;
    
    @Column(name = "producto_nombre", nullable = false, length = 200)
    private String productoNombre;
    
    @Column(name = "producto_marca", length = 100)
    private String productoMarca;
    
    @Column(name = "producto_categoria", length = 100)
    private String productoCategoria;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;

    @Column(nullable = false)
    private Double subtotal;
    
    /**
     * Calcula el subtotal basado en cantidad y precio unitario
     */
    public void calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            this.subtotal = cantidad * precioUnitario;
        }
    }
    
    @PrePersist
    @PreUpdate
    protected void onSave() {
        calcularSubtotal();
    }
}