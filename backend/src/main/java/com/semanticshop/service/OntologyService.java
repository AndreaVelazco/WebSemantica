package com.semanticshop.service;

import lombok.extern.slf4j.Slf4j;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio principal para manejar la ontología OWL y el razonador HermiT
 */
@Service
@Slf4j
public class OntologyService {

    private final ResourceLoader resourceLoader;
    
    @Value("${ontology.file.path}")
    private String ontologyPath;
    
    @Value("${ontology.namespace}")
    private String namespace;

    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLReasoner reasoner;
    private OWLDataFactory dataFactory;

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
            
            // Cargar ontología
            manager = OWLManager.createOWLOntologyManager();
            Resource resource = resourceLoader.getResource(ontologyPath);
            ontology = manager.loadOntologyFromOntologyDocument(resource.getInputStream());
            dataFactory = manager.getOWLDataFactory();
            
            log.info("Ontología cargada exitosamente. IRI: {}", ontology.getOntologyID());
            log.info("Número de axiomas: {}", ontology.getAxiomCount());
            
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
     * Ejecuta el razonador para precomputar inferencias
     */
    public void runReasoner() {
        log.info("Ejecutando razonador HermiT...");
        try {
            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
            reasoner.precomputeInferences(InferenceType.OBJECT_PROPERTY_HIERARCHY);
            reasoner.precomputeInferences(InferenceType.DATA_PROPERTY_HIERARCHY);
            reasoner.flush();
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
}