package com.semanticshop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un pedido realizado por un usuario
 */
@Entity
@Table(name = "pedidos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado;
    
    @Column(nullable = false)
    private Double total;
    
    @Column(name = "direccion_envio", columnDefinition = "TEXT")
    private String direccionEnvio;
    
    @Column(columnDefinition = "TEXT")
    private String notas;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DetallePedido> detalles = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (fechaPedido == null) {
            fechaPedido = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoPedido.PENDIENTE;
        }
        fechaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    /**
     * Calcula el total del pedido sumando los subtotales de los detalles
     */
    public void calcularTotal() {
        this.total = detalles.stream()
                .mapToDouble(DetallePedido::getSubtotal)
                .sum();
    }
    
    /**
     * Agrega un detalle al pedido
     */
    public void agregarDetalle(DetallePedido detalle) {
        detalles.add(detalle);
        detalle.setPedido(this);
    }
    
    /**
     * Verifica si el pedido puede ser cancelado
     */
    public boolean puedeCancelarse() {
        return estado != null && estado.esCancelable();
    }
    
    /**
     * Obtiene la cantidad total de items en el pedido
     */
    public int getCantidadTotal() {
        return detalles.stream()
                .mapToInt(DetallePedido::getCantidad)
                .sum();
    }
}