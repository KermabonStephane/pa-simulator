package fr.pa.simulator.shared;

/**
 * Noms d'attributs de requête partagés entre la couche sécurité (qui les pose) et les contrôleurs
 * (qui les lisent). Évite un couplage direct entre modules.
 */
public final class RequestAttributes {

    /** Entreprise authentifiée pour la requête courante (posée par le filtre de clé API). */
    public static final String TENANT = "pa.tenant";

    private RequestAttributes() {
    }
}
