package com.tanno.taskmanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI taskManagerOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .description("REST API para gerenciamento de projetos e tarefas.")
                        .version("1.0.0"));
    }
}