# Simulateur de Plateforme Agréée (PA)

Application web de **test** simulant une Plateforme Agréée de facturation électronique.
Voir [`docs/01-fonctionnement-plateforme-agreee.md`](./docs/01-fonctionnement-plateforme-agreee.md) (cadrage métier)
et [`docs/02-plan-realisation.md`](./docs/02-plan-realisation.md) (plan de réalisation).

> ⚠️ **ENVIRONNEMENT DE TEST** — aucune valeur fiscale. Données fictives uniquement.

## Stack

| Couche      | Choix |
|-------------|-------|
| Backend     | Spring Boot 4 / Java 25, Spring Modulith |
| Persistance | PostgreSQL + Flyway |
| Frontend    | Angular 21+ (standalone, zoneless, signals) |
| API         | REST + OpenAPI (springdoc) |
| Tests       | Spock (back), Vitest/Testing Library (front), Testcontainers |

## Monorepo

```
pa/
├── backend/    # API Spring Boot Modulith
├── frontend/   # UI Angular 21
├── docs/       # cadrage métier + plan
└── docker-compose.yml
```

## Démarrage (Lot 0)

### Tout-en-un (Docker Compose)

```bash
docker compose up --build
```

- API : http://localhost:8080 — Swagger UI : http://localhost:8080/swagger-ui.html
- Front : http://localhost:4200
- PostgreSQL : localhost:5432 (`pa` / `pa`)

### Développement local

**Backend**
```bash
cd backend
mvn spring-boot:run        # nécessite une PostgreSQL locale (cf. application.yml)
mvn test                   # tests Spock + vérification Modulith
```

**Frontend**
```bash
cd frontend
npm install
npm start                  # ng serve
npm test                   # Vitest
```

## Authentification (Lot 0)

Auth par **clé API** via l'en-tête `X-API-Key`.

1. Enregistrer une entreprise (endpoint public) :
   ```bash
   curl -X POST http://localhost:8080/api/v1/entreprises \
     -H 'Content-Type: application/json' \
     -d '{"siren":"552081317","siret":"55208131766522","raisonSociale":"ACME","tvaIntra":"FR55552081317"}'
   ```
   La réponse contient une `apiKey` **affichée une seule fois**.
2. Utiliser cette clé sur les endpoints protégés :
   ```bash
   curl http://localhost:8080/api/v1/entreprises/me -H 'X-API-Key: <clé>'
   ```

## Lotissement

Voir [`docs/02-plan-realisation.md`](./docs/02-plan-realisation.md) §7. Ce dépôt couvre le **Lot 0 — Socle**.
