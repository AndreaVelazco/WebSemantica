package com.semanticshop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request para actualizar estado de pedido
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstadoRequest {
    @NotBlank(message = "El estado es obligatorio")
    private String estado; // PENDIENTE, PROCESANDO, ENVIADO, ENTREGADO, CANCELADO
}