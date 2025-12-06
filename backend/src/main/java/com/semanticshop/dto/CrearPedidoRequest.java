package com.semanticshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request para crear pedido
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearPedidoRequest {
    private String direccionEnvio;
    private String notas;
}