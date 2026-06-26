package fr.pa.simulator.entreprise;

import fr.pa.simulator.shared.Siren;
import fr.pa.simulator.shared.Siret;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * API publique du module {@code entreprise} : enregistrement et authentification par clé API.
 */
@Service
public class EntrepriseService {

    private final EntrepriseRepository repository;

    public EntrepriseService(EntrepriseRepository repository) {
        this.repository = repository;
    }

    /**
     * Enregistre une nouvelle entreprise et lui attribue une clé API.
     *
     * @return la vue de l'entreprise et sa clé API en clair (à transmettre une seule fois)
     * @throws DuplicateSiretException si le SIRET est déjà connu
     */
    @Transactional
    public RegisteredEntreprise register(RegisterEntrepriseCommand command) {
        Siret siret = new Siret(command.siret());
        Siren siren = new Siren(command.siren());
        if (!siret.siren().equals(siren)) {
            throw new IllegalArgumentException("Le SIREN ne correspond pas aux 9 premiers chiffres du SIRET");
        }
        if (repository.existsBySiret(siret.value())) {
            throw new DuplicateSiretException(siret.value());
        }

        String apiKey = ApiKeys.generate();
        Entreprise entreprise = new Entreprise(
                UUID.randomUUID(),
                siren.value(),
                siret.value(),
                command.raisonSociale(),
                command.tvaIntra(),
                null,
                ApiKeys.hash(apiKey),
                Instant.now()
        );
        Entreprise saved = repository.save(entreprise);
        return new RegisteredEntreprise(saved.toView(), apiKey);
    }

    /**
     * Authentifie une entreprise à partir de sa clé API.
     *
     * @return la vue de l'entreprise si la clé est valide, sinon {@link Optional#empty()}
     */
    @Transactional(readOnly = true)
    public Optional<EntrepriseView> authenticate(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            return Optional.empty();
        }
        return repository.findByApiKeyHash(ApiKeys.hash(apiKey)).map(Entreprise::toView);
    }
}
