# CHANGELOG — Épreuve finale fullstack KFOKAM48

Tous les changements notables de ce projet sont documentés ici.

## [1.0.0] — 25/09/2026 — [JALON] v1.0

### Réparé (le socle initial ne compilait pas et ne démarrait pas)
- Champ `createdAt` dupliqué dans `Exercice`, imports `@PrePersist`/`@PreUpdate` manquants,
  injection `presenceRepository` absente, `RelectureRequest` sans `relecteurId` : compilation rétablie.
- **Double migration V1** (`V1__create_items.sql` du socle + `V1__create_kos_tables.sql`) :
  Flyway refusait de démarrer. L'ancienne est supprimée.
- **Seed** : codes `KFOKAM48-001` (12 caractères) dans une colonne `code VARCHAR(10)` →
  renommés `DEMOS001`/`DEMOS002`.
- **Frontend fantôme** : `frontend/kossi-app` était un gitlink sans `.gitmodules` et vide —
  remplacé par une vraie application Next.js.

### Ajouté
- **`GET /api/tableau?promotionId=`** (opération imposée n°5) : présence, dépôts,
  moyenne (calculée côté API, RG15) et relectures en attente par étudiant (Q16).
- **Relecture en deux temps** : assignation (`POST /api/relectures/{exerciceId}/assignation`,
  tirage au hasard parmi les présents, auteur exclu — Q6/Q7/RG7/RG8) puis rendu
  (`POST /api/relectures/{id}` avec `{note, commentaire}`, conforme à l'annexe B).
- **Correction de relecture** avant clôture (Q10, RG9) : `PUT /api/relectures/{id}`.
- **Blocage après 5 codes erronés** (Q4, RG17) : 429 `ETUDIANT_BLOQUE` pendant 2 minutes.
- **Clôture de session** exposée : `POST /api/sessions/{id}/cloture` (204).
- Migrations **V3** (relectures assignées, unicité RG6 et exercice, `promotion_id` sur
  students) et **V4** (backfill promotion des étudiants de démo).
- **Frontend Next.js 16 / React 19** : écran formateur (session + tableau), écran étudiant
  (présence + dépôt/remplacement), écran relecteur (rendre/corriger) ; couche API dédiée
  `lib/api.ts`, états de chargement et d'erreur (F1, F2, F3).
- **Tests B6** : `PresenceServiceImplTest` (4 unitaires : RG1, RG6, Q4, nominal) et
  `PresenceIntegrationTest` (4 intégrations Testcontainers : 201/400/410/409).

### Modifié
- **Codes HTTP conformes au contrat** : code inconnu 400, code expiré 410, déjà présent /
  exercice déjà déposé / relecture déjà rendue 409, auto-relecture / relecture débutée 403 —
  auparavant tout renvoyait 404.
- **Format d'erreur imposé** `{ "code": "...", "message": "..." }` partout (B4) ;
  l'ancien `ApiError` (timestamp/status/path) est supprimé.
- `@Valid` sur tous les `@RequestBody` ; statut `RELU` appliqué à l'exercice après relecture.
- `contrat.yaml` version 1.1 aligné sur l'implémentation et l'annexe B.
- Cahier des charges 1.1 (RG5 reformulée, RG17 à RG19), D2/D3 réécrits et alignés,
  README testé, journal complété.

---

## Historique des jalons

- `[JALON] analyse` — cahier des charges, 4 diagrammes Mermaid, contrat complété, issues créées.
- `[JALON] v0.1` — première version de l'API (Must), README, backlog trié.
- `[JALON] v1.0` — socle réparé et conforme, frontend livré, tests verts, docs à jour.
