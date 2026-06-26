package fr.pa.simulator;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Vérifie l'intégrité de l'architecture Spring Modulith : pas de dépendances illégales ni de cycles
 * entre modules.
 */
class ModularityTests {

    private final ApplicationModules modules = ApplicationModules.of(PaSimulatorApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }
}
