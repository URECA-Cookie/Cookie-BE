package com.cookie.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Value("${app.client.url}")
    private String clientUrl;

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins(clientUrl)
                .allowedMethods("*")
                .allowCredentials(true)
                .allowedHeaders("*")
                .exposedHeaders("Set-Cookie");
    }
}
