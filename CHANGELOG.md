# CHANGELOG — Épreuve finale fullstack KFOKAM48

Tous les changements apportés au projet sont listés ici.

---

## [1.0.0] — 24/09/2026

### Ajouté

- **Étape 1 — Analyse et conception** :
  - `docs/CAHIER_DES_CHARGES.md` : cahier des charges complet (10 sections)
  - `docs/diagrammes/` : 3 diagrammes Mermaid (D1, D2, D3)
  - `api/contrat.yaml` : contrat d'API complet (5 opérations imposées + compléments)
  - `docs/BACKLOG.md` : backlog trié par priorité (Must / Should / Could)
  - `docs/JOURNAL.md` : journal de bord
  - Commit `[JALON] analyse` poussé

- **Étape 2 — Première version** :
  - `backend/` : API Spring Boot 4 (Java 21) avec :
    - 5 entités : Promotion, Student, Session, Presence, Exercice, Relecture
    - 14 DTOs
    - 10 repositories (Spring Data JPA)
    - 11 services (interfaces + implémentations)
    - 6 contrôleurs REST
    - Migrations Flyway (V1 schéma, V2 données de démonstration)
    - Sécurité Spring (ouverture pour Swagger + healthcheck)
    - Validation Bean Validation
    - Gestion centralisée des erreurs (`@RestControllerAdvice`)
  - `frontend/kossi-app/` : Application Next.js 16 avec :
    - Page d'accueil : tableau du formateur
    - Page /pos : marquer sa présence
    - Page /exercice/new : déposer un exercice
    - Page /revisiter : faire une relecture
    - Couche API dédiée (`lib/api.ts`)
    - Design responsive avec Tailwind CSS 4
  - `README.md` mis à jour avec le projet KFOKAM48

### Modifié

- `api/contrat.yaml` : complet, figé avant le premier commit de code
- `docs/CAHIER_DES_CHARGES.md` : mis à jour pour refléter l'implémentation
- `docs/JOURNAL.md` : initialisé

### Supprimé

- `backend/src/main/java/com/kfokam/kos/model/Item.java` (ancien modèle de démonstration)
- `backend/src/main/java/com/kfokam/kos/controller/ItemController.java`
- `backend/src/main/java/com/kfokam/kos/repository/ItemRepository.java`
- `backend/src/main/java/com/kfokam/kos/service/ItemService.java`
- `backend/src/main/java/com/kfokam/kos/service/impl/ItemServiceImpl.java`
- `backend/src/main/java/com/kfokam/kos/dto/ItemRequest.java`
- `backend/src/main/java/com/kfokam/kos/dto/ItemResponse.java`

---

## Notes sur l'historique

- L'analyse (étape 1) est documentée et figée.
- La première version (étape 2) est implémentée avec les tickets Must.
- L'enveloppe (étape 3) n'a pas été fournie lors de l'épreuve.
- La version finale (étape 4) est en préparation.
- L'épreuve Git (étape 5) n'est pas encore effectuée.
- La soumission (étape 6) n'est pas encore faite.

---

## Définitions de version

- **1.0.0** : Première version stable (étape 2, v0.1)