package com.semanticshop.service;

import com.semanticshop.dto.ClienteDTO;
import com.semanticshop.dto.ProductoDTO;
import com.semanticshop.dto.RecomendacionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para generar recomendaciones inteligentes basadas en razonamiento semántico
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RecomendacionService {

    private final OntologyService ontologyService;
    private final ProductoService productoService;

    /**
     * Obtiene recomendaciones para un cliente específico
     * Las recomendaciones son inferidas por HermiT basándose en:
     * - Marca preferida del cliente
     * - Sistema operativo preferido
     * - Tipo de conector preferido
     */
    public RecomendacionDTO getRecomendacionesParaCliente(String clienteId) {
        log.info("Generando recomendaciones para cliente: {}", clienteId);
        
        // Obtener información del cliente
        ClienteDTO cliente = getClienteInfo(clienteId);
        
        // Obtener productos recomendados (inferidos por las reglas SWRL)
        Set<OWLNamedIndividual> productosRecomendados = ontologyService
                .getObjectPropertyValues(clienteId, "productoRecomendado");
        
        List<ProductoDTO> recomendaciones = productosRecomendados.stream()
                .map(ind -> productoService.getProductoById(getShortName(ind)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(ProductoDTO::isDisponible) // Solo productos en stock
                .collect(Collectors.toList());
        
        String razon = construirRazonRecomendacion(cliente);
        
        return RecomendacionDTO.builder()
                .clienteId(clienteId)
                .clienteNombre(cliente.getNombre())
                .productosRecomendados(recomendaciones)
                .razonRecomendacion(razon)
                .totalRecomendaciones(recomendaciones.size())
                .build();
    }

    /**
     * Obtiene todos los clientes
     */
    public List<ClienteDTO> getAllClientes() {
        Set<OWLNamedIndividual> clientes = ontologyService.getIndividualsOfClass("Cliente");
        
        return clientes.stream()
                .map(this::convertClienteToDTO)
                .sorted(Comparator.comparing(ClienteDTO::getNombre))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene información de un cliente
     */
    public ClienteDTO getClienteInfo(String clienteId) {
        OWLNamedIndividual individual = ontologyService.getDataFactory()
                .getOWLNamedIndividual(IRI.create(ontologyService.getNamespace() + clienteId));
        
        return convertClienteToDTO(individual);
    }

    /**
     * Recomienda accesorios compatibles para un producto
     */
    public List<ProductoDTO> recomendarAccesoriosCompatibles(String productoId) {
        log.info("Buscando accesorios compatibles para: {}", productoId);
        
        // Obtener todos los accesorios
        List<ProductoDTO> accesorios = productoService.getProductosByCategoria("Accesorio");
        
        // Filtrar solo los compatibles con el producto
        return accesorios.stream()
                .filter(accesorio -> productoService.sonCompatibles(productoId, accesorio.getId()))
                .filter(ProductoDTO::isDisponible)
                .collect(Collectors.toList());
    }

    /**
     * Recomienda productos basados en el historial de compras
     */
    public List<ProductoDTO> recomendarBasadoEnHistorial(String clienteId) {
        // Obtener pedidos del cliente
        Set<OWLNamedIndividual> pedidos = ontologyService
                .getObjectPropertyValues(clienteId, "realizoPedido");
        
        // Obtener productos de los pedidos
        Set<String> productosComprados = new HashSet<>();
        for (OWLNamedIndividual pedido : pedidos) {
            Set<OWLNamedIndividual> productos = ontologyService
                    .getObjectPropertyValues(getShortName(pedido), "contieneProducto");
            productos.forEach(p -> productosComprados.add(getShortName(p)));
        }
        
        // Recomendar accesorios compatibles con productos comprados
        Set<ProductoDTO> recomendaciones = new HashSet<>();
        for (String productoId : productosComprados) {
            recomendaciones.addAll(recomendarAccesoriosCompatibles(productoId));
        }
        
        return new ArrayList<>(recomendaciones);
    }

    /**
     * Convierte un OWLNamedIndividual de cliente a ClienteDTO
     */
    private ClienteDTO convertClienteToDTO(OWLNamedIndividual individual) {
        String id = getShortName(individual);
        
        ClienteDTO dto = ClienteDTO.builder()
                .id(id)
                .nombre(getStringProperty(individual, "nombre").orElse(id))
                .email(getStringProperty(individual, "email").orElse(""))
                .build();
        
        // Determinar tipo de cliente (inferido por HermiT)
        Set<OWLClass> tipos = ontologyService.getInferredClassesOfIndividual(id);
        dto.setTipo(tipos.stream()
                .map(this::getShortName)
                .filter(name -> name.equals("ClientePremium") || name.equals("ClienteNuevo"))
                .findFirst()
                .orElse("Cliente"));
        
        // Obtener marca preferida
        Set<OWLNamedIndividual> marcas = ontologyService
                .getObjectPropertyValues(id, "tieneMarcaPreferida");
        if (!marcas.isEmpty()) {
            dto.setMarcaPreferida(getShortName(marcas.iterator().next()));
        }
        
        // Obtener preferencias
        Set<OWLNamedIndividual> preferencias = ontologyService
                .getObjectPropertyValues(id, "tienePreferencia");
        dto.setPreferencias(preferencias.stream()
                .map(this::getShortName)
                .collect(Collectors.toList()));
        
        // Contar pedidos
        Set<OWLNamedIndividual> pedidos = ontologyService
                .getObjectPropertyValues(id, "realizoPedido");
        dto.setNumeroPedidos(pedidos.size());
        
        // Obtener productos recomendados
        Set<OWLNamedIndividual> recomendados = ontologyService
                .getObjectPropertyValues(id, "productoRecomendado");
        dto.setProductosRecomendados(recomendados.stream()
                .map(this::getShortName)
                .collect(Collectors.toSet()));
        
        return dto;
    }

    /**
     * Construye la razón de la recomendación basada en las preferencias del cliente
     */
    private String construirRazonRecomendacion(ClienteDTO cliente) {
        StringBuilder razon = new StringBuilder("Recomendado porque: ");
        
        if (cliente.getMarcaPreferida() != null) {
            razon.append("te gusta la marca ").append(cliente.getMarcaPreferida());
        }
        
        if (cliente.getPreferencias() != null && !cliente.getPreferencias().isEmpty()) {
            if (cliente.getMarcaPreferida() != null) {
                razon.append(" y ");
            }
            razon.append("prefieres ").append(String.join(", ", cliente.getPreferencias()));
        }
        
        if (cliente.getTipo().equals("ClientePremium")) {
            razon.append(". Como cliente Premium, tienes acceso a productos exclusivos");
        }
        
        return razon.toString();
    }

    /**
     * Métodos helper
     */
    private String getShortName(OWLNamedIndividual individual) {
        return individual.getIRI().getShortForm();
    }

    private String getShortName(OWLClass owlClass) {
        return owlClass.getIRI().getShortForm();
    }

    private Optional<String> getStringProperty(OWLNamedIndividual individual, String propertyName) {
        return ontologyService.getDataPropertyValue(getShortName(individual), propertyName);
    }
}
