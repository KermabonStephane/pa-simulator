package fr.pa.simulator.entreprise;

/**
 * Levée lorsqu'une entreprise existe déjà pour un SIRET donné.
 */
public class DuplicateSiretException extends RuntimeException {

    public DuplicateSiretException(String siret) {
        super("Une entreprise existe déjà pour le SIRET " + siret);
    }
}
