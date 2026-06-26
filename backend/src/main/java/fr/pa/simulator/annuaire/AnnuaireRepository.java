package fr.pa.simulator.annuaire;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Accès persistance de l'annuaire (interne au module).
 */
interface AnnuaireRepository extends JpaRepository<AnnuaireEntry, UUID> {

    Optional<AnnuaireEntry> findBySiretAndCodeRoutage(String siret, String codeRoutage);

    boolean existsBySiretAndCodeRoutage(String siret, String codeRoutage);
}
