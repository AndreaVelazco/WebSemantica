package com.semanticshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private String id;
    private String nombre;
    private String tipo;
    private String marca;
    private String categoria;
    private Double precio;
    private Integer stock;
    private String descripcion;
    private List<String> caracteristicas;
    private Set<String> productosCompatibles;
    private Set<String> productosIncompatibles;
    private boolean disponible;  // ← AGREGAR ESTE CAMPO
    private String imagenUrl;
}