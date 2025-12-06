package com.semanticshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para representar el carrito completo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoDTO {
    private List<CarritoItemDTO> items;
    private Integer cantidadTotal;
    private Double total;
    private Boolean todosDisponibles;
}