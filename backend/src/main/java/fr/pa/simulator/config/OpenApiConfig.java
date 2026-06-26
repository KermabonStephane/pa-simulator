package fr.pa.simulator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration OpenAPI : métadonnées de l'API et schéma de sécurité par clé API.
 */
@Configuration
class OpenApiConfig {

    private static final String API_KEY_SCHEME = "ApiKey";

    @Bean
    OpenAPI paSimulatorOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Simulateur de Plateforme Agréée (PA)")
                        .version("0.0.1")
                        .description("""
                                ⚠️ ENVIRONNEMENT DE TEST — aucune valeur fiscale, données fictives uniquement.

                                API simulant une Plateforme Agréée de facturation électronique.
                                Authentification par clé API via l'en-tête X-API-Key
                                (obtenue à l'enregistrement d'une entreprise)."""))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(API_KEY_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-Key")));
    }
}
