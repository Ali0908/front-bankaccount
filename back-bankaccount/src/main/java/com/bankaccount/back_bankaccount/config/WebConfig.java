package com.bankaccount.back_bankaccount.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.bankaccount.back_bankaccount.config.properties.CorsProperties;

import lombok.RequiredArgsConstructor;

/**
 * Web configuration for CORS, interceptors, and other web-related settings
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    /**
     * Configure CORS settings to allow cross-origin requests from frontend
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String allowedOrigins = corsProperties.getAllowedOrigins();
        String allowedMethods = corsProperties.getAllowedMethods();
        
        // Require origins to be provided via configuration (application-secret.properties or environment)
        if (allowedOrigins == null || allowedOrigins.isBlank()) {
            throw new IllegalStateException("CORS allowed origins not configured");
        }
        if (allowedMethods == null || allowedMethods.isEmpty()) {
            allowedMethods = "GET,POST,PUT,DELETE,OPTIONS";
        }
        
        registry.addMapping("/**")
            .allowedOrigins(allowedOrigins.split(","))
            .allowedMethods(allowedMethods.split(","))
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(corsProperties.getMaxAge());
    }
}