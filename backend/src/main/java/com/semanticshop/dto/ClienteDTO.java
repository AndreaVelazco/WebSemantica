package com.semanticshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data                    // ← IMPORTANTE
@Builder                 // ← IMPORTANTE
@NoArgsConstructor       // ← IMPORTANTE
@AllArgsConstructor      // ← IMPORTANTE
public class ClienteDTO {
    private String id;
    private String nombre;
    private String tipo;
    private String marcaPreferida;
    private List<String> preferencias;
    private int numeroPedidos;
    private Set<String> productosRecomendados;
}