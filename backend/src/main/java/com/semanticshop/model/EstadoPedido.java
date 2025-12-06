package com.semanticshop.model;

/**
 * Enumeración de estados posibles de un pedido
 */
public enum EstadoPedido {
    PENDIENTE("Pendiente", "Pedido recibido, pendiente de procesamiento"),
    PROCESANDO("Procesando", "Pedido en preparación"),
    ENVIADO("Enviado", "Pedido enviado al cliente"),
    ENTREGADO("Entregado", "Pedido entregado exitosamente"),
    CANCELADO("Cancelado", "Pedido cancelado");
    
    private final String displayName;
    private final String descripcion;
    
    EstadoPedido(String displayName, String descripcion) {
        this.displayName = displayName;
        this.descripcion = descripcion;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    /**
     * Verifica si el pedido puede ser cancelado
     */
    public boolean esCancelable() {
        return this == PENDIENTE || this == PROCESANDO;
    }
    
    /**
     * Verifica si el pedido está en proceso
     */
    public boolean enProceso() {
        return this == PENDIENTE || this == PROCESANDO || this == ENVIADO;
    }
    
    /**
     * Verifica si el pedido está finalizado
     */
    public boolean esFinal() {
        return this == ENTREGADO || this == CANCELADO;
    }
}