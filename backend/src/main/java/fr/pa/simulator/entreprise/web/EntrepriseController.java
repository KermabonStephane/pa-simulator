package fr.pa.simulator.entreprise.web;

import fr.pa.simulator.entreprise.EntrepriseService;
import fr.pa.simulator.entreprise.EntrepriseView;
import fr.pa.simulator.entreprise.RegisterEntrepriseCommand;
import fr.pa.simulator.shared.RequestAttributes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/entreprises")
@Tag(name = "Entreprises", description = "Comptes des entreprises et authentification par clé API")
public class EntrepriseController {

    private final EntrepriseService service;

    public EntrepriseController(EntrepriseService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Enregistrer une entreprise",
            description = "Endpoint public. Renvoie une clé API affichée une seule fois.")
    public RegisteredEntrepriseResponse register(@Valid @RequestBody RegisterEntrepriseRequest request) {
        var command = new RegisterEntrepriseCommand(
                request.siren(), request.siret(), request.raisonSociale(), request.tvaIntra());
        return RegisteredEntrepriseResponse.from(service.register(command));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "ApiKey")
    @Operation(summary = "Entreprise courante",
            description = "Renvoie l'entreprise authentifiée via l'en-tête X-API-Key.")
    public EntrepriseResponse me(HttpServletRequest request) {
        var tenant = (EntrepriseView) request.getAttribute(RequestAttributes.TENANT);
        return EntrepriseResponse.from(tenant);
    }
}
