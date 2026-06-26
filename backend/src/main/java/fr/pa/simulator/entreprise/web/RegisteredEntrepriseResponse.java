package fr.pa.simulator.entreprise.web;

import fr.pa.simulator.entreprise.RegisteredEntreprise;

/**
 * Réponse de création : l'entreprise et sa clé API en clair (affichée une seule fois).
 */
public record RegisteredEntrepriseResponse(
        EntrepriseResponse entreprise,
        String apiKey,
        String message
) {
    static RegisteredEntrepriseResponse from(RegisteredEntreprise r) {
        return new RegisteredEntrepriseResponse(
                EntrepriseResponse.from(r.entreprise()),
                r.apiKey(),
                "Conservez cette clé API : elle ne sera plus affichée. Utilisez l'en-tête X-API-Key."
        );
    }
}
