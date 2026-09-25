# Soumission — Épreuve finale fullstack KFOKAM48

> Vérifie ton lien depuis une fenêtre de navigation privée,
> puis téléverse ce fichier sur la plateforme **avant 18h00**.
> Sans ce dépôt sur la plateforme, tu n'as rien rendu.

---

## Candidat

| | |
|---|---|
| Nom et prénom(s) | Jean Bertin KOS |
| Matricule | KF48-200 |
| Centre | Yaoundé |
| Compte GitHub | kossi-cyberr |

## Projet

| | |
|---|---|
| Dépôt (public) | https://github.com/kossi-cyberr/kfokam48-epreuve--200- |
| Commit final — hash complet, 40 caractères | 9c98ad588bdf985f650328cd8690f929f55cad93 |
| Branche | master |

## Technique

| | |
|---|---|
| Frontend utilisé | Next.js 16 (React 19) — TypeScript |
| Base de données | PostgreSQL 17 (Docker) ; tests d'intégration sur PostgreSQL via Testcontainers |
| Commande de démarrage | `docker compose up --build` (ou 3 commandes sans Docker, voir README) |

## Ce que j'ai livré

- **Analyse (38 pts)** : `docs/CAHIER_DES_CHARGES.md` v1.1 (10 sections, EF1-EF10, RG1-RG19,
  contradiction Q10/Q15 tranchée, trou du cycle de vie identifié), 4 diagrammes Mermaid
  (D1 cas d'utilisation, D2 classes aligné sur les migrations, D3 séquence présence avec
  codes HTTP du contrat, D4 bonus états-transitions), 17 issues (17 fermées, 4 ouvertes
  comme backlog restant trié), contrat `api/contrat.yaml` 1.2 figé avant le code.
- **Backend (B1-B6)** : Spring Boot 4 / Java 21, couches contrôleur-service-repository avec
  DTO (B3), `@Valid` + `@RestControllerAdvice` au format `{code, message}` (B4), 5 migrations
  Flyway versionnées (B5), 8 tests (4 unitaires RG1/RG6/Q4 + 4 intégration Testcontainers, B6).
  Les 5 opérations imposées respectent l'annexe B : 201/400/409/410, POST `/api/relectures/{id}`.
- **Frontend (F1-F3)** : Next.js 16 / React 19, 3 écrans (formateur avec tableau et badge
  « provisoire », étudiant présence + dépôt, relecteur rendre/corriger), couche API dédiée
  `src/lib/api.ts`, moyenne jamais recalculée côté client (RG15).
- **Enveloppe (étape 3)** : bug de présence concurrente corrigé (unicité en base + 409),
  évolution « 2 relecteurs, note = moyenne, statut PROVISOIRE/RELEVE » livrée en 3 étapes
  tracées (issues #41, #42 ; PR #31, #40 ; migration V5).
- **Journal** : `docs/JOURNAL.md`, une entrée par étape, avec le temps perdu et la
  vérification systématique des réponses de l'IA (scénario curl de 15 points, 8 tests).

## Avant de téléverser, vérifie

- [x] Mon dépôt est **public** et s'ouvre en navigation privée
- [x] Le hash fait bien **40 caractères** et existe sur GitHub
- [x] Tout mon travail est **poussé** — `git status` est propre
- [x] Mon `README` a été testé depuis un clone vierge (docker compose up vérifié)
- [x] Mon `JOURNAL.md` et mon cahier des charges sont dans `docs/`
- [x] Les trois commits `[JALON]` sont poussés et dans le bon ordre

---

**Déclaration.** J'ai réalisé ce travail seul. Les outils d'IA étaient autorisés sans restriction et je les ai utilisés ; mon journal indique où et comment j'ai vérifié leurs réponses. Mon dépôt restera public et inchangé jusqu'à la publication des résultats.

Signature : Jean Bertin KOS (copie téléversée électroniquement)  Date : 25/09/2026
