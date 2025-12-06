package com.semanticshop.controller;

import com.semanticshop.dto.CarritoDTO;
import com.semanticshop.dto.CarritoItemDTO;
import com.semanticshop.model.Usuario;
import com.semanticshop.repository.UsuarioRepository;
import com.semanticshop.service.CarritoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestión del carrito de compras
 */
@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CarritoController {

    private final CarritoService carritoService;
    private final UsuarioRepository usuarioRepository;

    /**
     * 1. Agregar producto al carrito
     * POST /api/carrito/agregar
     */
    @PostMapping("/agregar")
    public ResponseEntity<Map<String, Object>> agregarProducto(
            @RequestBody Map<String, Object> request) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            String productoId = (String) request.get("productoId");
            Integer cantidad = (Integer) request.get("cantidad");
            
            if (productoId == null || productoId.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "El ID del producto es obligatorio"));
            }
            
            if (cantidad == null || cantidad < 1) {
                cantidad = 1;
            }
            
            log.info("🛒 Agregando {} x {} al carrito de {}", cantidad, productoId, usuario.getUsername());
            
            CarritoItemDTO item = carritoService.agregarProducto(usuario, productoId, cantidad);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mensaje", "Producto agregado al carrito");
            response.put("item", item);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error agregando producto al carrito: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 2. Ver carrito completo
     * GET /api/carrito
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerCarrito() {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            log.info("📋 Obteniendo carrito de {}", usuario.getUsername());
            
            CarritoDTO carrito = carritoService.obtenerCarrito(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("usuario", usuario.getUsername());
            response.put("carrito", carrito);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo carrito: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 3. Actualizar cantidad de un producto
     * PUT /api/carrito/actualizar/{productoId}
     */
    @PutMapping("/actualizar/{productoId}")
    public ResponseEntity<Map<String, Object>> actualizarCantidad(
            @PathVariable String productoId,
            @RequestBody Map<String, Object> request) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            Integer cantidad = (Integer) request.get("cantidad");
            
            if (cantidad == null || cantidad < 1) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "La cantidad debe ser al menos 1"));
            }
            
            log.info("🔄 Actualizando cantidad de {} a {} para {}", 
                    productoId, cantidad, usuario.getUsername());
            
            CarritoItemDTO item = carritoService.actualizarCantidad(usuario, productoId, cantidad);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mensaje", "Cantidad actualizada");
            response.put("item", item);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error actualizando cantidad: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 4. Eliminar producto del carrito
     * DELETE /api/carrito/eliminar/{productoId}
     */
    @DeleteMapping("/eliminar/{productoId}")
    public ResponseEntity<Map<String, Object>> eliminarProducto(@PathVariable String productoId) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            log.info("🗑️ Eliminando {} del carrito de {}", productoId, usuario.getUsername());
            
            carritoService.eliminarProducto(usuario, productoId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mensaje", "Producto eliminado del carrito");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error eliminando producto: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 5. Vaciar carrito completo
     * DELETE /api/carrito/limpiar
     */
    @DeleteMapping("/limpiar")
    public ResponseEntity<Map<String, Object>> limpiarCarrito() {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            log.info("🧹 Limpiando carrito de {}", usuario.getUsername());
            
            carritoService.limpiarCarrito(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mensaje", "Carrito limpiado");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error limpiando carrito: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 6. Calcular total del carrito
     * GET /api/carrito/total
     */
    @GetMapping("/total")
    public ResponseEntity<Map<String, Object>> calcularTotal() {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            Double total = carritoService.calcularTotal(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total", total);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error calculando total: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 7. Obtener cantidad de items en el carrito
     * GET /api/carrito/cantidad
     */
    @GetMapping("/cantidad")
    public ResponseEntity<Map<String, Object>> obtenerCantidad() {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            Integer cantidad = carritoService.obtenerCantidadItems(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("cantidad", cantidad);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo cantidad: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 8. Verificar disponibilidad de stock
     * POST /api/carrito/verificar-stock
     */
    @PostMapping("/verificar-stock")
    public ResponseEntity<Map<String, Object>> verificarStock() {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            log.info("🔍 Verificando stock para carrito de {}", usuario.getUsername());
            
            List<String> errores = carritoService.verificarStock(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", errores.isEmpty());
            response.put("disponible", errores.isEmpty());
            response.put("errores", errores);
            
            if (errores.isEmpty()) {
                response.put("mensaje", "Todo el stock está disponible");
            } else {
                response.put("mensaje", "Hay problemas de stock con algunos productos");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error verificando stock: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 9. Verificar compatibilidad de productos
     * POST /api/carrito/verificar-compatibilidad
     */
    @PostMapping("/verificar-compatibilidad")
    public ResponseEntity<Map<String, Object>> verificarCompatibilidad() {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            log.info("🔍 Verificando compatibilidad para carrito de {}", usuario.getUsername());
            
            List<String> advertencias = carritoService.verificarCompatibilidad(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("compatible", advertencias.isEmpty());
            response.put("advertencias", advertencias);
            
            if (advertencias.isEmpty()) {
                response.put("mensaje", "Todos los productos son compatibles");
            } else {
                response.put("mensaje", "Se encontraron advertencias de compatibilidad");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error verificando compatibilidad: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 10. Obtener resumen del carrito (para dashboard)
     * GET /api/carrito/resumen
     */
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> obtenerResumen() {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            CarritoDTO resumen = carritoService.obtenerResumen(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("resumen", resumen);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo resumen: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // ============================================
    // MÉTODO HELPER
    // ============================================

    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}