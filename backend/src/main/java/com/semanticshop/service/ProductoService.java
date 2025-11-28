package com.semanticshop.service;

import com.semanticshop.dto.ProductoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar productos y sus relaciones
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductoService {

    private final OntologyService ontologyService;

    /**
     * Obtiene todos los productos del catálogo
     */
    public List<ProductoDTO> getAllProductos() {
        Set<OWLNamedIndividual> productos = ontologyService.getIndividualsOfClass("Producto");
        
        return productos.stream()
                .map(this::convertToDTO)
                .sorted(Comparator.comparing(ProductoDTO::getNombre))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un producto por su ID
     */
    public Optional<ProductoDTO> getProductoById(String id) {
        try {
            OWLNamedIndividual individual = ontologyService.getDataFactory()
                    .getOWLNamedIndividual(IRI.create(ontologyService.getNamespace() + id));
            
            // Verificar si existe en la ontología
            if (!ontologyService.getOntology().containsIndividualInSignature(individual.getIRI())) {
                return Optional.empty();
            }
            
            return Optional.of(convertToDTO(individual));
        } catch (Exception e) {
            log.error("Error al obtener producto {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Obtiene productos por categoría
     */
    public List<ProductoDTO> getProductosByCategoria(String categoria) {
        Set<OWLNamedIndividual> productos = ontologyService.getIndividualsOfClass(categoria);
        
        return productos.stream()
                .map(this::convertToDTO)
                .sorted(Comparator.comparing(ProductoDTO::getPrecio))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene productos compatibles con un producto dado
     */
    public List<ProductoDTO> getProductosCompatibles(String productoId) {
        Set<OWLNamedIndividual> compatibles = ontologyService
                .getObjectPropertyValues(productoId, "esCompatibleCon");
        
        return compatibles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene productos incompatibles con un producto dado
     */
    public List<ProductoDTO> getProductosIncompatibles(String productoId) {
        Set<OWLNamedIndividual> incompatibles = ontologyService
                .getObjectPropertyValues(productoId, "esIncompatibleCon");
        
        return incompatibles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Verifica si dos productos son compatibles
     */
    public boolean sonCompatibles(String producto1Id, String producto2Id) {
        Set<OWLNamedIndividual> compatibles = ontologyService
                .getObjectPropertyValues(producto1Id, "esCompatibleCon");
        
        return compatibles.stream()
                .anyMatch(ind -> getShortName(ind).equals(producto2Id));
    }

    /**
     * Busca productos por nombre o características
     */
    public List<ProductoDTO> buscarProductos(String query) {
        List<ProductoDTO> todosProductos = getAllProductos();
        String queryLower = query.toLowerCase();
        
        return todosProductos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(queryLower) ||
                            p.getMarca().toLowerCase().contains(queryLower) ||
                            p.getTipo().toLowerCase().contains(queryLower))
                .collect(Collectors.toList());
    }

    /**
     * Convierte un OWLNamedIndividual a ProductoDTO
     */
    private ProductoDTO convertToDTO(OWLNamedIndividual individual) {
        String id = getShortName(individual);
        
        ProductoDTO dto = ProductoDTO.builder()
                .id(id)
                .nombre(getStringProperty(individual, "nombre").orElse(id))
                .precio(getDoubleProperty(individual, "precio").orElse(0.0))
                .stock(getIntProperty(individual, "stock").orElse(0))
                .build();
        
        // Obtener tipo del producto (clase más específica)
        Set<OWLClass> tipos = ontologyService.getInferredClassesOfIndividual(id);
        dto.setTipo(tipos.stream()
                .map(this::getShortName)
                .filter(name -> !name.equals("Producto") && !name.equals("Thing"))
                .findFirst()
                .orElse("Producto"));
        
        // Obtener marca
        Set<OWLNamedIndividual> marcas = ontologyService
                .getObjectPropertyValues(id, "tieneMarca");
        if (!marcas.isEmpty()) {
            dto.setMarca(getShortName(marcas.iterator().next()));
        }
        
        // Obtener categoría
        Set<OWLNamedIndividual> categorias = ontologyService
                .getObjectPropertyValues(id, "perteneceACategoria");
        if (!categorias.isEmpty()) {
            dto.setCategoria(getShortName(categorias.iterator().next()));
        }
        
        // Obtener características
        Set<OWLNamedIndividual> caracteristicas = ontologyService
                .getObjectPropertyValues(id, "tieneCaracteristica");
        dto.setCaracteristicas(caracteristicas.stream()
                .map(this::getShortName)
                .collect(Collectors.toList()));
        
        // Obtener compatibilidades (inferidas por HermiT)
        Set<OWLNamedIndividual> compatibles = ontologyService
                .getObjectPropertyValues(id, "esCompatibleCon");
        dto.setProductosCompatibles(compatibles.stream()
                .map(this::getShortName)
                .collect(Collectors.toSet()));
        
        // Obtener incompatibilidades (inferidas por HermiT)
        Set<OWLNamedIndividual> incompatibles = ontologyService
                .getObjectPropertyValues(id, "esIncompatibleCon");
        dto.setProductosIncompatibles(incompatibles.stream()
                .map(this::getShortName)
                .collect(Collectors.toSet()));
        
        return dto;
    }

    /**
     * Obtiene el nombre corto de un individuo o clase
     */
    private String getShortName(OWLNamedIndividual individual) {
        return individual.getIRI().getShortForm();
    }

    private String getShortName(OWLClass owlClass) {
        return owlClass.getIRI().getShortForm();
    }

    /**
     * Helper methods para obtener propiedades
     */
    private Optional<String> getStringProperty(OWLNamedIndividual individual, String propertyName) {
        return ontologyService.getDataPropertyValue(getShortName(individual), propertyName);
    }

    private Optional<Double> getDoubleProperty(OWLNamedIndividual individual, String propertyName) {
        return getStringProperty(individual, propertyName)
                .map(Double::parseDouble);
    }

    private Optional<Integer> getIntProperty(OWLNamedIndividual individual, String propertyName) {
        return getStringProperty(individual, propertyName)
                .map(Integer::parseInt);
    }
}
