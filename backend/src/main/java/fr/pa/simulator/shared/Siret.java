package fr.pa.simulator.shared;

/**
 * Numéro SIRET : 14 chiffres (SIREN sur 9 + NIC sur 5).
 *
 * <p>Value object : validation de format uniquement (longueur + chiffres). La validité de la clé de
 * Luhn n'est pas contrôlée — on accepte des données fictives en environnement de test.</p>
 */
public record Siret(String value) {

    public Siret {
        if (value == null || !value.matches("\\d{14}")) {
            throw new IllegalArgumentException("SIRET invalide (14 chiffres attendus) : " + value);
        }
    }

    /** Le SIREN (9 premiers chiffres) extrait du SIRET. */
    public Siren siren() {
        return new Siren(value.substring(0, 9));
    }

    @Override
    public String toString() {
        return value;
    }
}
