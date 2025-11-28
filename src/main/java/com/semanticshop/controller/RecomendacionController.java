package com.semanticshop.controller;

import com.semanticshop.dto.ClienteDTO;
import com.semanticshop.dto.ProductoDTO;
import com.semanticshop.dto.RecomendacionDTO;
import com.semanticshop.service.RecomendacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para el sistema de recomendaciones
 */
@RestController
@RequestMapping("/api/recomendaciones")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Recomendaciones", description = "API para recomendaciones inteligentes basadas en razonamiento semántico")
@CrossOrigin(origins = "*")
public class RecomendacionController {

    private final RecomendacionService recomendacionService;

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Obtener recomendaciones para un cliente", 
               description = "Genera recomendaciones personalizadas basadas en preferencias del cliente (inferidas por HermiT)")
    public ResponseEntity<RecomendacionDTO> getRecomendacionesCliente(@PathVariable String clienteId) {
        log.info("GET /api/recomendaciones/cliente/{}", clienteId);
        RecomendacionDTO recomendaciones = recomendacionService.getRecomendacionesParaCliente(clienteId);
        return ResponseEntity.ok(recomendaciones);
    }

    @GetMapping("/clientes")
    @Operation(summary = "Obtener todos los clientes", 
               description = "Retorna la lista de todos los clientes con sus tipos inferidos")
    public ResponseEntity<List<ClienteDTO>> getAllClientes() {
        log.info("GET /api/recomendaciones/clientes");
        List<ClienteDTO> clientes = recomendacionService.getAllClientes();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/clientes/{clienteId}")
    @Operation(summary = "Obtener información de un cliente", 
               description = "Retorna detalles del cliente incluyendo su tipo inferido (ClienteNuevo/ClientePremium)")
    public ResponseEntity<ClienteDTO> getClienteInfo(@PathVariable String clienteId) {
        log.info("GET /api/recomendaciones/clientes/{}", clienteId);
        ClienteDTO cliente = recomendacionService.getClienteInfo(clienteId);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/accesorios/{productoId}")
    @Operation(summary = "Recomendar accesorios compatibles", 
               description = "Recomienda accesorios compatibles con un producto específico")
    public ResponseEntity<List<ProductoDTO>> recomendarAccesorios(@PathVariable String productoId) {
        log.info("GET /api/recomendaciones/accesorios/{}", productoId);
        List<ProductoDTO> accesorios = recomendacionService.recomendarAccesoriosCompatibles(productoId);
        return ResponseEntity.ok(accesorios);
    }

    @GetMapping("/historial/{clienteId}")
    @Operation(summary = "Recomendar basado en historial", 
               description = "Recomienda productos basados en el historial de compras del cliente")
    public ResponseEntity<List<ProductoDTO>> recomendarPorHistorial(@PathVariable String clienteId) {
        log.info("GET /api/recomendaciones/historial/{}", clienteId);
        List<ProductoDTO> recomendaciones = recomendacionService.recomendarBasadoEnHistorial(clienteId);
        return ResponseEntity.ok(recomendaciones);
    }
}
