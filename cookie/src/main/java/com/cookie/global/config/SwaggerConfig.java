package com.cookie.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${app.client.url}")
    private String appUrl;

    @Bean
    public OpenAPI openAPI() {

        String defaultUrl = appUrl.contains("local") ? "http://localhost:8080" : "https://www.cookiekie.com";

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("BearerAuth");

        return new OpenAPI()
                .addServersItem(new Server().url(defaultUrl))
                .info(new Info()
                        .title("Cookie Project API 문서 🍿🍺")
                        .description("Cookie Project 개발 사용 되는 API 문서 ")
                        .version("v1.0"))
                .addSecurityItem(securityRequirement)
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", securityScheme)
                        .addResponses("400", new ApiResponse()
                                .description("잘못된 요청")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiError")))))
                        .addResponses("500", new ApiResponse()
                                .description("서버 오류")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiError"))))));

    }

}
