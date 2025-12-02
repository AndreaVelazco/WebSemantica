package com.semanticshop.controller;

import com.semanticshop.service.OntologyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Locale;

import java.util.*;

/**
 * Controlador REST para operaciones SPARQL
 * VERSIÓN FINAL - Compatible con ontología SemanticShop
 */
@RestController
@RequestMapping("/api/sparql")
@CrossOrigin(origins = "*")
@Slf4j
public class SparqlController {

    @Autowired
    private OntologyService ontologyService;

    /**
     * Forzar sincronización de modelos
     */
    @PostMapping("/sync")
    public ResponseEntity<?> syncModels() {
        try {
            log.info("🔄 Forzando sincronización de modelos...");
            ontologyService.syncJenaModelFromOWL();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Modelos sincronizados correctamente");
            response.put("jenaTriples", ontologyService.getJenaModel().size());
            response.put("owlIndividuals", ontologyService.getOntology().getIndividualsInSignature().size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sincronizando modelos: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Ejecutar consulta SPARQL personalizada
     */
    @PostMapping("/query")
    public ResponseEntity<?> executeQuery(@RequestBody Map<String, String> request) {
        try {
            String query = request.get("query");
            if (query == null || query.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Query no puede estar vacía");
                return ResponseEntity.badRequest().body(error);
            }
            
            List<Map<String, String>> results = ontologyService.executeSparqlQuery(query);
            
            Map<String, Object> response = new HashMap<>();
            response.put("query", query);
            response.put("resultCount", results.size());
            response.put("results", results);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error ejecutando query: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * ✅ CORREGIDO: Obtener todos los productos (usando subclases)
     * Ya que los individuos NO están declarados directamente como Producto,
     * buscamos en todas las subclases conocidas
     */
    @GetMapping("/analytics/todos-productos")
    public ResponseEntity<?> getTodosProductos() {
        String query = 
            "PREFIX : <http://www.semanticshop.com/ontology#>\n" +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "\n" +
            "SELECT DISTINCT ?producto ?tipo\n" +
            "WHERE {\n" +
            "  ?producto rdf:type ?tipo .\n" +
            "  ?tipo rdfs:subClassOf* :Producto .\n" +
            "  FILTER (?tipo != :Producto)\n" +
            "  FILTER (!isBlank(?producto))\n" +
            "}\n" +
            "ORDER BY ?producto";
        
        List<Map<String, String>> results = ontologyService.executeSparqlQuery(query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("analysis", "Todos los productos");
        response.put("note", "Productos obtenidos por jerarquía de clases (Smartphone, Laptop, Audifonos, Cable, Cargador son subclases de Producto)");
        response.put("resultCount", results.size());
        response.put("results", results);
        
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ YA FUNCIONA: Productos por marca
     */
    @GetMapping("/analytics/productos-por-marca")
    public ResponseEntity<?> getProductosPorMarca() {
        String query = 
            "PREFIX : <http://www.semanticshop.com/ontology#>\n" +
            "\n" +
            "SELECT ?marca (COUNT(DISTINCT ?producto) as ?cantidad)\n" +
            "WHERE {\n" +
            "  ?producto :tieneMarca ?marca .\n" +
            "}\n" +
            "GROUP BY ?marca\n" +
            "ORDER BY DESC(?cantidad)";
        
        List<Map<String, String>> results = ontologyService.executeSparqlQuery(query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("analysis", "Productos por marca");
        response.put("results", results);
        
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ CORREGIDO: Productos por categoría
     */
    @GetMapping("/analytics/productos-por-categoria")
    public ResponseEntity<?> getProductosPorCategoria() {
        String query = 
            "PREFIX : <http://www.semanticshop.com/ontology#>\n" +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "\n" +
            "SELECT ?categoria (COUNT(DISTINCT ?producto) as ?cantidad)\n" +
            "WHERE {\n" +
            "  ?producto rdf:type ?categoria .\n" +
            "  ?categoria rdfs:subClassOf+ :Producto .\n" +
            "  FILTER (?categoria NOT IN (:Producto, :Computadora, :Accesorio))\n" +
            "}\n" +
            "GROUP BY ?categoria\n" +
            "ORDER BY DESC(?cantidad)";
        
        List<Map<String, String>> results = ontologyService.executeSparqlQuery(query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("analysis", "Productos por categoría");
        response.put("results", results);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener preferencias de clientes
     */
    @GetMapping("/analytics/preferencias-clientes")
    public ResponseEntity<?> getPreferenciasClientes() {
        String query = 
            "PREFIX : <http://www.semanticshop.com/ontology#>\n" +
            "\n" +
            "SELECT ?cliente ?marcaPreferida ?soPreferido\n" +
            "WHERE {\n" +
            "  ?cliente :tieneMarcaPreferida ?marcaPreferida .\n" +
            "  OPTIONAL { ?cliente :tienePreferencia ?soPreferido }\n" +
            "}";
        
        List<Map<String, String>> results = ontologyService.executeSparqlQuery(query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("analysis", "Preferencias de clientes");
        response.put("results", results);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener productos por rango de precio
     */
    @GetMapping("/analytics/productos-precio-rango")
public ResponseEntity<?> getProductosPorRangoPrecio(
        @RequestParam(defaultValue = "0") double min,
        @RequestParam(defaultValue = "10000") double max) {
    
    // ✅ Usar Locale.US para garantizar punto decimal
    String query = String.format(Locale.US,
        "PREFIX : <http://www.semanticshop.com/ontology#>\n" +
        "\n" +
        "SELECT ?producto ?nombre ?precio\n" +
        "WHERE {\n" +
        "  ?producto :precio ?precio .\n" +
        "  OPTIONAL { ?producto :nombre ?nombre }\n" +
        "  FILTER (?precio >= %f && ?precio <= %f)\n" +
        "}\n" +
        "ORDER BY ?precio", min, max);
    
    List<Map<String, String>> results = ontologyService.executeSparqlQuery(query);
    
    Map<String, Object> response = new HashMap<>();
    response.put("analysis", "Productos por rango de precio");
    
    Map<String, Double> range = new HashMap<>();
    range.put("min", min);
    range.put("max", max);
    response.put("range", range);
    
    response.put("results", results);
    
    return ResponseEntity.ok(response);
}

    /**
     * Análisis de compatibilidad
     */
    @GetMapping("/analytics/compatibilidad")
    public ResponseEntity<?> getAnalisisCompatibilidad() {
        String query = 
            "PREFIX : <http://www.semanticshop.com/ontology#>\n" +
            "\n" +
            "SELECT ?producto1 ?producto2\n" +
            "WHERE {\n" +
            "  ?producto1 :esCompatibleCon ?producto2 .\n" +
            "}";
        
        List<Map<String, String>> results = ontologyService.executeSparqlQuery(query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("analysis", "Productos compatibles");
        response.put("note", "Compatibilidad inferida por reglas SWRL");
        response.put("results", results);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Análisis de incompatibilidad
     */
    @GetMapping("/analytics/incompatibilidad")
    public ResponseEntity<?> getAnalisisIncompatibilidad() {
        String query = 
            "PREFIX : <http://www.semanticshop.com/ontology#>\n" +
            "\n" +
            "SELECT ?producto1 ?producto2\n" +
            "WHERE {\n" +
            "  ?producto1 :esIncompatibleCon ?producto2 .\n" +
            "}";
        
        List<Map<String, String>> results = ontologyService.executeSparqlQuery(query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("analysis", "Productos incompatibles");
        response.put("note", "Incompatibilidad inferida por reglas SWRL");
        response.put("results", results);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Recomendaciones activas
     */
    @GetMapping("/analytics/recomendaciones-activas")
    public ResponseEntity<?> getRecomendacionesActivas() {
        String query = 
            "PREFIX : <http://www.semanticshop.com/ontology#>\n" +
            "\n" +
            "SELECT ?cliente ?producto\n" +
            "WHERE {\n" +
            "  ?cliente :productoRecomendado ?producto .\n" +
            "}";
        
        List<Map<String, String>> results = ontologyService.executeSparqlQuery(query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("analysis", "Recomendaciones activas");
        response.put("note", "Recomendaciones inferidas por reglas SWRL basadas en marca y SO preferido");
        response.put("results", results);
        
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ CORREGIDO: Obtener todos los smartphones
     */
    @GetMapping("/analytics/smartphones")
    public ResponseEntity<?> getSmartphones() {
        String query = 
            "PREFIX : <http://www.semanticshop.com/ontology#>\n" +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "\n" +
            "SELECT ?producto ?nombre ?precio ?marca\n" +
            "WHERE {\n" +
            "  ?producto rdf:type :Smartphone .\n" +
            "  OPTIONAL { ?producto :nombre ?nombre }\n" +
            "  OPTIONAL { ?producto :precio ?precio }\n" +
            "  OPTIONAL { ?producto :tieneMarca ?marca }\n" +
            "}";
        
        List<Map<String, String>> results = ontologyService.executeSparqlQuery(query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("analysis", "Todos los smartphones");
        response.put("results", results);
        
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ NUEVO: Obtener todos los laptops
     */
    @GetMapping("/analytics/laptops")
    public ResponseEntity<?> getLaptops() {
        String query = 
            "PREFIX : <http://www.semanticshop.com/ontology#>\n" +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "\n" +
            "SELECT ?producto ?nombre ?precio ?marca\n" +
            "WHERE {\n" +
            "  ?producto rdf:type :Laptop .\n" +
            "  OPTIONAL { ?producto :nombre ?nombre }\n" +
            "  OPTIONAL { ?producto :precio ?precio }\n" +
            "  OPTIONAL { ?producto :tieneMarca ?marca }\n" +
            "}";
        
        List<Map<String, String>> results = ontologyService.executeSparqlQuery(query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("analysis", "Todos los laptops");
        response.put("results", results);
        
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ NUEVO: Obtener información de clientes
     */
    @GetMapping("/analytics/clientes")
    public ResponseEntity<?> getClientes() {
        String query = 
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX : <http://www.semanticshop.com/ontology#>\n" +
            "\n" +
            "SELECT ?cliente ?nombre ?email ?marca ?so\n" +
            "WHERE {\n" +
            "  ?cliente rdf:type :Cliente .\n" +
            "  OPTIONAL { ?cliente :nombre ?nombre }\n" +
            "  OPTIONAL { ?cliente :email ?email }\n" +
            "  OPTIONAL { ?cliente :tieneMarcaPreferida ?marca }\n" +
            "  OPTIONAL { ?cliente :tienePreferencia ?so }\n" +
            "}";
        
        List<Map<String, String>> results = ontologyService.executeSparqlQuery(query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("analysis", "Información de clientes");
        response.put("results", results);
        
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ NUEVO: Análisis de pedidos
     */
    @GetMapping("/analytics/pedidos")
    public ResponseEntity<?> getPedidos() {
        String query = 
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX : <http://www.semanticshop.com/ontology#>\n" +
            "\n" +
            "SELECT ?pedido ?producto ?cantidad ?monto ?estado ?fecha\n" +
            "WHERE {\n" +
            "  ?pedido rdf:type :Pedido .\n" +
            "  OPTIONAL { ?pedido :contieneProducto ?producto }\n" +
            "  OPTIONAL { ?pedido :cantidadProducto ?cantidad }\n" +
            "  OPTIONAL { ?pedido :montoTotal ?monto }\n" +
            "  OPTIONAL { ?pedido :estadoPedido ?estado }\n" +
            "  OPTIONAL { ?pedido :fechaPedido ?fecha }\n" +
            "}\n" +
            "ORDER BY ?pedido";
        
        List<Map<String, String>> results = ontologyService.executeSparqlQuery(query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("analysis", "Análisis de pedidos");
        response.put("results", results);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Información sobre el endpoint SPARQL
     */
    @GetMapping("/info")
    public ResponseEntity<?> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("message", "Endpoint SPARQL para SemanticShop");
        info.put("namespace", "http://www.semanticshop.com/ontology#");
        info.put("jenaTriples", ontologyService.getJenaModel().size());
        info.put("owlIndividuals", ontologyService.getOntology().getIndividualsInSignature().size());
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("POST /api/sparql/sync", "✅ Sincronizar modelos OWL → Jena");
        endpoints.put("POST /api/sparql/query", "Ejecutar consulta SPARQL personalizada");
        endpoints.put("GET /api/sparql/analytics/todos-productos", "✅ Todos los productos (usando jerarquía rdfs:subClassOf)");
        endpoints.put("GET /api/sparql/analytics/productos-por-marca", "✅ Análisis de productos por marca");
        endpoints.put("GET /api/sparql/analytics/productos-por-categoria", "✅ Análisis de productos por categoría");
        endpoints.put("GET /api/sparql/analytics/preferencias-clientes", "Preferencias de clientes");
        endpoints.put("GET /api/sparql/analytics/productos-precio-rango", "Productos por rango de precio");
        endpoints.put("GET /api/sparql/analytics/compatibilidad", "Análisis de compatibilidad (SWRL)");
        endpoints.put("GET /api/sparql/analytics/incompatibilidad", "Análisis de incompatibilidad (SWRL)");
        endpoints.put("GET /api/sparql/analytics/recomendaciones-activas", "Recomendaciones activas (SWRL)");
        endpoints.put("GET /api/sparql/analytics/smartphones", "✅ Obtener todos los smartphones");
        endpoints.put("GET /api/sparql/analytics/laptops", "✅ Obtener todos los laptops");
        endpoints.put("GET /api/sparql/analytics/clientes", "✅ Información de clientes");
        endpoints.put("GET /api/sparql/analytics/pedidos", "✅ Análisis de pedidos");
        info.put("endpoints", endpoints);
        
        Map<String, Object> example = new HashMap<>();
        example.put("method", "POST");
        example.put("url", "/api/sparql/query");
        
        Map<String, String> exampleBody = new HashMap<>();
        exampleBody.put("query", "PREFIX : <http://www.semanticshop.com/ontology#> SELECT ?s WHERE { ?s ?p ?o } LIMIT 10");
        example.put("body", exampleBody);
        
        info.put("example", example);
        
        info.put("note", "✅ La clase Producto SÍ existe. Los individuos están en subclases (Smartphone, Laptop, etc.) y se recuperan usando rdfs:subClassOf*");
        
        return ResponseEntity.ok(info);
    }
}