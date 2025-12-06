package com.semanticshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para los parámetros de búsqueda y filtrado de productos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusquedaProductosRequest {
    
    // Búsqueda por texto
    private String q; // Término de búsqueda
    
    // Filtros
    private String categoria;
    private String marca;
    private Double precioMin;
    private Double precioMax;
    private Boolean disponible; // Solo productos en stock
    
    // Ordenamiento
    private String ordenarPor; // precio, nombre, popularidad, fecha
    private String direccion;  // asc, desc
    
    // Paginación
    private Integer pagina;
    private Integer tamanio;
    
    /**
     * Valores por defecto
     */
    public String getOrdenarPor() {
        return ordenarPor != null ? ordenarPor : "nombre";
    }
    
    public String getDireccion() {
        return direccion != null ? direccion : "asc";
    }
    
    public Integer getPagina() {
        return pagina != null ? pagina : 0;
    }
    
    public Integer getTamanio() {
        return tamanio != null && tamanio > 0 ? tamanio : 12;
    }
    
    public Boolean getDisponible() {
        return disponible != null ? disponible : false;
    }
}