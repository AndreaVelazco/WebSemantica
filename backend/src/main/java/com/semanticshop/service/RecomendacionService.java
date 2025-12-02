package com.semanticshop.service;

import com.semanticshop.dto.ClienteDTO;
import com.semanticshop.dto.ProductoDTO;
import com.semanticshop.dto.RecomendacionDTO;
import com.semanticshop.model.Usuario;
import com.semanticshop.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para generar recomendaciones inteligentes basadas en razonamiento semántico
 * VERSIÓN 100% ADAPTADA - Sin ProductoRepository, usa solo ProductoService
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RecomendacionService {

    private final OntologyService ontologyService;
    private final ProductoService productoService;
    private final UsuarioRepository usuarioRepository;

    // ============================================
    // MÉTODOS EXISTENTES (mantener compatibilidad)
    // ============================================

    /**
     * Obtiene recomendaciones para un cliente específico (por ID de ontología)
     */
    public RecomendacionDTO getRecomendacionesParaCliente(String clienteId) {
        log.info("Generando recomendaciones para cliente: {}", clienteId);
        
        // Obtener información del cliente
        ClienteDTO cliente = getClienteInfo(clienteId);
        
        // Obtener productos recomendados (inferidos por las reglas SWRL)
        Set<OWLNamedIndividual> productosRecomendados = ontologyService
                .getObjectPropertyValues(clienteId, "productoRecomendado");
        
        // Convertir OWLNamedIndividual a ProductoDTO
        List<ProductoDTO> recomendaciones = productosRecomendados.stream()
                .map(ind -> productoService.getProductoById(getShortName(ind)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(p -> p.getStock() != null && p.getStock() > 0)
                .collect(Collectors.toList());
        
        String razon = construirRazonRecomendacion(cliente);
        
        return RecomendacionDTO.builder()
                .clienteId(clienteId)
                .clienteNombre(cliente.getNombre())
                .productos(recomendaciones)
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
     * Obtiene información de un cliente (por ID de ontología)
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
        
        // Obtener todos los accesorios usando ProductoService
        List<ProductoDTO> accesorios = productoService.getProductosByCategoria("Accesorio");
        
        // Filtrar solo los compatibles con el producto
        return accesorios.stream()
                .filter(accesorio -> productoService.sonCompatibles(productoId, accesorio.getId()))
                .filter(p -> p.getStock() != null && p.getStock() > 0)
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

    // ============================================
    // ✅ NUEVOS MÉTODOS PERSONALIZADOS POR USUARIO DB
    // ============================================

    /**
     * ✅ NUEVO: Obtener recomendaciones personalizadas para usuario de BD
     * ADAPTADO 100%: Usa solo ProductoService.getAllProductos() y filtra
     * 
     * @param userId ID del usuario en la base de datos
     * @return Lista de productos recomendados
     */
    public List<ProductoDTO> getRecomendacionesPersonalizadas(Long userId) {
        try {
            log.info("🎯 Generando recomendaciones personalizadas para usuario ID: {}", userId);
            
            // Buscar usuario en BD
            Usuario usuario = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            List<ProductoDTO> recomendaciones = new ArrayList<>();
            
            // 1. Intentar obtener del cliente de ontología si existe
            if (usuario.getClienteIdOntologia() != null && !usuario.getClienteIdOntologia().isEmpty()) {
                try {
                    log.info("🧠 Buscando en ontología: {}", usuario.getClienteIdOntologia());
                    RecomendacionDTO recOntology = getRecomendacionesParaCliente(usuario.getClienteIdOntologia());
                    if (recOntology != null && recOntology.getProductos() != null) {
                        recomendaciones.addAll(recOntology.getProductos());
                        log.info("✅ Agregadas {} recomendaciones de ontología", recOntology.getProductos().size());
                    }
                } catch (Exception e) {
                    log.warn("⚠️ No se encontró cliente en ontología: {}", usuario.getClienteIdOntologia());
                }
            }
            
            // Obtener TODOS los productos una sola vez (eficiencia)
            List<ProductoDTO> todosProductos = productoService.getAllProductos();
            log.info("📦 Total productos disponibles: {}", todosProductos.size());
            
            // 2. Recomendaciones por marca preferida
            if (usuario.getMarcaPreferida() != null && !usuario.getMarcaPreferida().isEmpty()) {
                log.info("📱 Filtrando productos de marca: {}", usuario.getMarcaPreferida());
                List<ProductoDTO> porMarca = todosProductos.stream()
                    .filter(p -> p.getMarca() != null && 
                                usuario.getMarcaPreferida().equalsIgnoreCase(p.getMarca()))
                    .collect(Collectors.toList());
                recomendaciones.addAll(porMarca);
                log.info("✅ Encontrados {} productos de marca {}", porMarca.size(), usuario.getMarcaPreferida());
            }
            
            // 3. Recomendaciones por SO preferido
            if (usuario.getSistemaOperativoPreferido() != null && !usuario.getSistemaOperativoPreferido().isEmpty()) {
                log.info("💻 Filtrando productos compatibles con: {}", usuario.getSistemaOperativoPreferido());
                String soPreferido = usuario.getSistemaOperativoPreferido();
                
                List<ProductoDTO> porSO = todosProductos.stream()
                    .filter(p -> {
                        // Buscar SO en categoría o descripción
                        if (p.getCategoria() != null && p.getCategoria().contains(soPreferido)) {
                            return true;
                        }
                        // Búsqueda más flexible por keywords
                        String keywords = (p.getNombre() + " " + p.getCategoria()).toLowerCase();
                        return keywords.contains(soPreferido.toLowerCase());
                    })
                    .collect(Collectors.toList());
                    
                recomendaciones.addAll(porSO);
                log.info("✅ Encontrados {} productos compatibles con {}", porSO.size(), soPreferido);
            }
            
            // 4. Recomendaciones por rango de precio
            if (usuario.getRangoPrecioMin() != null && usuario.getRangoPrecioMax() != null) {
                log.info("💰 Filtrando por rango de precio: ${} - ${}", 
                         usuario.getRangoPrecioMin(), usuario.getRangoPrecioMax());
                         
                List<ProductoDTO> enRango = todosProductos.stream()
                    .filter(p -> p.getPrecio() != null &&
                                p.getPrecio() >= usuario.getRangoPrecioMin() && 
                                p.getPrecio() <= usuario.getRangoPrecioMax())
                    .collect(Collectors.toList());
                    
                recomendaciones.addAll(enRango);
                log.info("✅ Encontrados {} productos en rango de precio", enRango.size());
            }
            
            // 5. Si no hay recomendaciones personalizadas, agregar productos destacados
            if (recomendaciones.isEmpty()) {
                log.info("📊 Sin recomendaciones personalizadas, agregando productos destacados");
                recomendaciones.addAll(todosProductos.stream()
                    .filter(p -> p.getStock() != null && p.getStock() > 0)
                    .sorted((p1, p2) -> Double.compare(p2.getPrecio(), p1.getPrecio())) // Más caros primero
                    .limit(10)
                    .collect(Collectors.toList()));
            }
            
            // Eliminar duplicados, filtrar por stock y ordenar
            List<ProductoDTO> resultado = recomendaciones.stream()
                .filter(p -> p.getStock() != null && p.getStock() > 0)
                .collect(Collectors.toMap(
                    ProductoDTO::getId,  // Key: ID del producto
                    p -> p,              // Value: El producto mismo
                    (p1, p2) -> p1       // Si hay duplicados, mantener el primero
                ))
                .values()
                .stream()
                .sorted((p1, p2) -> Double.compare(p2.getPrecio(), p1.getPrecio()))
                .limit(20)
                .collect(Collectors.toList());
            
            log.info("✅ Total recomendaciones finales: {}", resultado.size());
            return resultado;
            
        } catch (Exception e) {
            log.error("❌ Error generando recomendaciones: {}", e.getMessage(), e);
            // En caso de error, devolver algunos productos por defecto
            try {
                return productoService.getAllProductos().stream()
                    .filter(p -> p.getStock() != null && p.getStock() > 0)
                    .limit(10)
                    .collect(Collectors.toList());
            } catch (Exception ex) {
                return new ArrayList<>();
            }
        }
    }

    /**
     * ✅ NUEVO: Obtener recomendaciones generales (sin usuario específico)
     */
    public List<ProductoDTO> getRecomendacionesGenerales() {
        try {
            log.info("🎯 Generando recomendaciones generales");
            
            List<ProductoDTO> productos = productoService.getAllProductos();
            
            // Ordenar por stock y precio para mostrar los más relevantes
            return productos.stream()
                .filter(p -> p.getStock() != null && p.getStock() > 0)
                .sorted((p1, p2) -> {
                    // Primero por stock descendente, luego por precio descendente
                    int stockCompare = Integer.compare(p2.getStock(), p1.getStock());
                    if (stockCompare != 0) return stockCompare;
                    return Double.compare(p2.getPrecio(), p1.getPrecio());
                })
                .limit(12)
                .collect(Collectors.toList());
            
        } catch (Exception e) {
            log.error("❌ Error generando recomendaciones generales: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * ✅ NUEVO: Ejecutar el razonador para actualizar las inferencias
     */
    public void ejecutarRazonador() {
        try {
            log.info("🔄 Ejecutando razonador HermiT...");
            ontologyService.runReasoner();
            log.info("✅ Razonador ejecutado correctamente");
        } catch (Exception e) {
            log.error("❌ Error ejecutando razonador: {}", e.getMessage());
            throw new RuntimeException("Error ejecutando razonador", e);
        }
    }

    // ============================================
    // MÉTODOS HELPER PRIVADOS
    // ============================================

    private ClienteDTO convertClienteToDTO(OWLNamedIndividual individual) {
        String id = getShortName(individual);
        
        ClienteDTO dto = ClienteDTO.builder()
                .id(id)
                .nombre(getStringProperty(individual, "nombre").orElse(id))
                .build();
        
        Set<OWLClass> tipos = ontologyService.getInferredClassesOfIndividual(id);
        dto.setTipo(tipos.stream()
                .map(this::getShortName)
                .filter(name -> name.equals("ClientePremium") || name.equals("ClienteNuevo"))
                .findFirst()
                .orElse("Cliente"));
        
        Set<OWLNamedIndividual> marcas = ontologyService
                .getObjectPropertyValues(id, "tieneMarcaPreferida");
        if (!marcas.isEmpty()) {
            dto.setMarcaPreferida(getShortName(marcas.iterator().next()));
        }
        
        Set<OWLNamedIndividual> preferencias = ontologyService
                .getObjectPropertyValues(id, "tienePreferencia");
        dto.setPreferencias(preferencias.stream()
                .map(this::getShortName)
                .collect(Collectors.toList()));
        
        Set<OWLNamedIndividual> pedidos = ontologyService
                .getObjectPropertyValues(id, "realizoPedido");
        dto.setNumeroPedidos(pedidos.size());
        
        Set<OWLNamedIndividual> recomendados = ontologyService
                .getObjectPropertyValues(id, "productoRecomendado");
        dto.setProductosRecomendados(recomendados.stream()
                .map(this::getShortName)
                .collect(Collectors.toSet()));
        
        return dto;
    }

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