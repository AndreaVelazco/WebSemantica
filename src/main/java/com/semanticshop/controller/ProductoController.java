package com.semanticshop.controller;

import com.semanticshop.dto.ProductoDTO;
import com.semanticshop.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar productos
 */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Productos", description = "API para gestión de productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    @Operation(summary = "Obtener todos los productos", 
               description = "Retorna el catálogo completo de productos")
    public ResponseEntity<List<ProductoDTO>> getAllProductos() {
        log.info("GET /api/productos - Obteniendo todos los productos");
        List<ProductoDTO> productos = productoService.getAllProductos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", 
               description = "Retorna un producto específico con sus relaciones inferidas")
    public ResponseEntity<ProductoDTO> getProductoById(@PathVariable String id) {
        log.info("GET /api/productos/{} - Obteniendo producto", id);
        return productoService.getProductoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Obtener productos por categoría", 
               description = "Retorna productos de una categoría específica (Smartphone, Laptop, etc.)")
    public ResponseEntity<List<ProductoDTO>> getProductosByCategoria(@PathVariable String categoria) {
        log.info("GET /api/productos/categoria/{} - Obteniendo productos por categoría", categoria);
        List<ProductoDTO> productos = productoService.getProductosByCategoria(categoria);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}/compatibles")
    @Operation(summary = "Obtener productos compatibles", 
               description = "Retorna productos compatibles con el producto especificado (inferido por HermiT)")
    public ResponseEntity<List<ProductoDTO>> getProductosCompatibles(@PathVariable String id) {
        log.info("GET /api/productos/{}/compatibles - Obteniendo productos compatibles", id);
        List<ProductoDTO> compatibles = productoService.getProductosCompatibles(id);
        return ResponseEntity.ok(compatibles);
    }

    @GetMapping("/{id}/incompatibles")
    @Operation(summary = "Obtener productos incompatibles", 
               description = "Retorna productos incompatibles con el producto especificado (inferido por HermiT)")
    public ResponseEntity<List<ProductoDTO>> getProductosIncompatibles(@PathVariable String id) {
        log.info("GET /api/productos/{}/incompatibles - Obteniendo productos incompatibles", id);
        List<ProductoDTO> incompatibles = productoService.getProductosIncompatibles(id);
        return ResponseEntity.ok(incompatibles);
    }

    @GetMapping("/compatibilidad")
    @Operation(summary = "Verificar compatibilidad entre dos productos", 
               description = "Verifica si dos productos son compatibles entre sí")
    public ResponseEntity<Map<String, Object>> verificarCompatibilidad(
            @RequestParam String producto1,
            @RequestParam String producto2) {
        log.info("GET /api/productos/compatibilidad?producto1={}&producto2={}", producto1, producto2);
        
        boolean compatibles = productoService.sonCompatibles(producto1, producto2);
        
        return ResponseEntity.ok(Map.of(
                "producto1", producto1,
                "producto2", producto2,
                "sonCompatibles", compatibles
        ));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar productos", 
               description = "Busca productos por nombre, marca o tipo")
    public ResponseEntity<List<ProductoDTO>> buscarProductos(@RequestParam String query) {
        log.info("GET /api/productos/buscar?query={}", query);
        List<ProductoDTO> resultados = productoService.buscarProductos(query);
        return ResponseEntity.ok(resultados);
    }
}
