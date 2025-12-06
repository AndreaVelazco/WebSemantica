package com.semanticshop.dto;

import com.semanticshop.model.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para representar un pedido completo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private LocalDateTime fechaPedido;
    private EstadoPedido estado;
    private String estadoDisplay;
    private Double total;
    private String direccionEnvio;
    private String notas;
    private LocalDateTime fechaActualizacion;
    private List<DetallePedidoDTO> detalles;
    private Integer cantidadTotal;
}