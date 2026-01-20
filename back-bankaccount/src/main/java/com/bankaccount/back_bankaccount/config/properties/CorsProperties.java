package com.bankaccount.back_bankaccount.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * CORS configuration properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    /**
     * Comma-separated list of allowed origins
     */
    private String allowedOrigins;

    /**
     * Comma-separated list of allowed HTTP methods
     */
    private String allowedMethods;

    /**
     * Max age for CORS preflight requests (in seconds)
     */
    private long maxAge = 3600;
}
