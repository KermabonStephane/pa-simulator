# Fonctionnement d'une Plateforme Agréée (PA) — Facturation électronique en France

> Document de cadrage fonctionnel. Objectif : décrire le rôle, l'écosystème et **les flux** d'une
> Plateforme Agréée afin de construire une plateforme de **test** (non destinée à la production /
> non immatriculée par la DGFiP) servant de bac à sable aux applications qui s'interfacent avec une PA.

---

## 1. Contexte réglementaire

La France généralise la **facturation électronique** entre entreprises assujetties à la TVA
(transactions domestiques **B2B**), ainsi que le **e-reporting** de certaines données de transaction
et de paiement.

### Calendrier (au moment de la rédaction)

| Échéance | Obligation |
|----------|------------|
| **1er septembre 2026** | **Réception** de factures électroniques obligatoire pour **toutes** les entreprises assujetties à la TVA. **Émission** + e-reporting pour les **grandes entreprises** et **ETI**. |
| **1er septembre 2027** | **Émission** + e-reporting pour les **TPE / PME / micro-entreprises**. |

### Terminologie : PDP → PA

Depuis **juillet 2025**, l'appellation officielle **Plateforme de Dématérialisation Partenaire (PDP)**
a été remplacée par **Plateforme Agréée (PA)**, pour insister sur l'**agrément** (immatriculation)
délivré par la DGFiP. Les deux termes désignent la même chose. La DGFiP a commencé à publier les
premières immatriculations définitives en **décembre 2025**, après une phase de tests d'interopérabilité.

> ⚠️ Notre projet **simule** une PA pour des tests d'intégration. Il n'a pas vocation à être immatriculé
> ni à transporter de vraies factures à valeur fiscale.

---

## 2. Les acteurs de l'écosystème

| Acteur | Rôle |
|--------|------|
| **Émetteur (fournisseur)** | Entreprise qui établit et envoie une facture. |
| **Récepteur (client)** | Entreprise destinataire qui reçoit, traite et paie la facture. |
| **PA — Plateforme Agréée** | Plateforme immatriculée DGFiP qui **émet, transmet, reçoit** les factures et réalise le **e-reporting**. C'est elle qui garantit intégrité, authenticité, lisibilité et complétude des données. C'est l'objet que nous simulons. |
| **OD — Opérateur de Dématérialisation** | Prestataire **non agréé** (logiciel de facturation, dématérialiseur). Il peut produire/recevoir des flux mais **doit obligatoirement s'appuyer sur une PA** pour la transmission officielle. |
| **PPF — Portail Public de Facturation** | Service de l'État. Depuis la réforme 2024, il **ne transporte plus les factures**. Il assure deux missions exclusives : (1) **l'annuaire central** ; (2) la **concentration et transmission à la DGFiP** des données de facturation, de transaction, de paiement et des statuts du cycle de vie. |
| **DGFiP** | Administration fiscale, destinataire final des données (contrôle TVA, pré-remplissage). |

### Du « modèle en Y » au modèle décentralisé

Initialement, le PPF devait aussi offrir un service gratuit d'échange de factures (« modèle en Y »).
Cette mission a été abandonnée : **toutes les factures transitent désormais par des PA** (modèle
« décentralisé » / **PA ↔ PA**). Le PPF se limite à **l'annuaire** et à la **collecte des données**.

```
   ┌────────────┐                         ┌────────────┐
   │  Émetteur  │                         │  Récepteur │
   │(fournisseur)│                        │  (client)  │
   └─────┬──────┘                         └─────▲──────┘
         │ dépôt facture                        │ mise à disposition
         ▼                                       │
   ┌────────────┐      facture (PA↔PA)    ┌──────┴─────┐
   │   PA  A    │ ───────────────────────▶│   PA  B    │
   │ (émettrice)│◀─────────────────────── │(réceptrice)│
   └─────┬──────┘   statuts cycle de vie  └──────┬─────┘
         │                                        │
         │   données de facturation / e-reporting / statuts
         ▼                                        ▼
              ┌──────────────────────────────┐
              │   PPF (annuaire + concentrateur)│
              └───────────────┬───────────────┘
                              ▼
                         ┌─────────┐
                         │  DGFiP  │
                         └─────────┘
```

---

## 3. Les deux grandes obligations

### 3.1 E-invoicing (facturation électronique)
Concerne les transactions **B2B domestiques** (vendeur ET acheteur établis en France, assujettis TVA).
La facture doit **circuler sous forme électronique structurée** via les PA — un PDF « simple » par
e-mail ne suffit plus.

### 3.2 E-reporting (transmission de données)
Concerne les transactions **hors champ de l'e-invoicing** mais soumises à déclaration :
- **B2C** (ventes aux particuliers),
- transactions **internationales / intracommunautaires** (B2B avec un acteur non établi en France),
- **données de paiement** (pour les prestations de services, l'exigibilité de la TVA suit l'encaissement).

La PA transmet périodiquement ces données agrégées au PPF (fréquence selon le régime TVA de l'entreprise).

---

## 4. Les formats de facture

Le **socle minimal** que toute PA doit savoir traiter :

| Format | Nature | Usage |
|--------|--------|-------|
| **Factur-X** | **Hybride** : PDF/A-3 **lisible** par un humain + **XML CII embarqué** lisible par une machine. | Le plus répandu côté TPE/PME. |
| **UBL** (Universal Business Language) | XML pur, standard OASIS. | Échanges structurés. |
| **CII** (UN/CEFACT Cross Industry Invoice) | XML pur. | Base du XML de Factur-X. |

Tous reposent sur la norme sémantique européenne **EN 16931**. Factur-X propose des **profils** de
richesse croissante : `MINIMUM`, `BASIC WL`, `BASIC`, `EN 16931 (COMFORT)`, `EXTENDED`.
Le profil `MINIMUM` ne porte que les données fiscales essentielles ; `EN 16931` couvre une facture complète.

---

## 5. L'annuaire central

Géré par le PPF, c'est l'**aiguilleur** du dispositif. Il associe à chaque entité (clé **SIREN/SIRET**,
éventuellement complétée d'un **code routage** pour cibler un service/établissement) la **PA de
rattachement** du destinataire.

**Cycle d'usage** :
1. Une entreprise s'inscrit auprès de sa PA → la PA déclare ce rattachement à l'annuaire.
2. Avant d'émettre, la PA émettrice **interroge l'annuaire** avec le SIRET du client.
3. L'annuaire renvoie la PA destinataire (et l'adresse de routage) → la PA sait **où** livrer la facture.

> Sans annuaire, une PA ne sait pas vers quelle autre PA router une facture. C'est le pivot de
> l'interopérabilité PA ↔ PA.

---

## 6. Les flux (cœur du dispositif)

Les **Spécifications externes** de la DGFiP (version **3.0**, publiée le **19 décembre 2024**, alignée
sur le rôle recentré du PPF) définissent une série de **flux numérotés**. La numérotation ci-dessous
reprend celle des spécifications. Le périmètre **PPF** se limite désormais aux services **« Annuaire »**
(flux 12, 13, 14) et **« Concentrateur »** (flux 1, 6, 10) ; le cadre normatif des échanges **entre PA**
(flux 2, 8, 9) relève de la commission e-invoicing.

### Vue d'ensemble

| Flux | Nom | Circule entre | Contenu |
|------|-----|---------------|---------|
| **Flux 2** | **Facture électronique (format socle)** | Émetteur → PA-A → PA-B → Récepteur | La facture au format **Factur-X / UBL / CII**. **Cœur de la réforme.** Transite exclusivement via les PA. |
| **Flux 3** | Facture (formats non-socle) | PA ↔ PA | Formats **hors socle** (EDIFACT, XML propriétaire) sous **accord préalable** entre les parties. |
| **Flux 1** | **Données de facturation** | PA → **Concentrateur (PPF)** → DGFiP | Données extraites par la PA (en-tête/pied, et dès 2027 le détail des lignes). À transmettre **≤ 24 h** après le dépôt. |
| **Flux 6** | **Cycle de vie (statuts)** | PA ↔ PA ↔ parties, et → **Concentrateur** | Statuts de la facture (les 4 obligatoires + recommandés/libres). |
| **Flux 8 / 9** | E-reporting « riche » (optionnels) | PA → Concentrateur | Données de transactions **internationales** et **B2C**. **Si non utilisés**, la PA produit le **flux 10**. |
| **Flux 10** | **E-reporting agrégé** | PA → **Concentrateur (PPF)** | Données fiscales hors e-invoicing, en 4 blocs (voir ci-dessous). |
| **Flux 12 / 13 / 14** | **Annuaire** | PA ↔ **PPF (annuaire)** | Gestion et consultation de l'annuaire (rattachement, adresses de routage). |

### Flux 2 — La facture électronique (le flux central)
Le fournisseur (ou son OD) dépose la facture sur sa PA dans un **format du socle** (Factur-X, UBL, CII).
La PA :
- contrôle la **conformité** (format, schéma, règles EN 16931, mentions obligatoires, cohérence TVA) ;
- en cas d'échec → statut **« Rejetée »** (via le flux 6) renvoyé à l'émetteur ;
- en cas de succès → **extrait les données** (→ flux 1), **consulte l'annuaire** (flux 12-14) et
  **transmet la facture à la PA destinataire** (PA-A → PA-B), qui la **met à disposition** du client.

### Flux 1 — Données de facturation (PA → Concentrateur PPF → DGFiP)
La PA transmet au **Concentrateur** les **données de facturation extraites** (identifiants des parties,
montants HT / TVA / TTC, etc.) destinées à l'administration fiscale, **dans les 24 h** suivant le dépôt.

### Flux 6 — Cycle de vie de la facture (statuts)
Les statuts circulent entre PA et parties, et les statuts **obligatoires** sont remontés au Concentrateur :

- **Statuts obligatoires** (les 4 incontournables) :
  1. **Déposée** — la facture a été déposée sur la plateforme.
  2. **Rejetée** — rejet **technique** (format/contrôles) avant traitement métier.
  3. **Refusée** — refus **métier** par le destinataire (litige, erreur de contenu).
  4. **Encaissée** — facture **payée / encaissée** (déclenche notamment l'exigibilité TVA sur services).
- **Statuts recommandés** (optionnels mais normalisés) : *Mise à disposition, Prise en charge,
  Approuvée, Approuvée partiellement, En litige, Suspendue, Complétée, Paiement transmis…*
- **Statuts libres** : définis librement entre partenaires.

> À noter : il existe aussi des statuts **techniques** de transmission (Émise → Déposée → Reçue par
> PA émettrice → Reçue par PA réceptrice → Mise à disposition) qui tracent l'acheminement.

### Flux 10 — E-reporting agrégé (PA → Concentrateur PPF)
Quatre blocs, transmis dans **un fichier XML unique par période** (périodicité selon le régime TVA) :

| Sous-flux | Contenu | Granularité |
|-----------|---------|-------------|
| **10.1** | Transactions **B2B internationales** (factures émises/reçues avec l'étranger) | Unitaire (1 ligne / facture) |
| **10.2** | **Paiements** associés aux transactions du flux 10.1 | Unitaire (1 ligne / paiement) |
| **10.3** | Ventes **B2C agrégées** (par catégorie : biens, services, exonérées, mixtes) | Agrégé (totaux quotidiens) |
| **10.4** | **Paiements B2C agrégés** (par taux de TVA) | Agrégé (totaux quotidiens) |

### Flux 12 / 13 / 14 — Annuaire (PA ↔ PPF)
Alimentation (rattachement des entreprises à une PA) et **consultation** de l'annuaire pour le routage
des factures (résolution SIRET + code routage → PA destinataire).

---

## 7. Cycle de vie de bout en bout (exemple B2B domestique)

```
1. Fournisseur crée la facture (Factur-X) et la dépose sur PA-A          → statut "Déposée"   (flux 6)
2. PA-A valide le format + règles EN 16931
      ├─ échec → "Rejetée" (retour fournisseur)            [FIN]            (flux 6)
      └─ succès →
3. PA-A extrait les données → transmet au Concentrateur PPF (≤ 24 h)                          (flux 1)
4. PA-A interroge l'annuaire (SIRET + code routage) → identifie PA-B                        (flux 12-14)
5. PA-A → PA-B : transmission de la facture au format socle                                   (flux 2)
6. PA-B met la facture à disposition du client                          → "Mise à disposition"(flux 6)
7. Client traite la facture :
      ├─ refuse → "Refusée" (remonte jusqu'au fournisseur)                                    (flux 6)
      └─ accepte → "Approuvée" (recommandé)                                                   (flux 6)
8. Client paie → "Encaissée"                                                                  (flux 6)
9. Chaque statut obligatoire remonte PA-B → PA-A → fournisseur, et est communiqué au PPF      (flux 6)
```

> Les opérations **B2C** et **internationales** ne suivent pas ce circuit de facture : elles font
> l'objet d'un **e-reporting** agrégé (flux 10.1 à 10.4) transmis périodiquement au Concentrateur.

---

## 8. Exigences transverses d'une PA (réelle)

Pour mémoire (à simuler partiellement dans notre bac à sable) :

- **Sécurité & confiance** : authentification forte des entreprises, chiffrement en transit (TLS),
  signature/cachet, traçabilité (piste d'audit fiable, **PAF**).
- **Intégrité & authenticité** : garantie sur tout le cycle de vie de la facture.
- **Archivage** : conservation à valeur probante (durée légale).
- **Conformité TVA** : contrôle des mentions obligatoires, taux, numéro de TVA intracom., etc.
- **Disponibilité & supervision** : SLA, monitoring, gestion des rejets et reprises.
- **Interopérabilité** : APIs normalisées PA ↔ PA et PA ↔ PPF.

---

## 9. Périmètre de notre plateforme de test

Ce que nous **simulons** (pour des tests d'intégration applicatifs) :

✅ Création / dépôt de factures (Factur-X, UBL, CII)
✅ Contrôles de format et règles métier (subset EN 16931)
✅ Routage **PA ↔ PA** (plusieurs PA simulées au sein de l'app, ou via webhooks)
✅ Mise à disposition / réception côté client
✅ Gestion des **statuts du cycle de vie** (les 4 obligatoires + recommandés)
✅ **Annuaire** simulé (SIRET → PA)
✅ Stubs e-reporting (transaction + paiement) et « PPF » simulé
✅ API REST + interface web de démonstration

Ce que nous **ne** faisons **pas** :

❌ Immatriculation DGFiP / valeur fiscale réelle
❌ Archivage à valeur probante de production
❌ Connexion au véritable PPF / annuaire national

---

## Sources

- [impots.gouv.fr — Facturation électronique et plateformes agréées](https://www.impots.gouv.fr/facturation-electronique-et-plateformes-agreees)
- [economie.gouv.fr — Tout savoir sur la facturation électronique](https://www.economie.gouv.fr/tout-savoir-sur-la-facturation-electronique-pour-les-entreprises)
- [Compagnie Fiduciaire — Le Portail Public de Facturation (PPF)](https://www.compagnie-fiduciaire.com/nos-conseils/piloter-son-activite/facturation-electronique-le-portail-public-de-facturation-ppf/)
- [Compagnie Fiduciaire — Les plateformes agréées (ex PDP)](https://www.compagnie-fiduciaire.com/nos-conseils/piloter-son-activite/facture-electronique-les-plateformes-agreees-ex-plateformes-de-dematerialisation-partenaires-pdp/)
- [Libeo — Plateforme Agréée (PA, ex-PDP) : guide 2026](https://libeo.io/blog/plateforme-de-dematerialisation-partenaire)
- [Docaposte — Liste des Plateformes Agréées (juin 2026)](https://www.docaposte.com/blog/article/liste-pa)
- [impots.gouv.fr — Spécifications externes et normes (B2B)](https://www.impots.gouv.fr/specifications-externes-b2b)
- [Cegedim — Comprendre les flux de la réforme (flux 1, 2, 3, 6, 10…)](https://www.cegedim-business-services.com/factures-achats/comprendre-les-flux-de-la-reforme-de-la-facturation-electronique/)
- [FactPulse — E-reporting : flux 10.1 à 10.4](https://factpulse.fr/docs/article-e-reporting/)
- [EY — Publication d'une nouvelle version des spécifications externes (v3.0)](https://www.avocats.ey.com/fr_fr/fiscalite/facturation-electronique-nouvelles-specifications-externes)
