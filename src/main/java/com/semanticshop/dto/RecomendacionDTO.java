package com.semanticshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para respuestas de recomendaciones
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecomendacionDTO {
    
    private String clienteId;
    private String clienteNombre;
    private List<ProductoDTO> productosRecomendados;
    private String razonRecomendacion;
    private Integer totalRecomendaciones;
}
