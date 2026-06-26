package fr.pa.simulator.entreprise;

import java.time.Instant;
import java.util.UUID;

/**
 * Vue exposée d'une entreprise (sans secret). Fait partie de l'API publique du module : utilisable
 * par les autres modules et par la couche web.
 */
public record EntrepriseView(
        UUID id,
        String siren,
        String siret,
        String raisonSociale,
        String tvaIntra,
        String paRattachement,
        Instant createdAt
) {
}
