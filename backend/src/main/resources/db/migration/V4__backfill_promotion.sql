-- V4__backfill_promotion.sql
-- Les étudiants de démonstration appartiennent à la promotion 1
-- (le tableau du formateur est construit par promotion, Q16).
UPDATE students SET promotion_id = 1 WHERE promotion_id IS NULL;
