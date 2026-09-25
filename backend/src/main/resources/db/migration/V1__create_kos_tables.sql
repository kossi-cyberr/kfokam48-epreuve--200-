-- V1__create_kos_tables.sql
-- Schéma de l'épreuve KFOKAM48

CREATE TABLE promotions (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(120) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE students (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(120) NOT NULL,
    prenom VARCHAR(120) NOT NULL,
    matricule VARCHAR(20) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sessions (
    id BIGSERIAL PRIMARY KEY,
    titre VARCHAR(120) NOT NULL,
    code VARCHAR(10) NOT NULL,
    ouverture_at TIMESTAMP NOT NULL,
    expiration_at TIMESTAMP NOT NULL,
    clotee BOOLEAN NOT NULL DEFAULT FALSE,
    promotion_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sessions_promotion FOREIGN KEY (promotion_id) REFERENCES promotions(id)
);

CREATE TABLE presences (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    etudiant_id BIGINT NOT NULL,
    source VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_presences_session FOREIGN KEY (session_id) REFERENCES sessions(id),
    CONSTRAINT fk_presences_etudiant FOREIGN KEY (etudiant_id) REFERENCES students(id)
);

CREATE TABLE exercices (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    etudiant_id BIGINT NOT NULL,
    lien VARCHAR(2000) NOT NULL,
    statut VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_exercices_session FOREIGN KEY (session_id) REFERENCES sessions(id),
    CONSTRAINT fk_exercices_etudiant FOREIGN KEY (etudiant_id) REFERENCES students(id)
);

CREATE TABLE relectures (
    id BIGSERIAL PRIMARY KEY,
    exercice_id BIGINT NOT NULL,
    relecteur_id BIGINT NOT NULL,
    note INTEGER NOT NULL,
    commentaire VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_relectures_exercice FOREIGN KEY (exercice_id) REFERENCES exercices(id),
    CONSTRAINT fk_relectures_relecteur FOREIGN KEY (relecteur_id) REFERENCES students(id)
);

CREATE INDEX idx_presences_session ON presences(session_id);
CREATE INDEX idx_presences_etudiant ON presences(etudiant_id);
CREATE INDEX idx_exercices_session ON exercices(session_id);
CREATE INDEX idx_exercices_etudiant ON exercices(etudiant_id);
CREATE INDEX idx_relectures_exercice ON relectures(exercice_id);
