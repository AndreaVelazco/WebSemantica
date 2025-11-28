package com.semanticshop.controller;

import com.semanticshop.service.SPARQLService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para consultas SPARQL y análisis de datos
 */
@RestController
@RequestMapping("/api/analisis")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Análisis", description = "API para análisis de ventas y consultas SPARQL")
@CrossOrigin(origins = "*")
public class AnalisisController {

    private final SPARQLService sparqlService;

    @PostMapping("/sparql")
    @Operation(summary = "Ejecutar consulta SPARQL personalizada", 
               description = "Ejecuta una consulta SPARQL sobre la ontología")
    public ResponseEntity<List<Map<String, String>>> ejecutarConsultaSPARQL(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        log.info("POST /api/analisis/sparql - Ejecutando consulta SPARQL");
        List<Map<String, String>> resultados = sparqlService.executeQuery(query);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/ventas/categoria")
    @Operation(summary = "Ventas por categoría", 
               description = "Obtiene estadísticas de ventas agrupadas por categoría")
    public ResponseEntity<List<Map<String, String>>> getVentasPorCategoria() {
        log.info("GET /api/analisis/ventas/categoria");
        List<Map<String, String>> resultados = sparqlService.getVentasPorCategoria();
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/productos/mas-vendidos")
    @Operation(summary = "Productos más vendidos", 
               description = "Obtiene los 10 productos más vendidos")
    public ResponseEntity<List<Map<String, String>>> getProductosMasVendidos() {
        log.info("GET /api/analisis/productos/mas-vendidos");
        List<Map<String, String>> resultados = sparqlService.getProductosMasVendidos();
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/clientes/premium")
    @Operation(summary = "Clientes premium", 
               description = "Obtiene clientes premium y su gasto total")
    public ResponseEntity<List<Map<String, String>>> getClientesPremium() {
        log.info("GET /api/analisis/clientes/premium");
        List<Map<String, String>> resultados = sparqlService.getClientesPremium();
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/productos/rango-precio")
    @Operation(summary = "Productos por rango de precio", 
               description = "Filtra productos por rango de precio")
    public ResponseEntity<List<Map<String, String>>> getProductosPorRangoPrecio(
            @RequestParam(defaultValue = "0") double precioMin,
            @RequestParam(defaultValue = "10000") double precioMax) {
        log.info("GET /api/analisis/productos/rango-precio?precioMin={}&precioMax={}", precioMin, precioMax);
        List<Map<String, String>> resultados = sparqlService.getProductosPorRangoPrecio(precioMin, precioMax);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/productos/bajo-stock")
    @Operation(summary = "Productos con bajo stock", 
               description = "Obtiene productos con stock por debajo del mínimo especificado")
    public ResponseEntity<List<Map<String, String>>> getProductosBajoStock(
            @RequestParam(defaultValue = "10") int stockMinimo) {
        log.info("GET /api/analisis/productos/bajo-stock?stockMinimo={}", stockMinimo);
        List<Map<String, String>> resultados = sparqlService.getProductosBajoStock(stockMinimo);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/marcas/populares")
    @Operation(summary = "Marcas más populares", 
               description = "Obtiene análisis de marcas por ventas y productos")
    public ResponseEntity<List<Map<String, String>>> getMarcasPopulares() {
        log.info("GET /api/analisis/marcas/populares");
        List<Map<String, String>> resultados = sparqlService.getMarcasPopulares();
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/pedidos/por-estado")
    @Operation(summary = "Pedidos por estado", 
               description = "Obtiene cantidad de pedidos agrupados por estado")
    public ResponseEntity<List<Map<String, String>>> getPedidosPorEstado() {
        log.info("GET /api/analisis/pedidos/por-estado");
        List<Map<String, String>> resultados = sparqlService.getPedidosPorEstado();
        return ResponseEntity.ok(resultados);
    }
}
