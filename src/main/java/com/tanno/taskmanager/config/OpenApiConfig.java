package com.tanno.taskmanager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI taskManagerOpenAPI() {

        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .description("REST API para gerenciamento de projetos e tarefas.")
                        .version("1.0.0"))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )
                .tags(List.of(
                        new Tag().name("Authentication").description("Autenticação e geração de token JWT"),
                        new Tag().name("Users").description("Gerenciamento de usuários."),
                        new Tag().name("Projects").description("Gerenciamento de projetos."),
                        new Tag().name("Tasks").description("Gerenciamento de tarefas.")
                ));
    }
}