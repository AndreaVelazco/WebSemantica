package com.semanticshop.service;

import com.semanticshop.dto.CarritoDTO;
import com.semanticshop.dto.CarritoItemDTO;
import com.semanticshop.dto.ProductoDTO;
import com.semanticshop.model.Carrito;
import com.semanticshop.model.Usuario;
import com.semanticshop.repository.CarritoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar el carrito de compras
 */
@Service

@RequiredArgsConstructor
public class CarritoService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CarritoService.class);

    private final CarritoRepository carritoRepository;
    private final ProductoService productoService;

    /**
     * Agregar un producto al carrito
     */
    @Transactional
    public CarritoItemDTO agregarProducto(Usuario usuario, String productoId, Integer cantidad) {
        log.info("🛒 Agregando producto {} al carrito del usuario {}", productoId, usuario.getUsername());

        // Verificar que el producto existe
        ProductoDTO producto = productoService.getProductoById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));

        // Verificar stock disponible
        if (producto.getStock() == null || producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + 
                (producto.getStock() != null ? producto.getStock() : 0));
        }

        // Buscar si el producto ya está en el carrito
        Optional<Carrito> carritoExistente = carritoRepository
                .findByUsuarioAndProductoId(usuario, productoId);

        Carrito carrito;
        if (carritoExistente.isPresent()) {
            // Actualizar cantidad existente
            carrito = carritoExistente.get();
            int nuevaCantidad = carrito.getCantidad() + cantidad;
            
            // Verificar stock para la nueva cantidad
            if (producto.getStock() < nuevaCantidad) {
                throw new RuntimeException("Stock insuficiente para la cantidad solicitada. Disponible: " + producto.getStock());
            }
            
            carrito.setCantidad(nuevaCantidad);
            log.info("✅ Cantidad actualizada a {} para producto {}", nuevaCantidad, productoId);
        } else {
            // Crear nuevo item en el carrito
            carrito = Carrito.builder()
                    .usuario(usuario)
                    .productoId(productoId)
                    .cantidad(cantidad)
                    .build();
            log.info("✅ Producto {} agregado al carrito", productoId);
        }

        carrito = carritoRepository.save(carrito);
        return convertirACarritoItemDTO(carrito, producto);
    }

    /**
     * Obtener el carrito completo del usuario
     */
    @Transactional(readOnly = true)
    public CarritoDTO obtenerCarrito(Usuario usuario) {
        log.info("📋 Obteniendo carrito del usuario {}", usuario.getUsername());

        List<Carrito> items = carritoRepository.findByUsuarioOrderByFechaAgregadoDesc(usuario);

        List<CarritoItemDTO> itemsDTO = items.stream()
                .map(this::convertirACarritoItemDTOConProducto)
                .collect(Collectors.toList());

        // Calcular totales
        int cantidadTotal = itemsDTO.stream()
                .mapToInt(CarritoItemDTO::getCantidad)
                .sum();

        double total = itemsDTO.stream()
                .mapToDouble(item -> item.getSubtotal() != null ? item.getSubtotal() : 0.0)
                .sum();

        boolean todosDisponibles = itemsDTO.stream()
                .allMatch(item -> item.getDisponible() != null && item.getDisponible());

        return CarritoDTO.builder()
                .items(itemsDTO)
                .cantidadTotal(cantidadTotal)
                .total(total)
                .todosDisponibles(todosDisponibles)
                .build();
    }

    /**
     * Actualizar la cantidad de un producto en el carrito
     */
    @Transactional
    public CarritoItemDTO actualizarCantidad(Usuario usuario, String productoId, Integer nuevaCantidad) {
        log.info("🔄 Actualizando cantidad de {} a {} para usuario {}", 
                productoId, nuevaCantidad, usuario.getUsername());

        if (nuevaCantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser al menos 1");
        }

        // Buscar el item en el carrito
        Carrito carrito = carritoRepository.findByUsuarioAndProductoId(usuario, productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));

        // Verificar stock disponible
        ProductoDTO producto = productoService.getProductoById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));

        if (producto.getStock() == null || producto.getStock() < nuevaCantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + 
                (producto.getStock() != null ? producto.getStock() : 0));
        }

        carrito.actualizarCantidad(nuevaCantidad);
        carrito = carritoRepository.save(carrito);

        log.info("✅ Cantidad actualizada correctamente");
        return convertirACarritoItemDTO(carrito, producto);
    }

    /**
     * Eliminar un producto del carrito
     */
    @Transactional
    public void eliminarProducto(Usuario usuario, String productoId) {
        log.info("🗑️ Eliminando producto {} del carrito de {}", productoId, usuario.getUsername());

        if (!carritoRepository.existsByUsuarioAndProductoId(usuario, productoId)) {
            throw new RuntimeException("Producto no encontrado en el carrito");
        }

        carritoRepository.deleteByUsuarioAndProductoId(usuario, productoId);
        log.info("✅ Producto eliminado del carrito");
    }

    /**
     * Vaciar todo el carrito
     */
    @Transactional
    public void limpiarCarrito(Usuario usuario) {
        log.info("🧹 Limpiando carrito completo de {}", usuario.getUsername());
        carritoRepository.deleteByUsuario(usuario);
        log.info("✅ Carrito limpiado");
    }

    /**
     * Obtener el total del carrito
     */
    @Transactional(readOnly = true)
    public Double calcularTotal(Usuario usuario) {
        List<Carrito> items = carritoRepository.findByUsuarioOrderByFechaAgregadoDesc(usuario);

        return items.stream()
                .mapToDouble(item -> {
                    Optional<ProductoDTO> producto = productoService.getProductoById(item.getProductoId());
                    return producto.map(p -> p.getPrecio() * item.getCantidad()).orElse(0.0);
                })
                .sum();
    }

    /**
     * Obtener cantidad total de items en el carrito
     */
    @Transactional(readOnly = true)
    public Integer obtenerCantidadItems(Usuario usuario) {
        Integer cantidad = carritoRepository.sumCantidadByUsuario(usuario);
        return cantidad != null ? cantidad : 0;
    }

    /**
     * Verificar disponibilidad de stock para todos los productos del carrito
     */
    @Transactional(readOnly = true)
    public List<String> verificarStock(Usuario usuario) {
        log.info("🔍 Verificando stock para carrito de {}", usuario.getUsername());

        List<Carrito> items = carritoRepository.findByUsuarioOrderByFechaAgregadoDesc(usuario);
        List<String> errores = new ArrayList<>();

        for (Carrito item : items) {
            Optional<ProductoDTO> productoOpt = productoService.getProductoById(item.getProductoId());
            
            if (productoOpt.isEmpty()) {
                errores.add("Producto no encontrado: " + item.getProductoId());
                continue;
            }

            ProductoDTO producto = productoOpt.get();
            if (producto.getStock() == null || producto.getStock() < item.getCantidad()) {
                errores.add(String.format("%s - Stock insuficiente. Solicitado: %d, Disponible: %d",
                        producto.getNombre(),
                        item.getCantidad(),
                        producto.getStock() != null ? producto.getStock() : 0));
            }
        }

        if (errores.isEmpty()) {
            log.info("✅ Todo el stock está disponible");
        } else {
            log.warn("⚠️ Hay {} productos con problemas de stock", errores.size());
        }

        return errores;
    }

    /**
     * Verificar compatibilidad de productos en el carrito
     */
    @Transactional(readOnly = true)
    public List<String> verificarCompatibilidad(Usuario usuario) {
        log.info("🔍 Verificando compatibilidad de productos en carrito de {}", usuario.getUsername());

        List<Carrito> items = carritoRepository.findByUsuarioOrderByFechaAgregadoDesc(usuario);
        List<String> advertencias = new ArrayList<>();

        // Verificar incompatibilidades entre productos
        for (int i = 0; i < items.size(); i++) {
            for (int j = i + 1; j < items.size(); j++) {
                String producto1 = items.get(i).getProductoId();
                String producto2 = items.get(j).getProductoId();

                // Verificar si son incompatibles usando el servicio de productos
                boolean compatibles = productoService.sonCompatibles(producto1, producto2);
                
                if (!compatibles) {
                    Optional<ProductoDTO> p1 = productoService.getProductoById(producto1);
                    Optional<ProductoDTO> p2 = productoService.getProductoById(producto2);
                    
                    String nombre1 = p1.map(ProductoDTO::getNombre).orElse(producto1);
                    String nombre2 = p2.map(ProductoDTO::getNombre).orElse(producto2);
                    
                    advertencias.add(String.format("⚠️ %s podría no ser compatible con %s", 
                            nombre1, nombre2));
                }
            }
        }

        if (advertencias.isEmpty()) {
            log.info("✅ Todos los productos son compatibles");
        } else {
            log.warn("⚠️ Se encontraron {} advertencias de compatibilidad", advertencias.size());
        }

        return advertencias;
    }

    /**
     * Obtener resumen del carrito (para dashboard)
     */
    @Transactional(readOnly = true)
    public CarritoDTO obtenerResumen(Usuario usuario) {
        CarritoDTO carrito = obtenerCarrito(usuario);
        
        // Limitar a los primeros 3 items para el resumen
        if (carrito.getItems() != null && carrito.getItems().size() > 3) {
            List<CarritoItemDTO> top3 = carrito.getItems().stream()
                    .limit(3)
                    .collect(Collectors.toList());
            carrito.setItems(top3);
        }
        
        return carrito;
    }

    // ============================================
    // MÉTODOS PRIVADOS DE CONVERSIÓN
    // ============================================

    private CarritoItemDTO convertirACarritoItemDTOConProducto(Carrito carrito) {
        Optional<ProductoDTO> productoOpt = productoService.getProductoById(carrito.getProductoId());
        
        if (productoOpt.isEmpty()) {
            // Producto no encontrado - crear DTO con información limitada
            return CarritoItemDTO.builder()
                    .id(carrito.getId())
                    .productoId(carrito.getProductoId())
                    .productoNombre("Producto no disponible")
                    .cantidad(carrito.getCantidad())
                    .fechaAgregado(carrito.getFechaAgregado())
                    .disponible(false)
                    .subtotal(0.0)
                    .build();
        }

        return convertirACarritoItemDTO(carrito, productoOpt.get());
    }

    private CarritoItemDTO convertirACarritoItemDTO(Carrito carrito, ProductoDTO producto) {
        boolean disponible = producto.getStock() != null && producto.getStock() >= carrito.getCantidad();
        double subtotal = producto.getPrecio() * carrito.getCantidad();

        return CarritoItemDTO.builder()
                .id(carrito.getId())
                .productoId(producto.getId())
                .productoNombre(producto.getNombre())
                .productoMarca(producto.getMarca())
                .productoCategoria(producto.getCategoria())
                .precio(producto.getPrecio())
                .cantidad(carrito.getCantidad())
                .stock(producto.getStock())
                .subtotal(subtotal)
                .fechaAgregado(carrito.getFechaAgregado())
                .disponible(disponible)
                .build();
    }
}