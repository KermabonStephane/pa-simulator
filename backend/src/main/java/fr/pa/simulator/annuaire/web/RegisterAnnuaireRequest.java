package fr.pa.simulator.annuaire.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Corps de requête pour rattacher un SIRET + code routage à une PA.
 */
public record RegisterAnnuaireRequest(
        @NotBlank @Pattern(regexp = "\\d{14}", message = "SIRET : 14 chiffres attendus")
        String siret,

        @NotBlank
        String codeRoutage,

        @NotBlank
        String paId
) {
}
