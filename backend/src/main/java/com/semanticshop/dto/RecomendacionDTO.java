package com.semanticshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecomendacionDTO {
    private String clienteId;
    private String clienteNombre;
    private List<ProductoDTO> productos;  // ← Este campo
    private String razon;
    private String razonRecomendacion;    // ← AGREGAR ESTE
    private int totalRecomendaciones;      // ← AGREGAR ESTE
}