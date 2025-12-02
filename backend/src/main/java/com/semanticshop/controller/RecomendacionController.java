package com.semanticshop.controller;

import com.semanticshop.dto.ClienteDTO;
import com.semanticshop.dto.ProductoDTO;
import com.semanticshop.dto.RecomendacionDTO;
import com.semanticshop.model.Usuario;
import com.semanticshop.repository.UsuarioRepository;
import com.semanticshop.service.RecomendacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller REST para endpoints de recomendaciones
 * VERSIÓN FINAL - Compatible con Postman collection
 */
@RestController
@RequestMapping("/api/recomendaciones")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RecomendacionController {

    private final RecomendacionService recomendacionService;
    private final UsuarioRepository usuarioRepository;

    // ============================================
    // ENDPOINTS EXISTENTES (mantener compatibilidad)
    // ============================================

    /**
     * ⭐ ENDPOINT USADO EN POSTMAN
     * Obtener recomendaciones para un cliente de la ontología
     * GET /api/recomendaciones/{clienteId}
     * Ejemplos:
     * - /api/recomendaciones/Cliente_juan_perez
     * - /api/recomendaciones/cliente1
     */
    @GetMapping("/{clienteId}")
    public ResponseEntity<Map<String, Object>> getRecomendacionesCliente(@PathVariable String clienteId) {
        try {
            log.info("📥 Solicitud de recomendaciones para cliente: {}", clienteId);
            
            RecomendacionDTO recomendacion = recomendacionService.getRecomendacionesParaCliente(clienteId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("clienteId", recomendacion.getClienteId());
            response.put("clienteNombre", recomendacion.getClienteNombre());
            response.put("productos", recomendacion.getProductos());
            response.put("razon", recomendacion.getRazonRecomendacion());
            response.put("totalRecomendaciones", recomendacion.getTotalRecomendaciones());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo recomendaciones: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Alias del endpoint anterior (para compatibilidad)
     * GET /api/recomendaciones/cliente/{clienteId}
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Map<String, Object>> getRecomendacionesClienteAlias(@PathVariable String clienteId) {
        return getRecomendacionesCliente(clienteId);
    }

    /**
     * Obtener todos los clientes de la ontología
     * GET /api/recomendaciones/clientes
     */
    @GetMapping("/clientes")
    public ResponseEntity<Map<String, Object>> getAllClientes() {
        try {
            List<ClienteDTO> clientes = recomendacionService.getAllClientes();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total", clientes.size());
            response.put("clientes", clientes);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo clientes: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Obtener información detallada de un cliente
     * GET /api/recomendaciones/clientes/{clienteId}
     */
    @GetMapping("/clientes/{clienteId}")
    public ResponseEntity<Map<String, Object>> getClienteInfo(@PathVariable String clienteId) {
        try {
            ClienteDTO cliente = recomendacionService.getClienteInfo(clienteId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("cliente", cliente);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo información del cliente: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Recomendar accesorios compatibles para un producto
     * GET /api/recomendaciones/accesorios/{productoId}
     */
    @GetMapping("/accesorios/{productoId}")
    public ResponseEntity<Map<String, Object>> getAccesoriosCompatibles(@PathVariable String productoId) {
        try {
            List<ProductoDTO> accesorios = recomendacionService.recomendarAccesoriosCompatibles(productoId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("productoId", productoId);
            response.put("totalAccesorios", accesorios.size());
            response.put("accesorios", accesorios);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo accesorios: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Recomendar productos basados en historial de compras
     * GET /api/recomendaciones/historial/{clienteId}
     */
    @GetMapping("/historial/{clienteId}")
    public ResponseEntity<Map<String, Object>> getRecomendacionesPorHistorial(@PathVariable String clienteId) {
        try {
            List<ProductoDTO> recomendaciones = recomendacionService.recomendarBasadoEnHistorial(clienteId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("clienteId", clienteId);
            response.put("totalRecomendaciones", recomendaciones.size());
            response.put("productos", recomendaciones);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo recomendaciones por historial: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // ============================================
    // ✅ NUEVOS ENDPOINTS PERSONALIZADOS
    // ============================================

    /**
     * ✅ NUEVO: Obtener recomendaciones personalizadas para el usuario autenticado
     * GET /api/recomendaciones/para-mi
     * Requiere: Token JWT (Bearer token en header Authorization)
     * 
     * Este endpoint es más inteligente que el de Postman porque:
     * 1. Usa el token JWT para identificar al usuario automáticamente
     * 2. No necesitas saber el clienteId de ontología
     * 3. Combina datos de BD + Ontología
     * 4. Incluye fallbacks si no hay datos en ontología
     */
    @GetMapping("/para-mi")
    public ResponseEntity<Map<String, Object>> getRecomendacionesParaMi() {
        try {
            // Obtener usuario autenticado desde el SecurityContext
            // El JwtAuthenticationFilter ya validó el token y estableció la autenticación
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            log.info("🔐 Usuario autenticado: {}", username);
            
            // Buscar usuario en BD
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Generar recomendaciones personalizadas
            List<ProductoDTO> productos = recomendacionService.getRecomendacionesPersonalizadas(usuario.getId());
            
            // Construir respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("usuario", usuario.getUsername());
            response.put("usuarioId", usuario.getId());
            response.put("totalRecomendaciones", productos.size());
            response.put("productos", productos);
            
            // Agregar preferencias del usuario
            Map<String, Object> preferencias = new HashMap<>();
            preferencias.put("marcaPreferida", usuario.getMarcaPreferida());
            preferencias.put("soPreferido", usuario.getSistemaOperativoPreferido());
            preferencias.put("rangoPrecioMin", usuario.getRangoPrecioMin());
            preferencias.put("rangoPrecioMax", usuario.getRangoPrecioMax());
            response.put("preferencias", preferencias);
            
            // Info adicional útil
            if (usuario.getClienteIdOntologia() != null) {
                response.put("clienteOntologia", usuario.getClienteIdOntologia());
                response.put("nota", "Recomendaciones combinadas: Ontología + Base de Datos + Preferencias");
            } else {
                response.put("nota", "Recomendaciones basadas en: Base de Datos + Preferencias");
            }
            
            log.info("✅ Recomendaciones generadas: {} productos", productos.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error generando recomendaciones personalizadas: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * ✅ NUEVO: Obtener recomendaciones para un usuario específico (admin)
     * GET /api/recomendaciones/para-usuario/{userId}
     * Requiere: Token JWT
     */
    @GetMapping("/para-usuario/{userId}")
    public ResponseEntity<Map<String, Object>> getRecomendacionesParaUsuario(@PathVariable Long userId) {
        try {
            log.info("📥 Solicitud de recomendaciones para usuario ID: {}", userId);
            
            // Buscar usuario
            Usuario usuario = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Generar recomendaciones
            List<ProductoDTO> productos = recomendacionService.getRecomendacionesPersonalizadas(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("usuario", usuario.getUsername());
            response.put("usuarioId", usuario.getId());
            response.put("totalRecomendaciones", productos.size());
            response.put("productos", productos);
            
            Map<String, Object> preferencias = new HashMap<>();
            preferencias.put("marcaPreferida", usuario.getMarcaPreferida());
            preferencias.put("soPreferido", usuario.getSistemaOperativoPreferido());
            preferencias.put("rangoPrecioMin", usuario.getRangoPrecioMin());
            preferencias.put("rangoPrecioMax", usuario.getRangoPrecioMax());
            response.put("preferencias", preferencias);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo recomendaciones para usuario: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * ✅ NUEVO: Obtener recomendaciones generales (sin autenticación)
     * GET /api/recomendaciones/general
     */
    @GetMapping("/general")
    public ResponseEntity<Map<String, Object>> getRecomendacionesGenerales() {
        try {
            log.info("📥 Solicitud de recomendaciones generales");
            
            List<ProductoDTO> productos = recomendacionService.getRecomendacionesGenerales();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalRecomendaciones", productos.size());
            response.put("productos", productos);
            response.put("mensaje", "Productos destacados del catálogo");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error obteniendo recomendaciones generales: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * ✅ NUEVO: Obtener resumen de recomendaciones (para dashboard)
     * GET /api/recomendaciones/resumen
     * Requiere: Token JWT
     */
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> getResumenRecomendaciones() {
        try {
            // Obtener usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Generar recomendaciones
            List<ProductoDTO> productos = recomendacionService.getRecomendacionesPersonalizadas(usuario.getId());
            
            // Crear resumen
            Map<String, Object> resumen = new HashMap<>();
            resumen.put("totalRecomendaciones", productos.size());
            resumen.put("top3", productos.stream().limit(3).collect(Collectors.toList()));
            
            // Categorías recomendadas
            Set<String> categorias = productos.stream()
                .map(ProductoDTO::getCategoria)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            resumen.put("categoriasRecomendadas", categorias);
            
            // Marcas recomendadas
            Set<String> marcas = productos.stream()
                .map(ProductoDTO::getMarca)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            resumen.put("marcasRecomendadas", marcas);
            
            // Rango de precios
            OptionalDouble precioMin = productos.stream()
                .mapToDouble(ProductoDTO::getPrecio)
                .min();
            OptionalDouble precioMax = productos.stream()
                .mapToDouble(ProductoDTO::getPrecio)
                .max();
                
            if (precioMin.isPresent() && precioMax.isPresent()) {
                resumen.put("rangoPreciosRecomendados", Map.of(
                    "min", precioMin.getAsDouble(),
                    "max", precioMax.getAsDouble()
                ));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("usuario", username);
            response.put("resumen", resumen);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error generando resumen: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * ✅ NUEVO: Ejecutar razonador HermiT
     * POST /api/recomendaciones/refrescar
     * Requiere: Token JWT
     */
    @PostMapping("/refrescar")
    public ResponseEntity<Map<String, Object>> refrescarRazonador() {
        try {
            log.info("🔄 Solicitud de ejecución del razonador");
            
            recomendacionService.ejecutarRazonador();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("mensaje", "Razonador ejecutado correctamente");
            response.put("timestamp", new Date());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error ejecutando razonador: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}