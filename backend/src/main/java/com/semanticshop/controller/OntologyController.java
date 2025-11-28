package com.semanticshop.controller;

import com.semanticshop.model.Usuario;
import com.semanticshop.repository.UsuarioRepository;
import com.semanticshop.service.OntologyService;
import com.semanticshop.service.OntologySyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.model.OWLClass;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión, validación y sincronización de la ontología
 */
@RestController
@RequestMapping("/api/ontology")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Ontología", description = "API para gestión, validación y sincronización de la ontología")
@CrossOrigin(origins = "*")
public class OntologyController {

    private final OntologyService ontologyService;
    private final OntologySyncService ontologySyncService;
    private final UsuarioRepository usuarioRepository;

    // ==================== ENDPOINTS DE VALIDACIÓN ====================

    @GetMapping("/consistencia")
    @Operation(summary = "Verificar consistencia de la ontología", 
               description = "Valida que la ontología sea lógicamente consistente usando HermiT")
    public ResponseEntity<Map<String, Object>> verificarConsistencia() {
        log.info("GET /api/ontology/consistencia - Verificando consistencia");
        
        boolean consistente = ontologyService.isConsistent();
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("consistente", consistente);
        resultado.put("timestamp", new Date());
        resultado.put("razonador", "HermiT");
        
        if (consistente) {
            resultado.put("mensaje", "La ontología es consistente");
            resultado.put("status", "OK");
        } else {
            resultado.put("mensaje", "¡ALERTA! La ontología contiene inconsistencias lógicas");
            resultado.put("status", "ERROR");
        }
        
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas de la ontología", 
               description = "Retorna información sobre clases, individuos y axiomas")
    public ResponseEntity<Map<String, Object>> getEstadisticas() {
        log.info("GET /api/ontology/estadisticas");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAxiomas", ontologyService.getOntology().getAxiomCount());
        stats.put("totalClases", ontologyService.getAllClasses().size());
        stats.put("totalIndividuos", ontologyService.getOntology().getIndividualsInSignature().size());
        stats.put("namespace", ontologyService.getNamespace());
        stats.put("ontologyIRI", ontologyService.getOntology().getOntologyID().getOntologyIRI()
                .map(Object::toString).orElse("N/A"));
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/clases")
    @Operation(summary = "Listar todas las clases", 
               description = "Obtiene todas las clases definidas en la ontología")
    public ResponseEntity<List<String>> getAllClasses() {
        log.info("GET /api/ontology/clases");
        
        Set<OWLClass> classes = ontologyService.getAllClasses();
        List<String> classNames = classes.stream()
                .map(c -> c.getIRI().getShortForm())
                .filter(name -> !name.equals("Thing")) // Excluir owl:Thing
                .sorted()
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(classNames);
    }

    @GetMapping("/individuos/{clase}")
    @Operation(summary = "Obtener individuos de una clase", 
               description = "Lista todos los individuos que pertenecen a una clase específica")
    public ResponseEntity<List<String>> getIndividualsOfClass(@PathVariable String clase) {
        log.info("GET /api/ontology/individuos/{}", clase);
        
        List<String> individuos = ontologyService.getIndividualsOfClass(clase).stream()
                .map(ind -> ind.getIRI().getShortForm())
                .sorted()
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(individuos);
    }

    @GetMapping("/info")
    @Operation(summary = "Información general del sistema", 
               description = "Retorna información sobre el estado del sistema de razonamiento")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        log.info("GET /api/ontology/info");
        
        Map<String, Object> info = new HashMap<>();
        info.put("aplicacion", "SemanticShop");
        info.put("version", "1.0.0");
        info.put("razonador", "HermiT 1.4.5");
        info.put("framework", "OWL API 5.5.0 + Apache Jena 4.10.0");
        info.put("consistente", ontologyService.isConsistent());
        info.put("timestamp", new Date());
        
        return ResponseEntity.ok(info);
    }

    // ==================== ENDPOINTS DE SINCRONIZACIÓN ====================

    @PostMapping("/sync")
    @Operation(summary = "Sincronizar usuario actual con la ontología",
               description = "Crea o actualiza el perfil del usuario autenticado en la ontología semántica")
    public ResponseEntity<Map<String, Object>> sincronizarUsuario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        log.info("POST /api/ontology/sync - Solicitud de sincronización para usuario: {}", username);

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Map<String, Object> response = new HashMap<>();
        
        try {
            ontologySyncService.sincronizarUsuarioConOntologia(usuario);
            
            response.put("success", true);
            response.put("message", "Usuario sincronizado exitosamente con la ontología");
            response.put("clienteIdOntologia", usuario.getClienteIdOntologia());
            response.put("username", usuario.getUsername());
            response.put("marcaPreferida", usuario.getMarcaPreferida());
            response.put("sistemaOperativo", usuario.getSistemaOperativoPreferido());
            response.put("timestamp", new Date());
            
            log.info("✅ Sincronización exitosa para: {}", usuario.getClienteIdOntologia());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error en sincronización: {}", e.getMessage(), e);
            
            response.put("success", false);
            response.put("message", "Error al sincronizar: " + e.getMessage());
            response.put("clienteIdOntologia", usuario.getClienteIdOntologia());
            response.put("error", e.getClass().getSimpleName());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/check")
    @Operation(summary = "Verificar si el usuario existe en la ontología",
               description = "Comprueba si el usuario autenticado está registrado en la ontología semántica")
    public ResponseEntity<Map<String, Object>> verificarExistencia() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        log.info("GET /api/ontology/check - Verificando existencia para: {}", username);

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String clienteId = usuario.getClienteIdOntologia();
        boolean existe = ontologySyncService.clienteExisteEnOntologia(clienteId);

        Map<String, Object> response = new HashMap<>();
        response.put("clienteIdOntologia", clienteId);
        response.put("username", usuario.getUsername());
        response.put("existeEnOntologia", existe);
        response.put("timestamp", new Date());
        
        if (existe) {
            response.put("mensaje", "✅ El cliente existe en la ontología");
            response.put("status", "SINCRONIZADO");
        } else {
            response.put("mensaje", "❌ El cliente NO existe en la ontología");
            response.put("status", "NO_SINCRONIZADO");
            response.put("accion", "Ejecuta POST /api/ontology/sync para sincronizar");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/sync/status")
    @Operation(summary = "Estado detallado de sincronización",
               description = "Muestra información completa sobre el estado del usuario en la ontología")
    public ResponseEntity<Map<String, Object>> estadoSincronizacion() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String clienteId = usuario.getClienteIdOntologia();
        boolean existe = ontologySyncService.clienteExisteEnOntologia(clienteId);

        Map<String, Object> response = new HashMap<>();
        
        // Información del usuario
        response.put("usuario", Map.of(
            "id", usuario.getId(),
            "username", usuario.getUsername(),
            "nombreCompleto", usuario.getNombreCompleto(),
            "email", usuario.getEmail()
        ));
        
        // Estado en ontología
        response.put("ontologia", Map.of(
            "clienteId", clienteId,
            "existe", existe,
            "sincronizado", existe
        ));
        
        // Preferencias
        response.put("preferencias", Map.of(
            "marcaPreferida", usuario.getMarcaPreferida() != null ? usuario.getMarcaPreferida() : "No especificada",
            "sistemaOperativo", usuario.getSistemaOperativoPreferido() != null ? usuario.getSistemaOperativoPreferido() : "No especificado",
            "rangoPrecio", String.format("$%.2f - $%.2f", 
                usuario.getRangoPrecioMin() != null ? usuario.getRangoPrecioMin() : 0.0,
                usuario.getRangoPrecioMax() != null ? usuario.getRangoPrecioMax() : 0.0)
        ));
        
        return ResponseEntity.ok(response);
    }
}