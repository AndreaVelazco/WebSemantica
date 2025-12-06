package com.semanticshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Configuración de CORS para permitir peticiones desde el frontend
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        
        // Permitir peticiones desde el frontend
        corsConfiguration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:3001",
            "http://127.0.0.1:3000"
        ));
        
        // Permitir todos los métodos HTTP
        corsConfiguration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Permitir todos los headers
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
        
        // Permitir credenciales (cookies, authorization headers)
        corsConfiguration.setAllowCredentials(true);
        
        // Tiempo que el navegador puede cachear la respuesta preflight
        corsConfiguration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        
        return new CorsFilter(source);
    }
}