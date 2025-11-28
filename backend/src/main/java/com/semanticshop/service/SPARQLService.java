package com.semanticshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.*;

/**
 * Servicio para ejecutar consultas SPARQL sobre la ontología
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SPARQLService {

    private final ResourceLoader resourceLoader;
    private OntModel model;

    @PostConstruct
    public void initialize() {
        try {
            log.info("Inicializando modelo Jena para consultas SPARQL");
            model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
            
            InputStream inputStream = resourceLoader
                    .getResource("classpath:ontology/semanticshop.owl")
                    .getInputStream();
            
            model.read(inputStream, null);
            log.info("Modelo SPARQL inicializado con {} triples", model.size());
            
        } catch (Exception e) {
            log.error("Error al inicializar modelo SPARQL: {}", e.getMessage(), e);
        }
    }

    /**
     * Ejecuta una consulta SPARQL y retorna los resultados
     */
    public List<Map<String, String>> executeQuery(String sparqlQuery) {
        List<Map<String, String>> resultados = new ArrayList<>();
        
        try {
            Query query = QueryFactory.create(sparqlQuery);
            
            try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
                ResultSet results = qexec.execSelect();
                
                while (results.hasNext()) {
                    QuerySolution solution = results.nextSolution();
                    Map<String, String> row = new HashMap<>();
                    
                    Iterator<String> varNames = solution.varNames();
                    while (varNames.hasNext()) {
                        String varName = varNames.next();
                        String value = solution.get(varName).toString();
                        // Limpiar URI para obtener solo el nombre corto
                        if (value.contains("#")) {
                            value = value.substring(value.indexOf("#") + 1);
                        }
                        row.put(varName, value);
                    }
                    
                    resultados.add(row);
                }
            }
            
        } catch (Exception e) {
            log.error("Error al ejecutar consulta SPARQL: {}", e.getMessage(), e);
        }
        
        return resultados;
    }

    /**
     * Obtiene estadísticas de ventas por categoría
     */
    public List<Map<String, String>> getVentasPorCategoria() {
        String query = """
                PREFIX : <http://www.semanticshop.com/ontology#>
                
                SELECT ?categoria (COUNT(?pedido) as ?totalPedidos) (SUM(?monto) as ?montoTotal)
                WHERE {
                    ?producto :perteneceACategoria ?cat .
                    ?cat rdfs:label ?categoria .
                    ?pedido :contieneProducto ?producto .
                    ?pedido :montoTotal ?monto .
                }
                GROUP BY ?categoria
                ORDER BY DESC(?montoTotal)
                """;
        
        return executeQuery(query);
    }

    /**
     * Obtiene los productos más vendidos
     */
    public List<Map<String, String>> getProductosMasVendidos() {
        String query = """
                PREFIX : <http://www.semanticshop.com/ontology#>
                
                SELECT ?nombreProducto (COUNT(?pedido) as ?totalVentas)
                WHERE {
                    ?producto :nombre ?nombreProducto .
                    ?pedido :contieneProducto ?producto .
                }
                GROUP BY ?nombreProducto
                ORDER BY DESC(?totalVentas)
                LIMIT 10
                """;
        
        return executeQuery(query);
    }

    /**
     * Obtiene clientes premium y su total gastado
     */
    public List<Map<String, String>> getClientesPremium() {
        String query = """
                PREFIX : <http://www.semanticshop.com/ontology#>
                
                SELECT ?nombreCliente (COUNT(?pedido) as ?totalPedidos) (SUM(?monto) as ?totalGastado)
                WHERE {
                    ?cliente a :ClientePremium .
                    ?cliente :nombre ?nombreCliente .
                    ?cliente :realizoPedido ?pedido .
                    ?pedido :montoTotal ?monto .
                }
                GROUP BY ?nombreCliente
                ORDER BY DESC(?totalGastado)
                """;
        
        return executeQuery(query);
    }

    /**
     * Obtiene productos por rango de precio
     */
    public List<Map<String, String>> getProductosPorRangoPrecio(double precioMin, double precioMax) {
        String query = String.format("""
                PREFIX : <http://www.semanticshop.com/ontology#>
                
                SELECT ?nombre ?precio ?marca
                WHERE {
                    ?producto :nombre ?nombre .
                    ?producto :precio ?precio .
                    OPTIONAL { 
                        ?producto :tieneMarca ?m .
                        ?m rdfs:label ?marca 
                    }
                    FILTER(?precio >= %.2f && ?precio <= %.2f)
                }
                ORDER BY ?precio
                """, precioMin, precioMax);
        
        return executeQuery(query);
    }

    /**
     * Obtiene productos con bajo stock
     */
    public List<Map<String, String>> getProductosBajoStock(int stockMinimo) {
        String query = String.format("""
                PREFIX : <http://www.semanticshop.com/ontology#>
                
                SELECT ?nombre ?stock ?precio
                WHERE {
                    ?producto :nombre ?nombre .
                    ?producto :stock ?stock .
                    ?producto :precio ?precio .
                    FILTER(?stock <= %d && ?stock > 0)
                }
                ORDER BY ?stock
                """, stockMinimo);
        
        return executeQuery(query);
    }

    /**
     * Obtiene análisis de marcas más populares
     */
    public List<Map<String, String>> getMarcasPopulares() {
        String query = """
                PREFIX : <http://www.semanticshop.com/ontology#>
                
                SELECT ?marca (COUNT(?producto) as ?totalProductos) (COUNT(?pedido) as ?totalVentas)
                WHERE {
                    ?producto :tieneMarca ?m .
                    ?m rdfs:label ?marca .
                    OPTIONAL {
                        ?pedido :contieneProducto ?producto .
                    }
                }
                GROUP BY ?marca
                ORDER BY DESC(?totalVentas)
                """;
        
        return executeQuery(query);
    }

    /**
     * Obtiene pedidos por estado
     */
    public List<Map<String, String>> getPedidosPorEstado() {
        String query = """
                PREFIX : <http://www.semanticshop.com/ontology#>
                
                SELECT ?estado (COUNT(?pedido) as ?cantidad)
                WHERE {
                    ?pedido a :Pedido .
                    ?pedido :estadoPedido ?estado .
                }
                GROUP BY ?estado
                ORDER BY DESC(?cantidad)
                """;
        
        return executeQuery(query);
    }
}
