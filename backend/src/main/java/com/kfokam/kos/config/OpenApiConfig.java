package com.kfokam.kos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Documentation OpenAPI exposée sur /swagger-ui.html
 * et /v3/api-docs (consommée par le front si besoin).
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public OpenAPI kosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API " + applicationName)
                        .description("API REST du projet kos — architecture en couches (controller / service / repository)")
                        .version("v1")
                        .contact(new Contact().name("Équipe kos").email("contact@kos.dev")))
                .servers(List.of(new Server().url("/").description("Serveur courant")));
    }
}
