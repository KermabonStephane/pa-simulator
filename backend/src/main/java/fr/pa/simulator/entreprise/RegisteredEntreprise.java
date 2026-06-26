package fr.pa.simulator.entreprise;

/**
 * Résultat d'un enregistrement : la vue de l'entreprise et sa clé API en clair.
 *
 * <p>La clé API n'est <strong>jamais</strong> persistée en clair (seul son hash l'est) ni renvoyée
 * une seconde fois : elle n'est disponible qu'à la création.</p>
 */
public record RegisteredEntreprise(EntrepriseView entreprise, String apiKey) {
}
