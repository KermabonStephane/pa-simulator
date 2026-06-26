package fr.pa.simulator.annuaire;

import fr.pa.simulator.shared.Siret;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * API publique du module {@code annuaire} : enregistrement et résolution des rattachements PA.
 */
@Service
public class AnnuaireService {

    private final AnnuaireRepository repository;

    public AnnuaireService(AnnuaireRepository repository) {
        this.repository = repository;
    }

    /** Rattache un SIRET + code routage à une PA. Idempotent sur le couple (SIRET, code routage). */
    @Transactional
    public AnnuaireResolution register(String siret, String codeRoutage, String paId) {
        String validSiret = new Siret(siret).value();
        return repository.findBySiretAndCodeRoutage(validSiret, codeRoutage)
                .map(AnnuaireEntry::toResolution)
                .orElseGet(() -> {
                    AnnuaireEntry entry = new AnnuaireEntry(
                            UUID.randomUUID(), validSiret, codeRoutage, paId, Instant.now());
                    return repository.save(entry).toResolution();
                });
    }

    /** Résout la PA destinataire pour un couple (SIRET, code routage). */
    @Transactional(readOnly = true)
    public Optional<AnnuaireResolution> resolve(String siret, String codeRoutage) {
        return repository.findBySiretAndCodeRoutage(new Siret(siret).value(), codeRoutage)
                .map(AnnuaireEntry::toResolution);
    }
}
