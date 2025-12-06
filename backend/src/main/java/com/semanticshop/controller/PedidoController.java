package com.semanticshop.controller;

import com.semanticshop.dto.PedidoDTO;
import com.semanticshop.model.Usuario;
import com.semanticshop.repository.UsuarioRepository;
import com.semanticshop.service.PedidoService;
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
 * Controller REST para gestión de pedidos
 */
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;
    private final UsuarioRepository usuarioRepository;

    /**
     * 1. Crear pedido desde el carrito
     * POST /api/pedidos/crear
     */
    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> crearPedido(@RequestBody Map<String, Object> request) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            String direccionEnvio = (String) request.get("direccionEnvio");
            String notas = (String) request.get("notas");
            
            log.info("📦 Creando pedido para usuario: {}", usuario.getUsername());
            
            PedidoDTO pedido = pedidoService.crearPedido(usuario, direccionEnvio, notas);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mensaje", "Pedido creado exitosamente");
            response.put("pedido", pedido);
            response.put("pedidoId", pedido.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error creando pedido: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 2. Obtener historial de pedidos del usuario
     * GET /api/pedidos/mis-pedidos
     */
    @GetMapping("/mis-pedidos")
    public ResponseEntity<Map<String, Object>> obtenerMisPedidos() {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            log.info("📋 Obteniendo pedidos de: {}", usuario.getUsername());
            
            List<PedidoDTO> pedidos = pedidoService.obtenerMisPedidos(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("usuario", usuario.getUsername());
            response.put("totalPedidos", pedidos.size());
            response.put("pedidos", pedidos);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo pedidos: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 3. Obtener detalle de un pedido específico
     * GET /api/pedidos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPedido(@PathVariable Long id) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            log.info("📄 Obteniendo detalle del pedido #{}", id);
            
            PedidoDTO pedido = pedidoService.obtenerPedido(usuario, id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("pedido", pedido);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo pedido: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 4. Cancelar un pedido
     * PUT /api/pedidos/{id}/cancelar
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Map<String, Object>> cancelarPedido(@PathVariable Long id) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            log.info("❌ Usuario {} cancelando pedido #{}", usuario.getUsername(), id);
            
            PedidoDTO pedido = pedidoService.cancelarPedido(usuario, id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mensaje", "Pedido cancelado exitosamente");
            response.put("pedido", pedido);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error cancelando pedido: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 5. Actualizar estado de un pedido (ADMIN)
     * PUT /api/pedidos/{id}/estado
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<Map<String, Object>> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            String nuevoEstado = (String) request.get("estado");
            
            if (nuevoEstado == null || nuevoEstado.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "El estado es obligatorio"));
            }
            
            log.info("🔄 Actualizando estado del pedido #{} a {}", id, nuevoEstado);
            
            PedidoDTO pedido = pedidoService.actualizarEstado(id, nuevoEstado);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mensaje", "Estado actualizado exitosamente");
            response.put("pedido", pedido);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error actualizando estado: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 6. Obtener todos los pedidos (ADMIN)
     * GET /api/pedidos
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerTodosPedidos() {
        try {
            log.info("📋 Obteniendo todos los pedidos (ADMIN)");
            
            List<PedidoDTO> pedidos = pedidoService.obtenerTodosPedidos();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalPedidos", pedidos.size());
            response.put("pedidos", pedidos);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo todos los pedidos: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 7. Obtener pedidos por estado (ADMIN)
     * GET /api/pedidos/por-estado/{estado}
     */
    @GetMapping("/por-estado/{estado}")
    public ResponseEntity<Map<String, Object>> obtenerPedidosPorEstado(@PathVariable String estado) {
        try {
            log.info("📋 Obteniendo pedidos por estado: {}", estado);
            
            List<PedidoDTO> pedidos = pedidoService.obtenerPedidosPorEstado(estado);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("estado", estado);
            response.put("totalPedidos", pedidos.size());
            response.put("pedidos", pedidos);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo pedidos por estado: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * 8. Obtener estadísticas de pedidos (ADMIN)
     * GET /api/pedidos/estadisticas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        try {
            log.info("📊 Obteniendo estadísticas de pedidos (ADMIN)");
            
            Map<String, Object> estadisticas = pedidoService.obtenerEstadisticas();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("estadisticas", estadisticas);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo estadísticas: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * BONUS: Obtener estadísticas del usuario actual
     * GET /api/pedidos/mis-estadisticas
     */
    @GetMapping("/mis-estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerMisEstadisticas() {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            log.info("📊 Obteniendo estadísticas de: {}", usuario.getUsername());
            
            Map<String, Object> estadisticas = pedidoService.obtenerEstadisticasUsuario(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("usuario", usuario.getUsername());
            response.put("estadisticas", estadisticas);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo estadísticas: {}", e.getMessage());
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