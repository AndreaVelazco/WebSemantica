package com.semanticshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para representar un item en el carrito
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItemDTO {
    private Long id;
    private String productoId;
    private String productoNombre;
    private String productoMarca;
    private String productoCategoria;
    private Double precio;
    private Integer cantidad;
    private Integer stock;
    private Double subtotal;
    private LocalDateTime fechaAgregado;
    private Boolean disponible;
}