package com.semanticshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * DTO para representar un cliente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    
    private String id;
    private String nombre;
    private String email;
    private String tipo; // ClienteNuevo, ClientePremium (inferido)
    
    // Preferencias del cliente
    private String marcaPreferida;
    private List<String> preferencias; // SO, TipoConector, etc.
    
    // Pedidos realizados
    private Integer numeroPedidos;
    
    // Productos recomendados (inferidos por el razonador)
    private Set<String> productosRecomendados;
}
