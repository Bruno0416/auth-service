package com.mariluz.auth_service.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Auth Service API",
        description = "Documentación de microservicio de autenticación",
        version = "1.0",
        contact = @Contact(name = "Bruno", email = "bru.valladares@duocuc.cl")
    )
)
public class SwaggerConfig {}
