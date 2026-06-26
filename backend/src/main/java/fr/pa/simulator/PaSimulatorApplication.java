package fr.pa.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;

/**
 * Point d'entrée du simulateur de Plateforme Agréée.
 *
 * <p>Application <strong>de test</strong> : elle imite les flux et formats d'une PA de facturation
 * électronique, sans aucune valeur fiscale.</p>
 */
@Modulithic(systemName = "PA Simulator")
@SpringBootApplication
public class PaSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaSimulatorApplication.class, args);
    }
}
