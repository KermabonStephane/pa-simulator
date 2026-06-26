package fr.pa.simulator.entreprise;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Accès persistance des entreprises (package-private : interne au module).
 */
interface EntrepriseRepository extends JpaRepository<Entreprise, UUID> {

    Optional<Entreprise> findByApiKeyHash(String apiKeyHash);

    Optional<Entreprise> findBySiret(String siret);

    boolean existsBySiret(String siret);
}
