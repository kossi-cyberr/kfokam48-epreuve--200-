# kos — Épreuve finale fullstack KFOKAM48

**Auteur :** Jean Bertin Kos
**Matricule :** 200
**Centre :** KOFAKAM48

Socle prêt pour l'**épreuve finale KFOKAM48** : présence, dépôt d'exercices et relecture par pairs, tableau du formateur.

## Structure imposée

```
examen/
├── api/
│   └── contrat.yaml              # Contrat API imposé (5 opérations + compléments)
├── docs/
│   ├── CAHIER_DES_CHARGES.md     # Cahier des charges
│   ├── JOURNAL.md                # Journal de bord
│   ├── SOUMISSION.md             # Fichier de soumission
│   └── diagrammes/               # D1, D2, D3 (Mermaid)
├── backend/                      # Spring Boot (Java 21, Maven, Flyway)
├── frontend/                     # Next.js 16 / React 19
└── docker-compose.yml            # Base + backend + frontend
```

## Démarrage

```bash
docker compose up --build
```

| Service   | URL                                       | Accès     |
| --------- | ----------------------------------------- | --------- |
| Base de données | PostgreSQL sur `db:5432`              | `kos` / `kos` |
| Backend   | http://localhost:8080/api                 | Swagger : `http://localhost:8080/swagger-ui.html` |
| Frontend  | http://localhost:3000                     | —         |

### Sans Docker

```bash
# 1. base
docker compose up -d db

# 2. backend
cd backend && ./mvnw spring-boot:run

# 3. front
cd frontend/kossi-app && cp .env.example .env.local && npm run dev
```

## API principale

| Méthode | Chemin | Description |
|---------|--------|-------------|
| POST | /api/sessions | Ouvrir une session et obtenir un code |
| POST | /api/presences | Marquer sa présence |
| POST | /api/exercices | Déposer un exercice |
| POST | /api/relectures | Rendre une relecture |
| GET | /api/tableau?promotionId= | Tableau du formateur |
| GET | /api/sessions | Lister les sessions |
| POST | /api/presences/{id}/manual | Ajouter une présence manuelle |
| POST | /api/sessions/{id}/clôture | Clôturer une session |

## Données de démonstration

Au démarrage, la base est initialisée avec :
- 1 promotion (KOFAKAM48-2026-1)
- 10 étudiants (matricules 200-209)
- 2 sessions de démonstration (codes : `KFOKAM48-001`, `KFOKAM48-002`)

## Barème (100 pts)

| Critère | Pts |
|---------|-----|
| Analyse et conception | 38 |
| Conduite du changement | 10 |
| Git (incluant l'épreuve git-lab) | 32 |
| Produit et conformité | 15 |
| Journal | 5 |
