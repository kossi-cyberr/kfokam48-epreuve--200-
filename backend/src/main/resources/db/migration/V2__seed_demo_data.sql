-- V2__seed_demo_data.sql
-- Données de démonstration pour l'épreuve KFOKAM48

-- Promotion
INSERT INTO promotions (nom) VALUES ('KOFAKAM48-2026-1');

-- Étudiants (10 étudiants de démonstration, promotion 1)
INSERT INTO students (nom, prenom, matricule) VALUES
('Kossi', 'Jean', '200-KOS'),
('BABA', 'Marie', '201-BAB'),
('Koudou', 'Thomas', '202-KOU'),
('Diop', 'Awa', '203-DIO'),
('Sambou', 'Ibrahim', '204-SAM'),
('Camara', 'Fatoumata', '205-CAM'),
('Traoré', 'Amadou', '206-TRA'),
('Barry', 'Ibrahima', '207-BAR'),
('Cissé', 'Haïdara', '208-CIS'),
('N''Diaye', 'Penda', '209-NDI');

-- Sessions (2 sessions de démonstration, codes <= 10 caractères)
INSERT INTO sessions (titre, code, ouverture_at, expiration_at, clotee, promotion_id) VALUES
('Session 1 - Présence et exercices', 'DEMOS001', NOW() - INTERVAL '1 hour', NOW() + INTERVAL '14 minutes', FALSE, 1),
('Session 2 - Exercices avancés', 'DEMOS002', NOW() - INTERVAL '2 hours', NOW() + INTERVAL '13 minutes', FALSE, 1);
