package fr.pa.simulator.entreprise.web;

import fr.pa.simulator.entreprise.EntrepriseView;

import java.time.Instant;
import java.util.UUID;

/**
 * Représentation REST d'une entreprise (sans secret).
 */
public record EntrepriseResponse(
        UUID id,
        String siren,
        String siret,
        String raisonSociale,
        String tvaIntra,
        String paRattachement,
        Instant createdAt
) {
    static EntrepriseResponse from(EntrepriseView v) {
        return new EntrepriseResponse(v.id(), v.siren(), v.siret(), v.raisonSociale(),
                v.tvaIntra(), v.paRattachement(), v.createdAt());
    }
}
