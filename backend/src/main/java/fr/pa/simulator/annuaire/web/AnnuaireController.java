package fr.pa.simulator.annuaire.web;

import fr.pa.simulator.annuaire.AnnuaireResolution;
import fr.pa.simulator.annuaire.AnnuaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/annuaire")
@SecurityRequirement(name = "ApiKey")
@Tag(name = "Annuaire", description = "PPF simulé : résolution SIRET + code routage → PA destinataire")
public class AnnuaireController {

    private final AnnuaireService service;

    public AnnuaireController(AnnuaireService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Rattacher un SIRET à une PA")
    public AnnuaireResolution register(@Valid @RequestBody RegisterAnnuaireRequest request) {
        return service.register(request.siret(), request.codeRoutage(), request.paId());
    }

    @GetMapping
    @Operation(summary = "Résoudre la PA destinataire")
    public ResponseEntity<AnnuaireResolution> resolve(@RequestParam String siret,
                                                       @RequestParam String codeRoutage) {
        return service.resolve(siret, codeRoutage)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
