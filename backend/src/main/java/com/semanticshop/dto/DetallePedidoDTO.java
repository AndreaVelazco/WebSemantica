package com.semanticshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar el detalle de un producto en un pedido
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoDTO {
    private Long id;
    private String productoId;
    private String productoNombre;
    private String productoMarca;
    private String productoCategoria;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}