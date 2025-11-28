package com.semanticshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de SemanticShop
 * Sistema de comercio electrónico con razonamiento semántico
 */
@SpringBootApplication
public class SemanticShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(SemanticShopApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  SemanticShop - Sistema Iniciado");
        System.out.println("  Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println("  H2 Console: http://localhost:8080/h2-console");
        System.out.println("===========================================");
    }
}
