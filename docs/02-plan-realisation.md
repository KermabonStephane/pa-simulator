# Plan de réalisation — Simulateur de Plateforme Agréée (PA)

> Application web de **test** simulant une Plateforme Agréée de facturation électronique.
> Voir [01-fonctionnement-plateforme-agreee.md](./01-fonctionnement-plateforme-agreee.md) pour le cadrage métier.

---

## 1. Objectifs produit

Permettre à une application tierce (un logiciel de facturation, un ERP, un OD) de **tester** son
intégration à une PA, en couvrant tout le cycle :

1. **Générer** une facture électronique (Factur-X / UBL / CII).
2. **Émettre / déposer** la facture sur la PA.
3. **Router** la facture vers la PA du destinataire (annuaire + PA ↔ PA).
4. **Réceptionner** et mettre à disposition la facture côté client.
5. **Gérer les statuts** du cycle de vie (déposée, rejetée, refusée, encaissée…).
6. **Simuler** l'e-reporting et le PPF (annuaire + concentrateur de données).
7. Le tout via une **API REST** documentée + une **interface web** de démonstration et d'observation.

### Principes
- **Réaliste mais pas conforme** : on imite les flux et formats, sans valeur fiscale.
- **Multi-tenant léger** : plusieurs entreprises et plusieurs « PA simulées » coexistent pour tester
  le routage PA ↔ PA sans déployer plusieurs instances.
- **Observable** : tout flux, statut et transition est traçable (journal, timeline UI, webhooks).
- **Pilotable** : possibilité de **forcer** des scénarios (rejet, refus, retard, erreur) pour tester
  la robustesse des clients.

---

## 2. Stack technique proposée

| Couche | Choix | Justification |
|--------|-------|---------------|
| **Backend** | **Spring Boot 4 / Java 25**, Spring Modulith | Modules métier clairs, écosystème mûr (validation, sécurité, scheduling). |
| **Architecture** | **Clean / Hexagonal** | Domaine isolé des frameworks ; testable. |
| **Persistance** | PostgreSQL + Flyway | Données relationnelles (factures, statuts, annuaire). JSONB pour payloads bruts. |
| **Frontend** | **Angular 21+** (standalone, signals) | UI de démonstration et console d'observation. |
| **Formats facture** | `mustangproject` (Factur-X), génération XML CII/UBL | Lib Java éprouvée pour Factur-X/ZUGFeRD. |
| **API** | REST + OpenAPI (springdoc) | Contrat clair pour les clients de test. |
| **Async / flux** | Événements internes Spring + webhooks sortants ; option file (RabbitMQ/Kafka) plus tard | Simuler l'asynchronisme PA ↔ PA. |
| **Auth** | OAuth2 client-credentials / API keys par entreprise | Imiter l'authentification d'une vraie PA. |
| **Tests** | Spock (back), Vitest/Testing Library (front), Testcontainers | Couverture domaine + intégration. |
| **Conteneurisation** | Docker Compose (api + db + front) | Démarrage one-shot pour les testeurs. |

> Alternative si l'on veut un mono-langage : **NestJS + Angular**. Le plan ci-dessous reste valable,
> seuls les outils changent.

---

## 3. Architecture cible (modules)

Découpage **Spring Modulith** (un module = un bounded context) :

```
pa-simulator/
├── facture        # création, formats (Factur-X/UBL/CII), validation EN 16931 (subset)
├── emission       # dépôt, contrôles, extraction de données, déclenchement routage
├── routage        # consultation annuaire, transmission PA↔PA (in-process ou webhook)
├── reception      # mise à disposition côté destinataire, accusés
├── cycle-de-vie   # gestion & historisation des statuts
├── annuaire       # SIRET → PA, codes routage (PPF simulé)
├── e-reporting    # agrégation transactions/paiements vers "PPF"
├── ppf            # concentrateur de données + endpoints annuaire (mock administration)
├── entreprise     # comptes, rattachement à une PA, clés API
└── shared         # value objects (Siret, Montant, TauxTVA), erreurs, audit
```

Chaque module : `domain/` (entités, VO, règles) · `application/` (use cases) · `infrastructure/`
(REST, persistance, libs externes).

---

## 4. Modèle de données (principal)

- **Entreprise** : `siren`, `siret`, raison sociale, n° TVA intracom, `pa_rattachement`, clés API.
- **PA (simulée)** : `id`, libellé, endpoint webhook, mode (in-process / externe).
- **AnnuaireEntry** : `siret` + `code_routage` → `pa_id`.
- **Facture** : `id`, `numero`, émetteur, destinataire, lignes, montants (HT/TVA/TTC), `format`,
  `profil` (Factur-X), payload brut (XML/PDF), `statut_courant`.
- **LigneFacture** : désignation, qté, PU, taux TVA, montants.
- **StatutEvenement** : `facture_id`, `type` (Déposée/Rejetée/Refusée/Encaissée/…), horodatage,
  acteur, motif, payload.
- **EReportingLot** : type (transaction B2C / international / paiement), période, agrégats.
- **JournalFlux** : trace de chaque échange (flux 2 facture, flux 1 données, flux 6 statuts, flux 10 e-reporting), pour l'observabilité.

---

## 5. API REST (esquisse de contrat)

> Authentification par clé API / token rattaché à une entreprise. Documentée via OpenAPI (`/swagger-ui`).

### Émission
- `POST /api/v1/factures` — créer une facture (JSON métier) → renvoie l'objet + format généré.
- `POST /api/v1/factures/{id}/emettre` — **déposer** sur la PA (flux 2) → statut `Déposée` ou `Rejetée`.
- `GET  /api/v1/factures/{id}` — détail + statut courant.
- `GET  /api/v1/factures/{id}/factur-x` — télécharger le PDF/A-3 Factur-X.
- `GET  /api/v1/factures/{id}/xml?format=ubl|cii` — télécharger le XML.

### Réception (côté destinataire)
- `GET  /api/v1/inbox` — factures reçues (mises à disposition).
- `POST /api/v1/factures/{id}/statuts` — pousser un statut (`Refusée`, `Approuvée`, `Encaissée`…).

### Annuaire (PPF simulé)
- `GET  /api/v1/annuaire?siret=...&codeRoutage=...` — résoudre la PA destinataire (flux 12-14).
- `POST /api/v1/annuaire` — rattacher une entreprise à une PA.

### E-reporting / PPF
- `GET  /api/v1/ppf/donnees-facturation` — données extraites transmises (flux 1).
- `GET  /api/v1/ppf/e-reporting` — lots de transaction/paiement.

### Cycle de vie / observabilité
- `GET  /api/v1/factures/{id}/statuts` — historique des statuts.
- `GET  /api/v1/journal` — journal des flux (filtres).
- **Webhooks** : l'entreprise enregistre une URL ; la PA notifie réception de facture et changements
  de statut (pour tester l'intégration événementielle des clients).

### Pilotage de scénarios (test)
- `POST /api/v1/scenarios/forcer-rejet`, `.../forcer-refus`, `.../retard`, `.../erreur-pa`
  — injecter des comportements pour tester la résilience des clients.

---

## 6. Interface web (Angular)

Pages principales :

1. **Dashboard** — vue d'ensemble (factures émises/reçues, statuts, flux récents).
2. **Création de facture** — formulaire (parties, lignes, TVA) + aperçu + choix du format/profil.
3. **Émises** — liste + timeline du cycle de vie de chaque facture.
4. **Boîte de réception** — factures reçues, actions (approuver / refuser / marquer encaissée).
5. **Annuaire** — gestion des entreprises et de leur PA de rattachement.
6. **PPF / e-reporting** — visualisation des données concentrées.
7. **Journal des flux** — timeline technique (flux 2 facture, flux 1 données, flux 6 statuts) avec payloads.
8. **Console de scénarios** — déclencher rejets/refus/erreurs pour les tests.
9. **Settings** — clés API, webhooks, profils d'entreprise.

---

## 7. Lotissement (roadmap incrémentale)

### Lot 0 — Socle (1 sprint)
- Repo, Docker Compose (api + PostgreSQL + front), CI, OpenAPI, squelette Modulith + Angular.
- Modèle `Entreprise`, auth par clé API, module `annuaire` minimal.

### Lot 1 — Génération & validation de factures (1–2 sprints)
- Module `facture` : modèle métier, génération **Factur-X** (mustangproject), **UBL**, **CII**.
- Validation : mentions obligatoires + subset **EN 16931** + cohérence TVA.
- UI : formulaire de création + aperçu + téléchargements.

### Lot 2 — Émission & routage (flux 2 + flux 1) (2 sprints)
- `POST /factures/{id}/emettre` : contrôles → `Déposée`/`Rejetée`.
- Extraction des données (flux 1) vers `ppf` simulé ; transmission de la facture (flux 2) PA ↔ PA.
- Consultation `annuaire` → transmission **PA ↔ PA** (in-process puis webhook).
- `reception` : mise à disposition + boîte de réception UI.

### Lot 3 — Cycle de vie / statuts (flux 6) (1–2 sprints)
- Module `cycle-de-vie` : 4 statuts obligatoires + recommandés, machine à états.
- Remontée des statuts PA-B → PA-A → émetteur + notification PPF.
- Webhooks sortants. Timeline UI.

### Lot 4 — E-reporting & PPF (flux 10) (1 sprint)
- Agrégation des 4 blocs : 10.1 (B2B int'l), 10.2 (paiements int'l), 10.3 (B2C), 10.4 (paiements B2C).
- Vues PPF (données de facturation + e-reporting).

### Lot 5 — Outillage de test & robustesse (1 sprint)
- Console de scénarios (forcer rejet/refus/retard/erreur).
- Jeux de données de démo (seed), export/import.
- Documentation d'intégration pour les équipes clientes + collection Postman/Bruno.

### Lot 6 (optionnel) — Réalisme accru
- Plusieurs instances PA en conteneurs distincts (vraie interop PA↔PA réseau).
- Signature/cachet électronique simulé, file de messages (Kafka), simulation de SLA/latence.

---

## 8. Stratégie de test

- **Domaine** : tests unitaires Spock sur règles de validation et machine à états des statuts.
- **Intégration** : Testcontainers (PostgreSQL), tests des flux bout-en-bout (émission → réception → encaissée).
- **Contrat** : validation des XML générés contre les **schémas XSD** (CII/UBL) et profils Factur-X.
- **E2E front** : parcours création → émission → réception → statut.
- **Échantillons** : se constituer un jeu de factures de référence (cas nominal + cas d'erreur).

---

## 9. Risques & points d'attention

| Risque | Mitigation |
|--------|------------|
| Spécifications externes DGFiP évolutives | Isoler la numérotation/format des flux derrière des adaptateurs ; documenter la version ciblée. |
| Confusion test ↔ production | Bandeau « ENVIRONNEMENT DE TEST » visible, watermark sur PDF, données fictives uniquement. |
| Complexité Factur-X (profils, PDF/A-3) | Démarrer sur profil `BASIC`/`EN 16931`, s'appuyer sur mustangproject. |
| Validation EN 16931 exhaustive | Implémenter un **subset** pertinent pour les tests, pas la conformité totale. |

---

## 10. Prochaine étape proposée

Démarrer le **Lot 0** : initialiser le monorepo (backend Spring Modulith + frontend Angular + Docker
Compose) et poser le modèle `Entreprise` / `annuaire`. Je peux générer ce squelette dès validation
de la stack (voir §2).
