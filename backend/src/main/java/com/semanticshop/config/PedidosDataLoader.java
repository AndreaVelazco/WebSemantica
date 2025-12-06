package com.semanticshop.config;

import com.semanticshop.model.*;
import com.semanticshop.repository.CarritoRepository;
import com.semanticshop.repository.PedidoRepository;
import com.semanticshop.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Carga datos de prueba para el sistema de pedidos
 * Se ejecuta automáticamente al iniciar la aplicación
 */
@Component
@Order(2) // Se ejecuta después de otros CommandLineRunner
@RequiredArgsConstructor
@Slf4j
public class PedidosDataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;

    @Override
    public void run(String... args) throws Exception {
        
        // Solo cargar si no hay pedidos
        if (pedidoRepository.count() > 0) {
            log.info("📦 Datos de pedidos ya existen. Saltando carga automática.");
            return;
        }

        log.info("🔄 Cargando datos de prueba para sistema de pedidos...");

        try {
            cargarDatosPrueba();
            log.info("✅ Datos de pedidos cargados exitosamente");
        } catch (Exception e) {
            log.warn("⚠️ No se pudieron cargar datos de prueba de pedidos: {}", e.getMessage());
            // No lanzar excepción para que la app inicie igual
        }
    }

    private void cargarDatosPrueba() {
        // Buscar primer usuario disponible
        Usuario usuario = usuarioRepository.findAll().stream()
                .findFirst()
                .orElse(null);

        if (usuario == null) {
            log.warn("⚠️ No hay usuarios en el sistema. Crea un usuario primero para cargar pedidos de prueba.");
            return;
        }

        log.info("📝 Creando pedidos de prueba para usuario: {}", usuario.getUsername());

        // ============================================
        // 1. CARRITO DE PRUEBA
        // ============================================
        crearCarritoPrueba(usuario);

        // ============================================
        // 2. PEDIDOS DE PRUEBA
        // ============================================
        crearPedidoEntregado(usuario);
        crearPedidoEnviado(usuario);
        crearPedidoProcesando(usuario);
        crearPedidoPendiente(usuario);

        log.info("✅ Creados 4 pedidos de prueba y carrito con 3 items");
    }

    private void crearCarritoPrueba(Usuario usuario) {
        // Limpiar carrito existente
        carritoRepository.deleteByUsuario(usuario);

        // Agregar items al carrito
        Carrito item1 = Carrito.builder()
                .usuario(usuario)
                .productoId("iPhone15Pro")
                .cantidad(1)
                .fechaAgregado(LocalDateTime.now().minusDays(2))
                .build();

        Carrito item2 = Carrito.builder()
                .usuario(usuario)
                .productoId("AirPodsPro")
                .cantidad(2)
                .fechaAgregado(LocalDateTime.now().minusDays(1))
                .build();

        Carrito item3 = Carrito.builder()
                .usuario(usuario)
                .productoId("AppleWatch9")
                .cantidad(1)
                .fechaAgregado(LocalDateTime.now())
                .build();

        carritoRepository.save(item1);
        carritoRepository.save(item2);
        carritoRepository.save(item3);

        log.info("🛒 Carrito creado con 3 items");
    }

    private void crearPedidoEntregado(Usuario usuario) {
        Pedido pedido = Pedido.builder()
                .usuario(usuario)
                .fechaPedido(LocalDateTime.now().minusDays(15))
                .estado(EstadoPedido.ENTREGADO)
                .total(0.0) // Se calculará automáticamente
                .direccionEnvio(usuario.getDireccion() != null ? usuario.getDireccion() : "Dirección de prueba")
                .notas("Pedido de prueba - Entrega rápida")
                .build();

        // Agregar detalles
        DetallePedido detalle1 = DetallePedido.builder()
                .pedido(pedido)
                .productoId("iPhone15Pro")
                .productoNombre("iPhone 15 Pro 128GB")
                .productoMarca("Apple")
                .productoCategoria("Smartphone")
                .cantidad(1)
                .precioUnitario(999.99)
                .subtotal(999.99)
                .build();

        DetallePedido detalle2 = DetallePedido.builder()
                .pedido(pedido)
                .productoId("FundaiPhone15")
                .productoNombre("Funda iPhone 15 Pro")
                .productoMarca("Apple")
                .productoCategoria("Accesorio")
                .cantidad(2)
                .precioUnitario(29.99)
                .subtotal(59.98)
                .build();

        DetallePedido detalle3 = DetallePedido.builder()
                .pedido(pedido)
                .productoId("CargadorUSBC")
                .productoNombre("Cargador USB-C 20W")
                .productoMarca("Apple")
                .productoCategoria("Accesorio")
                .cantidad(1)
                .precioUnitario(39.99)
                .subtotal(39.99)
                .build();

        pedido.agregarDetalle(detalle1);
        pedido.agregarDetalle(detalle2);
        pedido.agregarDetalle(detalle3);
        pedido.calcularTotal();

        pedidoRepository.save(pedido);
        log.info("📦 Pedido ENTREGADO creado (${}) con 3 items", pedido.getTotal());
    }

    private void crearPedidoEnviado(Usuario usuario) {
        Pedido pedido = Pedido.builder()
                .usuario(usuario)
                .fechaPedido(LocalDateTime.now().minusDays(3))
                .estado(EstadoPedido.ENVIADO)
                .total(0.0)
                .direccionEnvio(usuario.getDireccion() != null ? usuario.getDireccion() : "Dirección de prueba")
                .notas("Pedido de prueba - Envío express")
                .build();

        DetallePedido detalle1 = DetallePedido.builder()
                .pedido(pedido)
                .productoId("MacBookAirM2")
                .productoNombre("MacBook Air M2 256GB")
                .productoMarca("Apple")
                .productoCategoria("Laptop")
                .cantidad(1)
                .precioUnitario(1199.99)
                .subtotal(1199.99)
                .build();

        DetallePedido detalle2 = DetallePedido.builder()
                .pedido(pedido)
                .productoId("AirPodsPro")
                .productoNombre("AirPods Pro 2da Gen")
                .productoMarca("Apple")
                .productoCategoria("Accesorio")
                .cantidad(2)
                .precioUnitario(249.99)
                .subtotal(499.98)
                .build();

        pedido.agregarDetalle(detalle1);
        pedido.agregarDetalle(detalle2);
        pedido.calcularTotal();

        pedidoRepository.save(pedido);
        log.info("📦 Pedido ENVIADO creado (${}) con 2 items", pedido.getTotal());
    }

    private void crearPedidoProcesando(Usuario usuario) {
        Pedido pedido = Pedido.builder()
                .usuario(usuario)
                .fechaPedido(LocalDateTime.now().minusDays(1))
                .estado(EstadoPedido.PROCESANDO)
                .total(0.0)
                .direccionEnvio(usuario.getDireccion() != null ? usuario.getDireccion() : "Dirección de prueba")
                .build();

        DetallePedido detalle1 = DetallePedido.builder()
                .pedido(pedido)
                .productoId("AppleWatch9")
                .productoNombre("Apple Watch Series 9")
                .productoMarca("Apple")
                .productoCategoria("Smartwatch")
                .cantidad(1)
                .precioUnitario(399.99)
                .subtotal(399.99)
                .build();

        DetallePedido detalle2 = DetallePedido.builder()
                .pedido(pedido)
                .productoId("CorreaAppleWatch")
                .productoNombre("Correa Apple Watch")
                .productoMarca("Apple")
                .productoCategoria("Accesorio")
                .cantidad(1)
                .precioUnitario(49.99)
                .subtotal(49.99)
                .build();

        pedido.agregarDetalle(detalle1);
        pedido.agregarDetalle(detalle2);
        pedido.calcularTotal();

        pedidoRepository.save(pedido);
        log.info("📦 Pedido PROCESANDO creado (${}) con 2 items", pedido.getTotal());
    }

    private void crearPedidoPendiente(Usuario usuario) {
        Pedido pedido = Pedido.builder()
                .usuario(usuario)
                .fechaPedido(LocalDateTime.now().minusHours(2))
                .estado(EstadoPedido.PENDIENTE)
                .total(0.0)
                .direccionEnvio(usuario.getDireccion() != null ? usuario.getDireccion() : "Dirección de prueba")
                .notas("Primera compra - Cliente nuevo")
                .build();

        DetallePedido detalle1 = DetallePedido.builder()
                .pedido(pedido)
                .productoId("iPadAir")
                .productoNombre("iPad Air 256GB")
                .productoMarca("Apple")
                .productoCategoria("Tablet")
                .cantidad(1)
                .precioUnitario(799.99)
                .subtotal(799.99)
                .build();

        DetallePedido detalle2 = DetallePedido.builder()
                .pedido(pedido)
                .productoId("ApplePencil")
                .productoNombre("Apple Pencil 2da Gen")
                .productoMarca("Apple")
                .productoCategoria("Accesorio")
                .cantidad(1)
                .precioUnitario(129.99)
                .subtotal(129.99)
                .build();

        pedido.agregarDetalle(detalle1);
        pedido.agregarDetalle(detalle2);
        pedido.calcularTotal();

        pedidoRepository.save(pedido);
        log.info("📦 Pedido PENDIENTE creado (${}) con 2 items", pedido.getTotal());
    }
}