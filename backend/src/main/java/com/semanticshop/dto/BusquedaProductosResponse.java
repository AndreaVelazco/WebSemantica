package com.semanticshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO para la respuesta de búsqueda de productos con paginación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusquedaProductosResponse {
    
    private List<ProductoDTO> productos;
    private PaginacionInfo paginacion;
    private FiltrosAplicados filtrosAplicados;
    private OrdenamientoInfo ordenamiento;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginacionInfo {
        private Integer paginaActual;
        private Integer tamanioPagina;
        private Long totalElementos;
        private Integer totalPaginas;
        private Boolean esUltimaPagina;
        private Boolean esPrimeraPagina;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FiltrosAplicados {
        private String busqueda;
        private String categoria;
        private String marca;
        private Double precioMin;
        private Double precioMax;
        private Boolean disponible;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrdenamientoInfo {
        private String campo;
        private String direccion;
    }
}