# kos — Épreuve finale fullstack KFOKAM48

**Auteur :** Jean Bertin Kos · **Matricule :** 200 · **Centre :** KOFAKAM48

Application de gestion de présence par code, de dépôt d'exercices et de relecture
par les pairs, avec tableau récapitulatif pour le formateur.

**Frontend choisi : Next.js (React)**, parce qu'il combine React 19 (écrans clients
interactifs) et une structure de projet outillée (build TypeScript vérifié), ce qui
permet une couche API dédiée et trois écrans propres sans configuration superflue (F1).

## Structure

```
├── api/contrat.yaml               # Contrat API (5 opérations imposées + compléments)
├── docs/
│   ├── CAHIER_DES_CHARGES.md      # 10 sections, EF1-EF10, RG1-RG19
│   ├── JOURNAL.md                 # Journal de bord, une entrée par étape
│   ├── SOUMISSION.md              # Fichier de soumission
│   └── diagrammes/                # D1, D2, D3 + D4 bonus (Mermaid)
├── backend/                       # Spring Boot (Java 21, Maven, mvnw, Flyway)
├── frontend/kossi-app/            # Next.js 16 / React 19
└── docker-compose.yml             # db + backend + frontend
```

## Démarrage (une commande)

```bash
docker compose up --build
```

| Service  | URL                              | Note                                    |
| -------- | -------------------------------- | --------------------------------------- |
| Frontend | http://localhost:3010            | 3 écrans : formateur, étudiant, relecteur |
| Backend  | http://localhost:8090/api        | Swagger : http://localhost:8090/swagger-ui.html |
| Base     | PostgreSQL sur `db:5432`         | Adminer : http://localhost:8083         |

### Sans Docker (3 commandes)

```bash
# 1. base
docker compose up -d db

# 2. backend  (Flyway crée le schéma et charge les données de démo au démarrage)
cd backend && DB_URL='jdbc:postgresql://localhost:5437/kos' DB_USERNAME=kos DB_PASSWORD=kos ./mvnw spring-boot:run

# 3. frontend
cd frontend/kossi-app && cp .env.example .env.local && npm install && npm run dev
```

> Vérifié depuis un clone vierge avec Docker 29 / Java 21 / Node 20.
> Les tests d'intégration (`mvnw test`) démarrent leur propre PostgreSQL via
> Testcontainers : aucune base locale nécessaire, seulement Docker (B6).

## Données de démonstration (chargées par Flyway V2 + V4)

- 1 promotion : **KOFAKAM48-2026-1** (id 1)
- 10 étudiants (matricules 200-KOS à 209-NDI, promotion 1)
- 2 sessions de démonstration, codes **DEMOS001** et **DEMOS002** (expirant ~15 min après le démarrage)

Premier test rapide :

1. Ouvrir http://localhost:3010 → écran formateur → ouvrir une session → un code de 8 caractères s'affiche.
2. Écran étudiant → choisir un nom → saisir le code → présence enregistrée (201).
3. Déposer un exercice, puis écran relecteur → rendre une note : le tableau du formateur se met à jour (moyenne calculée par l'API, RG15).

## API principale

| Méthode | Chemin | Description |
|---------|--------|-------------|
| POST | /api/sessions | Ouvrir une session et obtenir un code (201) |
| POST | /api/presences | Marquer sa présence (201 · 400 CODE_INCONNU · 409 DEJA_PRESENT · 410 CODE_EXPIRE) |
| POST | /api/exercices | Déposer un exercice (201 · 400 LIEN_INVALIDE · 409 déjà déposé) |
| POST | /api/relectures/{id} | Rendre une relecture {note, commentaire} (200 · 400 NOTE_INVALIDE · 409 déjà rendue) |
| GET | /api/tableau?promotionId= | Tableau du formateur (200 · 404 PROMOTION_INCONNUE) |
| POST | /api/relectures/{exerciceId}/assignation | Assigner un relecteur au hasard parmi les présents (Q7) |
| PUT | /api/relectures/{id} | Corriger une relecture avant clôture (Q10) |
| POST | /api/presences/manual | Présence ajoutée par le formateur (source=FORMATEUR, Q14) |
| POST | /api/sessions/{id}/cloture | Clôturer une session (204) |

Contrat complet : [`api/contrat.yaml`](api/contrat.yaml) — format d'erreur imposé
`{ "code": "...", "message": "..." }` pour toutes les erreurs, sans exception (B4).

## Backend

- Java 21, Spring Boot 4, Maven, wrapper `mvnw` committé (B1)
- Couches contrôleur / service / repository, entités JPA jamais exposées (DTO partout, B3)
- Validation Bean Validation (`@Valid`) + `@RestControllerAdvice` : format d'erreur unique (B4)
- Schéma versionné Flyway : V1 schéma, V2 seed, V3 relectures assignées + unicité, V4 backfill (B5)
- Tests : `PresenceServiceImplTest` (unitaire, règles RG1/RG6/Q4) et
  `PresenceIntegrationTest` (Testcontainers PostgreSQL, codes du contrat) (B6)

## Frontend

- Next.js 16 / React 19, TypeScript strict (F1)
- 3 écrans : formateur (`/`), étudiant (`/etudiant`), relecteur (`/relecteur`) (F2)
- Couche API dédiée `src/lib/api.ts`, états de chargement et d'erreur partout,
  moyenne jamais recalculée côté client (F3)
