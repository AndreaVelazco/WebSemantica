package com.semanticshop.controller;

import com.semanticshop.service.OntologyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.model.OWLClass;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión y validación de la ontología
 */
@RestController
@RequestMapping("/api/ontology")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Ontología", description = "API para gestión y validación de la ontología")
@CrossOrigin(origins = "*")
public class OntologyController {

    private final OntologyService ontologyService;

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
}
