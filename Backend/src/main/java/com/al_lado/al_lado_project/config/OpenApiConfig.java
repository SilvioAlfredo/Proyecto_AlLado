package com.al_lado.al_lado_project.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Documentación OpenAPI: título/versión y esquema Bearer JWT para que el
 * botón "Authorize" de Swagger UI emita el header Authorization.
 */
@Configuration
public class OpenApiConfig {

    public static final String ESQUEMA_JWT = "bearer-jwt";

    @Bean
    public OpenAPI alLadoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AlLado API")
                        .version("1.0")
                        .description("Plataforma barrial de oportunidades laborales: "
                                + "auth JWT, ofertas, postulaciones, perfiles, comunidad y administración. "
                                + "Roles: ADMIN, EMPLEADOR, TRABAJADOR. "
                                + "Las operaciones mutantes exigen ser titular del recurso o admin."))
                .components(new Components().addSecuritySchemes(ESQUEMA_JWT,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_JWT));
    }
}
