/**
 * Module {@code config} : configuration transverse de l'API (OpenAPI, gestion globale des erreurs).
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Config",
        allowedDependencies = {"entreprise", "shared"}
)
package fr.pa.simulator.config;
