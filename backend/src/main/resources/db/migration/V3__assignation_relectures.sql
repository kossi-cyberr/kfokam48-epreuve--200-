-- V3__assignation_relectures.sql
-- Évolution du modèle : une relecture est d'abord ASSIGNÉE (note en attente),
-- puis RENDUE par le relecteur, puis corrigeable tant que la session est ouverte (Q10).

ALTER TABLE relectures ALTER COLUMN note DROP NOT NULL;
ALTER TABLE relectures ALTER COLUMN commentaire DROP NOT NULL;

-- Un étudiant appartient à une promotion (le tableau du formateur est par promotion, Q16)
ALTER TABLE students ADD COLUMN promotion_id BIGINT REFERENCES promotions(id);

-- RG6 : une seule présence par étudiant et par session (Q3)
ALTER TABLE presences ADD CONSTRAINT uq_presence_session_etudiant UNIQUE (session_id, etudiant_id);

-- Un seul exercice déposé par étudiant et par session (contrat : 409 EXERCICE_DEJA_DEPOSE)
ALTER TABLE exercices ADD CONSTRAINT uq_exercice_session_etudiant UNIQUE (session_id, etudiant_id);

-- RG8 : un seul relecteur assigné par exercice (Q6)
ALTER TABLE relectures ADD CONSTRAINT uq_relecture_exercice UNIQUE (exercice_id);
