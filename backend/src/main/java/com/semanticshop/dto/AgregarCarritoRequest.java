package com.semanticshop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request para agregar producto al carrito
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgregarCarritoRequest {
    @NotBlank(message = "El ID del producto es obligatorio")
    private String productoId;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
}