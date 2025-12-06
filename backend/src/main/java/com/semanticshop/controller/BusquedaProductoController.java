package com.semanticshop.controller;

import com.semanticshop.dto.BusquedaProductosRequest;
import com.semanticshop.dto.BusquedaProductosResponse;
import com.semanticshop.service.BusquedaProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller para búsqueda y filtrado avanzado de productos
 */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BusquedaProductoController {

    private final BusquedaProductoService busquedaService;

    /**
     * 1. Búsqueda avanzada con filtros, ordenamiento y paginación
     * GET /api/productos/buscar
     */
    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscarProductos(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(required = false) Boolean disponible,
            @RequestParam(required = false) String ordenarPor,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer tamanio) {
        
        try {
            log.info("🔍 Búsqueda: q={}, categoria={}, marca={}, precio=[{}-{}]",
                    q, categoria, marca, precioMin, precioMax);

            BusquedaProductosRequest request = BusquedaProductosRequest.builder()
                    .q(q)
                    .categoria(categoria)
                    .marca(marca)
                    .precioMin(precioMin)
                    .precioMax(precioMax)
                    .disponible(disponible)
                    .ordenarPor(ordenarPor)
                    .direccion(direccion)
                    .pagina(pagina)
                    .tamanio(tamanio)
                    .build();

            BusquedaProductosResponse response = busquedaService.buscarProductos(request);

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("success", true);
            resultado.put("datos", response);

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            log.error("❌ Error en búsqueda: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 2. Obtener todas las categorías disponibles
     * GET /api/productos/categorias
     */
    @GetMapping("/categorias")
    public ResponseEntity<Map<String, Object>> obtenerCategorias() {
        try {
            log.info("📂 Obteniendo categorías disponibles");

            List<String> categorias = busquedaService.obtenerCategorias();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("categorias", categorias);
            response.put("total", categorias.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error obteniendo categorías: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 3. Obtener todas las marcas disponibles
     * GET /api/productos/marcas
     */
    @GetMapping("/marcas")
    public ResponseEntity<Map<String, Object>> obtenerMarcas() {
        try {
            log.info("🏷️ Obteniendo marcas disponibles");

            List<String> marcas = busquedaService.obtenerMarcas();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("marcas", marcas);
            response.put("total", marcas.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error obteniendo marcas: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 4. Obtener rango de precios (min y max)
     * GET /api/productos/rango-precios
     */
    @GetMapping("/rango-precios")
    public ResponseEntity<Map<String, Object>> obtenerRangoPrecios() {
        try {
            log.info("💰 Obteniendo rango de precios");

            Map<String, Double> rango = busquedaService.obtenerRangoPrecios();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("precioMin", rango.get("precioMin"));
            response.put("precioMax", rango.get("precioMax"));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error obteniendo rango de precios: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 5. Obtener sugerencias de autocompletado
     * GET /api/productos/sugerencias?q=texto
     */
    @GetMapping("/sugerencias")
    public ResponseEntity<Map<String, Object>> obtenerSugerencias(
            @RequestParam String q,
            @RequestParam(defaultValue = "5") int limite) {
        
        try {
            log.info("💡 Obteniendo sugerencias para: {}", q);

            List<String> sugerencias = busquedaService.obtenerSugerencias(q, limite);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sugerencias", sugerencias);
            response.put("total", sugerencias.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error obteniendo sugerencias: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
