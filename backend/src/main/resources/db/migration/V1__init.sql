-- Lot 0 — socle : entreprise + annuaire minimal
-- Environnement de TEST, données fictives uniquement.

CREATE TABLE entreprise (
    id              UUID         NOT NULL PRIMARY KEY,
    siren           VARCHAR(9)   NOT NULL,
    siret           VARCHAR(14)  NOT NULL UNIQUE,
    raison_sociale  VARCHAR(255) NOT NULL,
    tva_intra       VARCHAR(20),
    pa_rattachement VARCHAR(64),
    api_key_hash    VARCHAR(64)  NOT NULL UNIQUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_entreprise_siren ON entreprise (siren);

-- Annuaire (PPF simulé) : SIRET + code routage -> PA destinataire
CREATE TABLE annuaire_entry (
    id           UUID         NOT NULL PRIMARY KEY,
    siret        VARCHAR(14)  NOT NULL,
    code_routage VARCHAR(64)  NOT NULL,
    pa_id        VARCHAR(64)  NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_annuaire_siret_routage UNIQUE (siret, code_routage)
);

CREATE INDEX idx_annuaire_siret ON annuaire_entry (siret);
