package fr.pa.simulator.config;

import fr.pa.simulator.entreprise.DuplicateSiretException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduit les exceptions métier en réponses {@link ProblemDetail} (RFC 7807).
 * Les erreurs de validation des corps de requête sont déjà gérées par Spring
 * ({@code spring.mvc.problemdetails.enabled=true}).
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateSiretException.class)
    ProblemDetail handleDuplicate(DuplicateSiretException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Conflit");
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Requête invalide");
        return problem;
    }
}
