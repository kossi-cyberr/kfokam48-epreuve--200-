-- ============================================================
-- V1 : schéma initial (items)
-- Flyway est la seule source de vérité du schéma :
-- Hibernate est en mode "validate" et ne modifie jamais la base.
-- Convention de nommage : snake_case, une table par entité.
-- ============================================================

CREATE TABLE items
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(120)        NOT NULL,
    description VARCHAR(1000),
    quantity    INTEGER             NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ         NOT NULL DEFAULT now()
);

CREATE INDEX idx_items_name ON items (LOWER(name));
