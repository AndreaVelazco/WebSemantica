package com.semanticshop.service;

import com.semanticshop.dto.*;
import com.semanticshop.model.*;
import com.semanticshop.repository.CarritoRepository;
import com.semanticshop.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar pedidos
 */
@Service
@RequiredArgsConstructor
public class PedidoService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;
    private final ProductoService productoService;

    /**
     * Crear un nuevo pedido desde el carrito del usuario
     */
    @Transactional
    public PedidoDTO crearPedido(Usuario usuario, String direccionEnvio, String notas) {
        log.info("📦 Creando pedido para usuario: {}", usuario.getUsername());

        // Obtener items del carrito
        List<Carrito> itemsCarrito = carritoRepository.findByUsuarioOrderByFechaAgregadoDesc(usuario);

        if (itemsCarrito.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // Verificar stock y obtener productos
        List<DetallePedido> detalles = new ArrayList<>();
        double total = 0.0;

        for (Carrito item : itemsCarrito) {
            ProductoDTO producto = productoService.getProductoById(item.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getProductoId()));

            // Verificar stock
            if (producto.getStock() == null || producto.getStock() < item.getCantidad()) {
                throw new RuntimeException(String.format(
                        "Stock insuficiente para %s. Disponible: %d, Solicitado: %d",
                        producto.getNombre(),
                        producto.getStock() != null ? producto.getStock() : 0,
                        item.getCantidad()));
            }

            // Crear detalle del pedido
            DetallePedido detalle = DetallePedido.builder()
                    .productoId(producto.getId())
                    .productoNombre(producto.getNombre())
                    .productoMarca(producto.getMarca())
                    .productoCategoria(producto.getCategoria())
                    .cantidad(item.getCantidad())
                    .precioUnitario(producto.getPrecio())
                    .build();
            
            detalle.calcularSubtotal();
            detalles.add(detalle);
            total += detalle.getSubtotal();
        }

        // Crear el pedido
        Pedido pedido = Pedido.builder()
                .usuario(usuario)
                .fechaPedido(LocalDateTime.now())
                .estado(EstadoPedido.PENDIENTE)
                .total(total)
                .direccionEnvio(direccionEnvio != null ? direccionEnvio : usuario.getDireccion())
                .notas(notas)
                .build();

        // Agregar detalles al pedido
        for (DetallePedido detalle : detalles) {
            pedido.agregarDetalle(detalle);
        }

        // Guardar pedido
        pedido = pedidoRepository.save(pedido);
        
        // Limpiar el carrito
        carritoRepository.deleteByUsuario(usuario);

        log.info("✅ Pedido #{} creado exitosamente. Total: ${}", pedido.getId(), pedido.getTotal());

        return convertirAPedidoDTO(pedido);
    }

    /**
     * Obtener todos los pedidos de un usuario
     */
    @Transactional(readOnly = true)
    public List<PedidoDTO> obtenerMisPedidos(Usuario usuario) {
        log.info("📋 Obteniendo pedidos del usuario: {}", usuario.getUsername());

        List<Pedido> pedidos = pedidoRepository.findByUsuarioOrderByFechaPedidoDesc(usuario);

        return pedidos.stream()
                .map(this::convertirAPedidoDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener detalle de un pedido específico
     */
    @Transactional(readOnly = true)
    public PedidoDTO obtenerPedido(Usuario usuario, Long pedidoId) {
        log.info("📄 Obteniendo detalle del pedido #{} para usuario {}", pedidoId, usuario.getUsername());

        Pedido pedido = pedidoRepository.findByIdAndUsuario(pedidoId, usuario)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado o no tienes permiso para verlo"));

        return convertirAPedidoDTO(pedido);
    }

    /**
     * Cancelar un pedido (solo si está en estado PENDIENTE o PROCESANDO)
     */
    @Transactional
    public PedidoDTO cancelarPedido(Usuario usuario, Long pedidoId) {
        log.info("❌ Cancelando pedido #{} de usuario {}", pedidoId, usuario.getUsername());

        Pedido pedido = pedidoRepository.findByIdAndUsuario(pedidoId, usuario)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (!pedido.puedeCancelarse()) {
            throw new RuntimeException("El pedido no puede ser cancelado en su estado actual: " + pedido.getEstado());
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedido = pedidoRepository.save(pedido);

        log.info("✅ Pedido #{} cancelado", pedidoId);

        return convertirAPedidoDTO(pedido);
    }

    /**
     * Actualizar el estado de un pedido (solo admin)
     */
    @Transactional
    public PedidoDTO actualizarEstado(Long pedidoId, String nuevoEstado) {
        log.info("🔄 Actualizando estado del pedido #{} a {}", pedidoId, nuevoEstado);

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // Validar que el estado es válido
        EstadoPedido estado;
        try {
            estado = EstadoPedido.valueOf(nuevoEstado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado inválido: " + nuevoEstado);
        }

        // Validar transición de estado
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new RuntimeException("No se puede cambiar el estado de un pedido cancelado");
        }

        if (pedido.getEstado() == EstadoPedido.ENTREGADO && estado != EstadoPedido.ENTREGADO) {
            throw new RuntimeException("No se puede cambiar el estado de un pedido entregado");
        }

        pedido.setEstado(estado);
        pedido = pedidoRepository.save(pedido);

        log.info("✅ Estado actualizado a {}", estado);

        return convertirAPedidoDTO(pedido);
    }

    /**
     * Obtener todos los pedidos (admin)
     */
    @Transactional(readOnly = true)
    public List<PedidoDTO> obtenerTodosPedidos() {
        log.info("📋 Obteniendo todos los pedidos (admin)");

        List<Pedido> pedidos = pedidoRepository.findAll();

        return pedidos.stream()
                .map(this::convertirAPedidoDTO)
                .sorted(Comparator.comparing(PedidoDTO::getFechaPedido).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Obtener pedidos por estado (admin)
     */
    @Transactional(readOnly = true)
    public List<PedidoDTO> obtenerPedidosPorEstado(String estadoStr) {
        log.info("📋 Obteniendo pedidos por estado: {}", estadoStr);

        EstadoPedido estado;
        try {
            estado = EstadoPedido.valueOf(estadoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado inválido: " + estadoStr);
        }

        List<Pedido> pedidos = pedidoRepository.findByEstadoOrderByFechaPedidoDesc(estado);

        return pedidos.stream()
                .map(this::convertirAPedidoDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener estadísticas de pedidos (admin)
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticas() {
        log.info("📊 Calculando estadísticas de pedidos");

        Map<String, Object> estadisticas = new HashMap<>();

        // Cantidad por estado
        Map<String, Long> cantidadPorEstado = new HashMap<>();
        Map<String, Double> ventasPorEstado = new HashMap<>();

        for (EstadoPedido estado : EstadoPedido.values()) {
            long cantidad = pedidoRepository.countByEstado(estado);
            Double ventas = pedidoRepository.sumTotalByEstado(estado);

            cantidadPorEstado.put(estado.name(), cantidad);
            ventasPorEstado.put(estado.name(), ventas != null ? ventas : 0.0);
        }

        estadisticas.put("cantidadPorEstado", cantidadPorEstado);
        estadisticas.put("ventasPorEstado", ventasPorEstado);

        // Totales generales
        long totalPedidos = pedidoRepository.count();
        double totalVentas = ventasPorEstado.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        estadisticas.put("totalPedidos", totalPedidos);
        estadisticas.put("totalVentas", totalVentas);

        // Pedidos en proceso
        long enProceso = cantidadPorEstado.getOrDefault("PENDIENTE", 0L) +
                        cantidadPorEstado.getOrDefault("PROCESANDO", 0L) +
                        cantidadPorEstado.getOrDefault("ENVIADO", 0L);

        estadisticas.put("pedidosEnProceso", enProceso);

        log.info("✅ Estadísticas calculadas: {} pedidos totales, ${} en ventas", totalPedidos, totalVentas);

        return estadisticas;
    }

    /**
     * Obtener estadísticas de un usuario específico
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticasUsuario(Usuario usuario) {
        log.info("📊 Calculando estadísticas para usuario: {}", usuario.getUsername());

        Map<String, Object> estadisticas = new HashMap<>();

        long totalPedidos = pedidoRepository.countByUsuario(usuario);
        Double totalGastado = pedidoRepository.sumTotalByUsuario(usuario);

        estadisticas.put("totalPedidos", totalPedidos);
        estadisticas.put("totalGastado", totalGastado != null ? totalGastado : 0.0);

        // Último pedido
        List<Pedido> pedidos = pedidoRepository.findByUsuarioOrderByFechaPedidoDesc(usuario);
        if (!pedidos.isEmpty()) {
            Pedido ultimoPedido = pedidos.get(0);
            estadisticas.put("ultimoPedidoId", ultimoPedido.getId());
            estadisticas.put("ultimoPedidoFecha", ultimoPedido.getFechaPedido());
            estadisticas.put("ultimoPedidoEstado", ultimoPedido.getEstado().name());
        }

        return estadisticas;
    }

    // ============================================
    // MÉTODOS PRIVADOS DE CONVERSIÓN
    // ============================================

    private PedidoDTO convertirAPedidoDTO(Pedido pedido) {
        List<DetallePedidoDTO> detallesDTO = pedido.getDetalles().stream()
                .map(this::convertirADetallePedidoDTO)
                .collect(Collectors.toList());

        return PedidoDTO.builder()
                .id(pedido.getId())
                .usuarioId(pedido.getUsuario().getId())
                .usuarioNombre(pedido.getUsuario().getUsername())
                .fechaPedido(pedido.getFechaPedido())
                .estado(pedido.getEstado())
                .estadoDisplay(pedido.getEstado().getDisplayName())
                .total(pedido.getTotal())
                .direccionEnvio(pedido.getDireccionEnvio())
                .notas(pedido.getNotas())
                .fechaActualizacion(pedido.getFechaActualizacion())
                .detalles(detallesDTO)
                .cantidadTotal(pedido.getCantidadTotal())
                .build();
    }

    private DetallePedidoDTO convertirADetallePedidoDTO(DetallePedido detalle) {
        return DetallePedidoDTO.builder()
                .id(detalle.getId())
                .productoId(detalle.getProductoId())
                .productoNombre(detalle.getProductoNombre())
                .productoMarca(detalle.getProductoMarca())
                .productoCategoria(detalle.getProductoCategoria())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getSubtotal())
                .build();
    }
}