package com.quizapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * ============================================================
 * CorsConfig - Cross-Origin Resource Sharing Configuration
 * ============================================================
 * CORS is a browser security feature that BLOCKS requests
 * from a different domain/port than the server.
 *
 * Problem:
 *   Frontend runs on: file:// or http://localhost:5500
 *   Backend runs on:  http://localhost:8080
 *   → Browser BLOCKS the fetch() call!
 *
 * Solution:
 *   This config tells the browser: "It's OK, I allow it."
 *
 * In production, replace allowedOrigins("*") with your
 * actual frontend domain for better security.
 * ============================================================
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Allow requests from ANY origin (OK for development).
        // For production: corsConfiguration.setAllowedOrigins(List.of("https://yourdomain.com"));
        corsConfiguration.setAllowedOrigins(List.of("*"));

        // Allow these HTTP methods from the frontend
        corsConfiguration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // Allow these request headers
        corsConfiguration.setAllowedHeaders(Arrays.asList(
                "Origin", "Content-Type", "Accept",
                "Authorization", "X-Requested-With"
        ));

        // Apply this CORS config to ALL API endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", corsConfiguration);

        return new CorsFilter(source);
    }
}
