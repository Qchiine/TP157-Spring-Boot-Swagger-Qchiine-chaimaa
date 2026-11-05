package com.example.studentmanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Metadonnees de la documentation OpenAPI.
 * <p>
 * springdoc genere automatiquement la description des endpoints ; cette classe
 * ne fait qu'ajouter l'entete du document : titre, version, contact et serveurs.
 */
@Configuration
public class SwaggerConfig {

    /**
     * @return la description OpenAPI de l'API etudiants
     */
    @Bean
    public OpenAPI documentationEtudiants() {
        Contact contact = new Contact()
                .name("Qchiine Chaimaa")
                .email("chaimaa.qchiine@example.com");

        Info information = new Info()
                .title("API de gestion des etudiants")
                .version("1.0.0")
                .description("TP 6 : API REST Spring Boot documentee avec springdoc-openapi")
                .contact(contact)
                .license(new License().name("Usage pedagogique"));

        Server local = new Server()
                .url("http://localhost:8080")
                .description("Environnement local");

        return new OpenAPI()
                .info(information)
                .servers(List.of(local));
    }
}
