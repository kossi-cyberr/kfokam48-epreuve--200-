-- V5__deux_relecteurs.sql
-- Enveloppe (étape 3) : chaque exercice est relu par DEUX relecteurs différents.
-- La contrainte d'unicité V3 (un seul relecteur par exercice) est supprimée ;
-- l'unicité devient (exercice_id, relecteur_id) : un même pair ne relit pas deux fois.

ALTER TABLE relectures DROP CONSTRAINT IF EXISTS uq_relecture_exercice;
ALTER TABLE relectures ADD CONSTRAINT uq_relecture_exercice_relecteur UNIQUE (exercice_id, relecteur_id);
