package fr.pa.simulator.shared;

/**
 * Numéro SIREN : 9 chiffres identifiant une entreprise.
 *
 * <p>Value object : validation de format uniquement (donnée fictive acceptée en test).</p>
 */
public record Siren(String value) {

    public Siren {
        if (value == null || !value.matches("\\d{9}")) {
            throw new IllegalArgumentException("SIREN invalide (9 chiffres attendus) : " + value);
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
