# kos — socle fullstack (Next.js · Spring Boot · PostgreSQL)

Socle prêt pour l'**épreuve fullstack** : le domaine métier reste à définir dès que le sujet
est connu — tout le reste (structure, Docker, Swagger, base, CORS, erreurs, tests) est en place.

## Démarrage

```bash
docker compose up --build
```

| Service   | URL                                       | Identifiants                       |
| --------- | ----------------------------------------- | ---------------------------------- |
| Front     | http://localhost:3000                     | —                                  |
| Swagger   | http://localhost:8080/swagger-ui.html     | —                                  |
| Adminer   | http://localhost:8081                     | serveur `db`, base `kos`, mdp `kos` |
| API       | http://localhost:8080/api/items           | —                                  |
| Health    | http://localhost:8080/actuator/health     | —                                  |

Variables surchargeables : copier `.env.example` en `.env`.

### Sans Docker (développement)

```bash
# 1. base (seule)
docker compose up -d db adminer

# 2. backend
cd backend && ./mvnw spring-boot:run

# 3. front
cd frontend/kossi-app && cp .env.example .env.local && npm run dev
```

### Tests

```bash
cd backend && ./mvnw test          # PostgreSQL en Testcontainers (Docker requis)
cd frontend/kossi-app && npm run lint && npm run build
```

## Architecture

```
examen/
├── docker-compose.yml            # 4 services : db, adminer, backend, frontend
├── .env.example                  # variables de configuration (copier en .env)
├── backend/                      # API Spring Boot 4 / Java 21
│   ├── Dockerfile                # multi-stage : Maven → JRE 21 (non-root)
│   └── src/main/
│       ├── java/com/kfokam/kos/
│       │   ├── config/           # CorsConfig, SecurityConfig, OpenApiConfig
│       │   ├── controller/       # REST : validation + délégation au service
│       │   ├── service/          # logique métier (interface + impl/)
│       │   ├── repository/       # Spring Data JPA
│       │   ├── model/            # entités JPA
│       │   ├── dto/              # records de requête/réponse (jamais d'entité exposée)
│       │   └── exception/        # GlobalExceptionHandler + ApiError standardisé
│       └── resources/
│           ├── application.properties   # tout est sur variables d'environnement
│           └── db/migration/     # Flyway (V1__create_items.sql) = schéma
└── frontend/kossi-app/           # Next.js 16 / React 19 / Tailwind 4
    ├── Dockerfile                # multi-stage → output standalone
    ├── app/                      # App Router : page (accueil), items/ (démo CRUD)
    ├── components/               # composants "use client"
    └── lib/
        ├── api.ts                # client HTTP typé (erreurs, futur JWT)
        ├── server.ts             # fetch côté serveur (Server Components)
        └── types.ts              # types miroir des DTO
```

### Choix d'architecture

- **Backend en couches** (`controller → service → repository → model`) : chaque couche ne
  parle qu'à la suivante, les DTO protègent le modèle interne, la validation est déclarative
  (Bean Validation sur les DTO), toutes les erreurs passent par un `@RestControllerAdvice`
  qui renvoie un format `ApiError` unique.
- **Flyway seule source de vérité du schéma** : Hibernate est en `ddl-auto=validate`,
  il ne crée ni ne modifie jamais la base. Une évolution = une nouvelle migration `V2__…`.
- **Sécurité prête, mais ouverte** : `SecurityConfig` est en stateless/CSRF off avec
  Swagger et le healthcheck ouverts ; il ne reste qu'à brancher le filtre JWT et passer
  `anyRequest().authenticated()` (les TODO sont dans le fichier).
- **Front hybride** : les listes sont chargées en **Server Component** (SSR, `force-dynamic`),
  les mutations partent du navigateur via `lib/api.ts`. Deux variables d'URL :
  `NEXT_PUBLIC_API_URL` (navigateur, figée au build) et `API_URL` (serveur Next, réseau Docker).
- **Docker** : images multi-stage non-root, healthchecks, `depends_on: service_healthy`
  pour l'ordre de démarrage (db → backend → frontend), volume persistant pour la base.

## Checklist « le sujet est arrivé »

1. **Modéliser** : entités JPA + migration `V2__<sujet>.sql` + DTO (record `XxxRequest` / `XxxResponse`).
2. **API** : `XxxService` (interface) + `XxxServiceImpl` + `XxxController` annoté `@Tag`/`@Operation` → Swagger se met à jour tout seul.
3. **Front** : types dans `lib/types.ts`, appels via `lib/api.ts`, page dans `app/<sujet>/page.tsx` (données initiales en serveur, formulaires en client component).
4. **Auth** (si le sujet l'exige) : `JwtAuthenticationFilter` + `AuthenticationProvider`, remonter les rôles, remplacer `permitAll()` dans `SecurityConfig`, stocker le token côté front dans `lib/api.ts` (le point d'insertion est déjà commenté).
5. **Tests** : `@SpringBootTest` avec Testcontainers (déjà en place) + tests unitaires du service (Mockito).
