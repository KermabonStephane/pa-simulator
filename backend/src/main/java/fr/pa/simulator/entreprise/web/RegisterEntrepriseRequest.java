package fr.pa.simulator.entreprise.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Corps de la requête d'enregistrement d'une entreprise.
 */
public record RegisterEntrepriseRequest(
        @NotBlank @Pattern(regexp = "\\d{9}", message = "SIREN : 9 chiffres attendus")
        String siren,

        @NotBlank @Pattern(regexp = "\\d{14}", message = "SIRET : 14 chiffres attendus")
        String siret,

        @NotBlank
        String raisonSociale,

        String tvaIntra
) {
}
