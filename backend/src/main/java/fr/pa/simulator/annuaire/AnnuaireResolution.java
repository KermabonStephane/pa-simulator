package fr.pa.simulator.annuaire;

/**
 * Résultat d'une résolution d'annuaire : la PA destinataire pour un couple (SIRET, code routage).
 * API publique du module.
 */
public record AnnuaireResolution(String siret, String codeRoutage, String paId) {
}
