package fr.pa.simulator.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pa.simulator.entreprise.EntrepriseService;
import fr.pa.simulator.entreprise.EntrepriseView;
import fr.pa.simulator.shared.RequestAttributes;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Authentifie les requêtes {@code /api/**} via l'en-tête {@code X-API-Key}.
 *
 * <p>Endpoints publics (non filtrés) : enregistrement d'une entreprise ({@code POST /api/v1/entreprises}),
 * documentation et actuator. En cas de clé absente ou invalide, renvoie {@code 401} au format
 * {@link ProblemDetail} (RFC 7807).</p>
 */
@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    static final String HEADER = "X-API-Key";
    private static final String API_PREFIX = "/api/";
    private static final String REGISTER_PATH = "/api/v1/entreprises";

    private final EntrepriseService entrepriseService;
    private final ObjectMapper objectMapper;

    public ApiKeyAuthFilter(EntrepriseService entrepriseService, ObjectMapper objectMapper) {
        this.entrepriseService = entrepriseService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (!path.startsWith(API_PREFIX)) {
            return true;
        }
        // Enregistrement d'une entreprise : public
        return HttpMethod.POST.matches(request.getMethod()) && REGISTER_PATH.equals(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader(HEADER);
        Optional<EntrepriseView> tenant = entrepriseService.authenticate(apiKey);
        if (tenant.isEmpty()) {
            writeUnauthorized(response, apiKey == null || apiKey.isBlank()
                    ? "En-tête X-API-Key manquant"
                    : "Clé API invalide");
            return;
        }
        request.setAttribute(RequestAttributes.TENANT, tenant.get());
        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response, String detail) throws IOException {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                org.springframework.http.HttpStatus.UNAUTHORIZED, detail);
        problem.setTitle("Authentification requise");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), problem);
    }
}
