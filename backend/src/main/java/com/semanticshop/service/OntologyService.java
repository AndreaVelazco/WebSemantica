package com.semanticshop.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.ontology.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio principal para manejar la ontología OWL y el razonador HermiT
 * CON SOPORTE PARA CONSULTAS SPARQL
 */
@Service
public class OntologyService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OntologyService.class);



    private final ResourceLoader resourceLoader;
    
    @Value("${ontology.file.path}")
    private String ontologyPath;
    
    @Value("${ontology.namespace}")
    private String namespace;

    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLReasoner reasoner;
    private OWLDataFactory dataFactory;
    
    // Modelo Jena para SPARQL
    private OntModel jenaModel;

    public OntologyService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Inicializa la ontología y el razonador HermiT al inicio de la aplicación
     */
    @PostConstruct
    public void initialize() {
        try {
            log.info("Inicializando ontología desde: {}", ontologyPath);
            
            // Cargar ontología con OWL API
            manager = OWLManager.createOWLOntologyManager();
            Resource resource = resourceLoader.getResource(ontologyPath);
            ontology = manager.loadOntologyFromOntologyDocument(resource.getInputStream());
            dataFactory = manager.getOWLDataFactory();
            
            log.info("Ontología cargada exitosamente. IRI: {}", ontology.getOntologyID());
            log.info("Número de axiomas: {}", ontology.getAxiomCount());
            
            // ✅ Cargar modelo Jena para SPARQL
            loadJenaModel();
            
            // ✅ IMPORTANTE: Sincronizar modelos al inicio
            syncJenaModelFromOWL();
            
            // Inicializar razonador HermiT
            OWLReasonerFactory reasonerFactory = new ReasonerFactory();
            reasoner = reasonerFactory.createReasoner(ontology);
            
            // Realizar inferencias
            log.info("Ejecutando razonamiento con HermiT...");
            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
            reasoner.precomputeInferences(InferenceType.OBJECT_PROPERTY_HIERARCHY);
            reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS);
            
            // Verificar consistencia
            boolean isConsistent = reasoner.isConsistent();
            log.info("Ontología consistente: {}", isConsistent);
            
            if (!isConsistent) {
                log.error("¡ALERTA! La ontología no es consistente");
            }
            
            log.info("Razonador HermiT inicializado correctamente");
            
        } catch (Exception e) {
            log.error("Error al inicializar la ontología: {}", e.getMessage(), e);
            throw new RuntimeException("Error fatal al cargar la ontología", e);
        }
    }

    /**
     * Cargar modelo Jena para consultas SPARQL
     */
    private void loadJenaModel() {
        try {
            log.info("Cargando modelo Jena para SPARQL...");
            jenaModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            
            Resource resource = resourceLoader.getResource(ontologyPath);
            InputStream in = resource.getInputStream();
            
            if (in != null) {
                jenaModel.read(in, null);
                log.info("✅ Modelo Jena base cargado");
            }
        } catch (Exception e) {
            log.error("Error cargando modelo Jena: {}", e.getMessage());
        }
    }

    /**
     * ✅ NUEVO: Sincronizar modelo Jena desde OWL API
     * Este método copia los datos del modelo OWL API al modelo Jena
     */
    public void syncJenaModelFromOWL() {
        try {
            log.info("🔄 Sincronizando modelo Jena desde OWL API...");
            
            // Exportar ontología OWL a RDF/XML temporal
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            manager.saveOntology(ontology, new RDFXMLDocumentFormat(), outputStream);
            
            // Recargar en Jena
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            jenaModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            jenaModel.read(inputStream, null);
            
            log.info("✅ Modelo Jena sincronizado");
            log.info("   - Triples en Jena: {}", jenaModel.size());
            log.info("   - Individuos en OWL: {}", ontology.getIndividualsInSignature().size());
            
        } catch (Exception e) {
            log.error("❌ Error sincronizando modelo Jena: {}", e.getMessage(), e);
        }
    }

    /**
     * Ejecuta el razonador para precomputar inferencias
     */
    public void runReasoner() {
        log.info("Ejecutando razonador HermiT...");
        try {
            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
            reasoner.precomputeInferences(InferenceType.OBJECT_PROPERTY_HIERARCHY);
            reasoner.precomputeInferences(InferenceType.DATA_PROPERTY_HIERARCHY);
            reasoner.flush();
            
            // ✅ Sincronizar Jena después de razonar
            syncJenaModelFromOWL();
            
            log.info("✅ Razonador ejecutado exitosamente");
        } catch (Exception e) {
            log.error("❌ Error al ejecutar razonador: {}", e.getMessage());
            throw new RuntimeException("Error al ejecutar razonador", e);
        }
    }

    /**
     * Limpia recursos al cerrar la aplicación
     */
    @PreDestroy
    public void cleanup() {
        if (reasoner != null) {
            reasoner.dispose();
            log.info("Razonador HermiT cerrado");
        }
    }

    /**
     * Verifica si la ontología es consistente
     */
    public boolean isConsistent() {
        return reasoner.isConsistent();
    }

    /**
     * Obtiene todas las clases de la ontología
     */
    public Set<OWLClass> getAllClasses() {
        return ontology.getClassesInSignature();
    }

    /**
     * Obtiene todos los individuos de una clase específica
     */
    public Set<OWLNamedIndividual> getIndividualsOfClass(String className) {
        OWLClass owlClass = dataFactory.getOWLClass(IRI.create(namespace + className));
        return reasoner.getInstances(owlClass, false)
                .getFlattened();
    }

    /**
     * Obtiene las clases inferidas de un individuo
     */
    public Set<OWLClass> getInferredClassesOfIndividual(String individualName) {
        OWLNamedIndividual individual = dataFactory.getOWLNamedIndividual(
                IRI.create(namespace + individualName));
        return reasoner.getTypes(individual, false)
                .getFlattened();
    }

    /**
     * Obtiene el valor de una data property de un individuo
     */
    public Optional<String> getDataPropertyValue(String individualName, String propertyName) {
        OWLNamedIndividual individual = dataFactory.getOWLNamedIndividual(
                IRI.create(namespace + individualName));
        OWLDataProperty property = dataFactory.getOWLDataProperty(
                IRI.create(namespace + propertyName));
        
        return ontology.getDataPropertyAssertionAxioms(individual).stream()
                .filter(ax -> ax.getProperty().equals(property))
                .map(ax -> ax.getObject().getLiteral())
                .findFirst();
    }

    /**
     * Obtiene los valores de una object property de un individuo
     */
    public Set<OWLNamedIndividual> getObjectPropertyValues(String individualName, String propertyName) {
        OWLNamedIndividual individual = dataFactory.getOWLNamedIndividual(
                IRI.create(namespace + individualName));
        OWLObjectProperty property = dataFactory.getOWLObjectProperty(
                IRI.create(namespace + propertyName));
        
        return reasoner.getObjectPropertyValues(individual, property)
                .getFlattened();
    }

    // ========== MÉTODOS PARA SPARQL ==========

    /**
     * Ejecutar consulta SPARQL sobre la ontología
     */
    public List<Map<String, String>> executeSparqlQuery(String sparqlQuery) {
        List<Map<String, String>> results = new ArrayList<>();
        
        if (jenaModel == null) {
            log.error("Modelo Jena no inicializado");
            return results;
        }
        
        try {
            Query query = QueryFactory.create(sparqlQuery);
            
            try (QueryExecution qexec = QueryExecutionFactory.create(query, jenaModel)) {
                ResultSet resultSet = qexec.execSelect();
                
                while (resultSet.hasNext()) {
                    QuerySolution solution = resultSet.nextSolution();
                    Map<String, String> row = new HashMap<>();
                    
                    Iterator<String> varNames = solution.varNames();
                    while (varNames.hasNext()) {
                        String varName = varNames.next();
                        RDFNode node = solution.get(varName);
                        
                        if (node != null) {
                            if (node.isLiteral()) {
                                row.put(varName, node.asLiteral().getString());
                            } else if (node.isResource()) {
                                String uri = node.asResource().getURI();
                                if (uri != null && uri.contains("#")) {
                                    String localName = uri.substring(uri.indexOf('#') + 1);
                                    row.put(varName, localName);
                                } else {
                                    row.put(varName, uri != null ? uri : "");
                                }
                            } else {
                                row.put(varName, node.toString());
                            }
                        }
                    }
                    
                    if (!row.isEmpty()) {
                        results.add(row);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error ejecutando consulta SPARQL: {}", e.getMessage());
            throw new RuntimeException("Error en consulta SPARQL: " + e.getMessage());
        }
        
        return results;
    }

    /**
     * Ejecutar consulta SPARQL ASK (booleana)
     */
    public boolean executeSparqlAsk(String sparqlQuery) {
        if (jenaModel == null) {
            log.error("Modelo Jena no inicializado");
            return false;
        }
        
        try {
            Query query = QueryFactory.create(sparqlQuery);
            
            try (QueryExecution qexec = QueryExecutionFactory.create(query, jenaModel)) {
                return qexec.execAsk();
            }
        } catch (Exception e) {
            log.error("Error ejecutando ASK query: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtener información general de la ontología (para endpoints REST)
     */
    public Map<String, Object> getOntologyInfo() {
        Map<String, Object> info = new HashMap<>();
        
        try {
            info.put("framework", "OWL API + HermiT + Apache Jena");
            info.put("reasoner", "HermiT 1.4.5");
            info.put("consistent", reasoner.isConsistent());
            info.put("classes", ontology.getClassesInSignature().size());
            info.put("individuals", ontology.getIndividualsInSignature().size());
            info.put("objectProperties", ontology.getObjectPropertiesInSignature().size());
            info.put("dataProperties", ontology.getDataPropertiesInSignature().size());
            info.put("axioms", ontology.getAxiomCount());
            info.put("jenaTriples", jenaModel != null ? jenaModel.size() : 0);
        } catch (Exception e) {
            log.error("Error obteniendo información de ontología: {}", e.getMessage());
        }
        
        return info;
    }

    /**
     * Obtener lista de nombres de clases (para endpoints REST)
     */
    public List<String> getClassNames() {
        return ontology.getClassesInSignature().stream()
                .filter(owlClass -> !owlClass.isOWLThing() && !owlClass.isOWLNothing())
                .map(owlClass -> owlClass.getIRI().getFragment())
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Obtener lista de nombres de individuos de una clase (para endpoints REST)
     */
    public List<String> getIndividualNames(String className) {
        Set<OWLNamedIndividual> individuals = getIndividualsOfClass(className);
        return individuals.stream()
                .map(ind -> ind.getIRI().getFragment())
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Verificar consistencia (para endpoints REST)
     */
    public boolean checkConsistency() {
        try {
            reasoner.precomputeInferences();
            boolean consistent = reasoner.isConsistent();
            log.info("Verificación de consistencia: {}", consistent);
            return consistent;
        } catch (Exception e) {
            log.error("Error verificando consistencia: {}", e.getMessage());
            return false;
        }
    }

    // Getters
    public OWLOntology getOntology() {
        return ontology;
    }

    public OWLReasoner getReasoner() {
        return reasoner;
    }

    public OWLDataFactory getDataFactory() {
        return dataFactory;
    }

    public String getNamespace() {
        return namespace;
    }
    
    public OWLOntologyManager getManager() {
        return manager;
    }
    
    public OntModel getJenaModel() {
        return jenaModel;
    }
}