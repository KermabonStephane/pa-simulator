package fr.pa.simulator.entreprise;

/**
 * Commande de création d'une entreprise. API publique du module.
 */
public record RegisterEntrepriseCommand(
        String siren,
        String siret,
        String raisonSociale,
        String tvaIntra
) {
}
