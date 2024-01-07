package com.neupinion.neupinion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Value("${cors.mapping}")
    private String mapping;

    @Value("${cors.origins}")
    private String origins;

    @Value("${cors.methods}")
    private String methods;

    @Value("${cors.headers}")
    private String headers;

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping(mapping)
            .allowedOrigins(origins)
            .allowedMethods(methods)
            .allowedHeaders(headers);
    }
}
