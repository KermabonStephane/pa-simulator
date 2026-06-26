/**
 * Module {@code security} : authentification des requêtes par clé API ({@code X-API-Key}).
 * S'appuie sur le module {@code entreprise} pour résoudre la clé.
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Security",
        allowedDependencies = {"entreprise", "shared"}
)
package fr.pa.simulator.security;
