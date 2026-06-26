package fr.pa.simulator.annuaire;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Entrée d'annuaire : associe un SIRET et un code routage à l'identifiant d'une PA destinataire.
 * Entité persistée, interne au module.
 */
@Entity
@Table(name = "annuaire_entry")
class AnnuaireEntry {

    @Id
    private UUID id;

    @Column(nullable = false, length = 14)
    private String siret;

    @Column(name = "code_routage", nullable = false, length = 64)
    private String codeRoutage;

    @Column(name = "pa_id", nullable = false, length = 64)
    private String paId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected AnnuaireEntry() {
        // requis par JPA
    }

    AnnuaireEntry(UUID id, String siret, String codeRoutage, String paId, Instant createdAt) {
        this.id = id;
        this.siret = siret;
        this.codeRoutage = codeRoutage;
        this.paId = paId;
        this.createdAt = createdAt;
    }

    AnnuaireResolution toResolution() {
        return new AnnuaireResolution(siret, codeRoutage, paId);
    }
}
