package com.semanticshop.service;

import com.semanticshop.dto.BusquedaProductosRequest;
import com.semanticshop.dto.BusquedaProductosResponse;
import com.semanticshop.dto.ProductoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para búsqueda y filtrado avanzado de productos
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BusquedaProductoService {

    private final ProductoService productoService;

    /**
     * Búsqueda avanzada con filtros, ordenamiento y paginación
     */
    public BusquedaProductosResponse buscarProductos(BusquedaProductosRequest request) {
        log.info("🔍 Búsqueda avanzada: q={}, categoria={}, marca={}, precioMin={}, precioMax={}",
                request.getQ(), request.getCategoria(), request.getMarca(), 
                request.getPrecioMin(), request.getPrecioMax());

        // Obtener todos los productos (con retry en caso de error del razonador)
        List<ProductoDTO> todosProductos = obtenerProductosConRetry();

        // Aplicar filtros
        List<ProductoDTO> productosFiltrados = aplicarFiltros(todosProductos, request);

        // Aplicar ordenamiento
        List<ProductoDTO> productosOrdenados = aplicarOrdenamiento(productosFiltrados, request);

        // Aplicar paginación
        List<ProductoDTO> productosPaginados = aplicarPaginacion(productosOrdenados, request);

        // Construir respuesta
        BusquedaProductosResponse response = construirRespuesta(
                productosPaginados,
                productosOrdenados.size(),
                request
        );

        log.info("✅ Búsqueda completada: {} resultados encontrados", productosOrdenados.size());

        return response;
    }

    /**
     * Aplicar todos los filtros a la lista de productos
     */
    private List<ProductoDTO> aplicarFiltros(List<ProductoDTO> productos, BusquedaProductosRequest request) {
        return productos.stream()
                .filter(p -> filtrarPorTexto(p, request.getQ()))
                .filter(p -> filtrarPorCategoria(p, request.getCategoria()))
                .filter(p -> filtrarPorMarca(p, request.getMarca()))
                .filter(p -> filtrarPorPrecio(p, request.getPrecioMin(), request.getPrecioMax()))
                .filter(p -> filtrarPorDisponibilidad(p, request.getDisponible()))
                .collect(Collectors.toList());
    }

    /**
     * Filtrar por texto de búsqueda (busca en nombre, marca, categoría, descripción)
     */
    private boolean filtrarPorTexto(ProductoDTO producto, String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return true;
        }

        String textoLower = texto.toLowerCase().trim();

        // Buscar en nombre
        if (producto.getNombre() != null && 
            producto.getNombre().toLowerCase().contains(textoLower)) {
            return true;
        }

        // Buscar en marca
        if (producto.getMarca() != null && 
            producto.getMarca().toLowerCase().contains(textoLower)) {
            return true;
        }

        // Buscar en categoría
        if (producto.getCategoria() != null && 
            producto.getCategoria().toLowerCase().contains(textoLower)) {
            return true;
        }

        // Buscar en descripción
        if (producto.getDescripcion() != null && 
            producto.getDescripcion().toLowerCase().contains(textoLower)) {
            return true;
        }

        // Buscar en tipo
        if (producto.getTipo() != null && 
            producto.getTipo().toLowerCase().contains(textoLower)) {
            return true;
        }

        return false;
    }

    /**
     * Filtrar por categoría
     */
    private boolean filtrarPorCategoria(ProductoDTO producto, String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            return true;
        }
        return producto.getCategoria() != null && 
               producto.getCategoria().equalsIgnoreCase(categoria.trim());
    }

    /**
     * Filtrar por marca
     */
    private boolean filtrarPorMarca(ProductoDTO producto, String marca) {
        if (marca == null || marca.trim().isEmpty()) {
            return true;
        }
        return producto.getMarca() != null && 
               producto.getMarca().equalsIgnoreCase(marca.trim());
    }

    /**
     * Filtrar por rango de precio
     */
    private boolean filtrarPorPrecio(ProductoDTO producto, Double precioMin, Double precioMax) {
        if (producto.getPrecio() == null) {
            return false;
        }

        boolean cumpleMin = precioMin == null || producto.getPrecio() >= precioMin;
        boolean cumpleMax = precioMax == null || producto.getPrecio() <= precioMax;

        return cumpleMin && cumpleMax;
    }

    /**
     * Filtrar por disponibilidad
     */
    private boolean filtrarPorDisponibilidad(ProductoDTO producto, Boolean soloDisponibles) {
        if (!soloDisponibles) {
            return true;
        }
        return producto.getStock() != null && producto.getStock() > 0;
    }

    /**
     * Aplicar ordenamiento
     */
    private List<ProductoDTO> aplicarOrdenamiento(List<ProductoDTO> productos, BusquedaProductosRequest request) {
        String campo = request.getOrdenarPor();
        String direccion = request.getDireccion();
        boolean ascendente = "asc".equalsIgnoreCase(direccion);

        List<ProductoDTO> productosOrdenados = new ArrayList<>(productos);

        switch (campo.toLowerCase()) {
            case "precio":
                productosOrdenados.sort((p1, p2) -> {
                    Double precio1 = p1.getPrecio() != null ? p1.getPrecio() : 0.0;
                    Double precio2 = p2.getPrecio() != null ? p2.getPrecio() : 0.0;
                    return ascendente ? precio1.compareTo(precio2) : precio2.compareTo(precio1);
                });
                break;

            case "nombre":
                productosOrdenados.sort((p1, p2) -> {
                    String nombre1 = p1.getNombre() != null ? p1.getNombre() : "";
                    String nombre2 = p2.getNombre() != null ? p2.getNombre() : "";
                    return ascendente ? nombre1.compareTo(nombre2) : nombre2.compareTo(nombre1);
                });
                break;

            case "popularidad":
            case "stock":
                productosOrdenados.sort((p1, p2) -> {
                    Integer stock1 = p1.getStock() != null ? p1.getStock() : 0;
                    Integer stock2 = p2.getStock() != null ? p2.getStock() : 0;
                    return ascendente ? stock1.compareTo(stock2) : stock2.compareTo(stock1);
                });
                break;

            default:
                // Por defecto ordenar por nombre
                productosOrdenados.sort((p1, p2) -> {
                    String nombre1 = p1.getNombre() != null ? p1.getNombre() : "";
                    String nombre2 = p2.getNombre() != null ? p2.getNombre() : "";
                    return nombre1.compareTo(nombre2);
                });
                break;
        }

        return productosOrdenados;
    }

    /**
     * Aplicar paginación
     */
    private List<ProductoDTO> aplicarPaginacion(List<ProductoDTO> productos, BusquedaProductosRequest request) {
        int pagina = request.getPagina();
        int tamanio = request.getTamanio();

        int inicio = pagina * tamanio;
        int fin = Math.min(inicio + tamanio, productos.size());

        if (inicio >= productos.size()) {
            return Collections.emptyList();
        }

        return productos.subList(inicio, fin);
    }

    /**
     * Construir respuesta completa
     */
    private BusquedaProductosResponse construirRespuesta(
            List<ProductoDTO> productosPaginados,
            int totalElementos,
            BusquedaProductosRequest request) {

        int totalPaginas = (int) Math.ceil((double) totalElementos / request.getTamanio());

        BusquedaProductosResponse.PaginacionInfo paginacion = BusquedaProductosResponse.PaginacionInfo.builder()
                .paginaActual(request.getPagina())
                .tamanioPagina(request.getTamanio())
                .totalElementos((long) totalElementos)
                .totalPaginas(totalPaginas)
                .esUltimaPagina(request.getPagina() >= totalPaginas - 1)
                .esPrimeraPagina(request.getPagina() == 0)
                .build();

        BusquedaProductosResponse.FiltrosAplicados filtros = BusquedaProductosResponse.FiltrosAplicados.builder()
                .busqueda(request.getQ())
                .categoria(request.getCategoria())
                .marca(request.getMarca())
                .precioMin(request.getPrecioMin())
                .precioMax(request.getPrecioMax())
                .disponible(request.getDisponible())
                .build();

        BusquedaProductosResponse.OrdenamientoInfo ordenamiento = BusquedaProductosResponse.OrdenamientoInfo.builder()
                .campo(request.getOrdenarPor())
                .direccion(request.getDireccion())
                .build();

        return BusquedaProductosResponse.builder()
                .productos(productosPaginados)
                .paginacion(paginacion)
                .filtrosAplicados(filtros)
                .ordenamiento(ordenamiento)
                .build();
    }

    /**
     * Obtener productos con retry en caso de error del razonador
     */
    private List<ProductoDTO> obtenerProductosConRetry() {
        int intentos = 0;
        int maxIntentos = 3;
        
        while (intentos < maxIntentos) {
            try {
                return productoService.getAllProductos();
            } catch (Exception e) {
                intentos++;
                log.warn("⚠️ Error obteniendo productos (intento {}/{}): {}", 
                        intentos, maxIntentos, e.getMessage());
                
                if (intentos >= maxIntentos) {
                    log.error("❌ Error después de {} intentos, devolviendo lista vacía", maxIntentos);
                    return new ArrayList<>();
                }
                
                // Esperar un poco antes de reintentar
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        return new ArrayList<>();
    }

    /**
     * Obtener todas las categorías disponibles
     */
    public List<String> obtenerCategorias() {
        return obtenerProductosConRetry().stream()
                .map(ProductoDTO::getCategoria)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Obtener todas las marcas disponibles
     */
    public List<String> obtenerMarcas() {
        return obtenerProductosConRetry().stream()
                .map(ProductoDTO::getMarca)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Obtener rango de precios (min y max)
     */
    public Map<String, Double> obtenerRangoPrecios() {
        List<ProductoDTO> productos = obtenerProductosConRetry();

        DoubleSummaryStatistics stats = productos.stream()
                .map(ProductoDTO::getPrecio)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        Map<String, Double> rango = new HashMap<>();
        rango.put("precioMin", stats.getMin());
        rango.put("precioMax", stats.getMax());

        return rango;
    }

    /**
     * Obtener sugerencias de autocompletado
     */
    public List<String> obtenerSugerencias(String texto, int limite) {
        if (texto == null || texto.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String textoLower = texto.toLowerCase().trim();

        return obtenerProductosConRetry().stream()
                .map(ProductoDTO::getNombre)
                .filter(Objects::nonNull)
                .filter(nombre -> nombre.toLowerCase().contains(textoLower))
                .distinct()
                .limit(limite)
                .sorted()
                .collect(Collectors.toList());
    }
}