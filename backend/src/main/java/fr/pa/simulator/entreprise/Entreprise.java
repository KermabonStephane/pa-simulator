package fr.pa.simulator.entreprise;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Entreprise utilisatrice du simulateur. Entité persistée (package-private : détail d'implémentation
 * du module, non exposé aux autres modules — voir {@link EntrepriseView}).
 */
@Entity
@Table(name = "entreprise")
class Entreprise {

    @Id
    private UUID id;

    @Column(nullable = false, length = 9)
    private String siren;

    @Column(nullable = false, length = 14, unique = true)
    private String siret;

    @Column(name = "raison_sociale", nullable = false)
    private String raisonSociale;

    @Column(name = "tva_intra")
    private String tvaIntra;

    @Column(name = "pa_rattachement")
    private String paRattachement;

    @Column(name = "api_key_hash", nullable = false, length = 64, unique = true)
    private String apiKeyHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Entreprise() {
        // requis par JPA
    }

    Entreprise(UUID id, String siren, String siret, String raisonSociale, String tvaIntra,
               String paRattachement, String apiKeyHash, Instant createdAt) {
        this.id = id;
        this.siren = siren;
        this.siret = siret;
        this.raisonSociale = raisonSociale;
        this.tvaIntra = tvaIntra;
        this.paRattachement = paRattachement;
        this.apiKeyHash = apiKeyHash;
        this.createdAt = createdAt;
    }

    EntrepriseView toView() {
        return new EntrepriseView(id, siren, siret, raisonSociale, tvaIntra, paRattachement, createdAt);
    }

    UUID getId() {
        return id;
    }

    String getApiKeyHash() {
        return apiKeyHash;
    }
}
