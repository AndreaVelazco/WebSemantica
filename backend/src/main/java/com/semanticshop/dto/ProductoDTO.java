package com.semanticshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * DTO para representar un producto del catálogo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    
    private String id;
    private String nombre;
    private String tipo; // Smartphone, Laptop, Accesorio, etc.
    private String marca;
    private Double precio;
    private Integer stock;
    private String categoria;
    
    // Características técnicas
    private List<String> caracteristicas;
    
    // Productos compatibles (inferidos por el razonador)
    private Set<String> productosCompatibles;
    
    // Productos incompatibles (inferidos por el razonador)
    private Set<String> productosIncompatibles;
    
    // Indica si el producto está en stock
    public boolean isDisponible() {
        return stock != null && stock > 0;
    }
}
