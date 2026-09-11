package com.wms.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WMS Backend API - Gestión de Almacén")
                        .version("1.0.0")
                        .description("API RESTful para el sistema de control de inventario y gestión de almacén (WMS).")
                        .contact(new Contact()
                                .name("Soporte WMS")
                                .email("soporte@wms.com")));
    }
}