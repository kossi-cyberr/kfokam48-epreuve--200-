# Journal de bord — 200 Kos Jean Bertin

> Une entrée **par étape**, écrite **au moment où tu la termines**, pas à la fin de la journée.
> Trois lignes suffisent. Un journal rédigé d'un bloc juste avant de soumettre se repère
> immédiatement dans l'historique Git et ne compte pas.

Chaque entrée répond aux trois mêmes questions :

- **Fait** — ce que tu viens de terminer
- **Bloqué** — ce qui t'a coûté du temps, et combien
- **IA** — ce que tu lui as demandé, et **comment tu as vérifié sa réponse**

---

## Étape 1 — Analyse et conception

**Fait :**
- Cahier des charges complet (10 sections, 10 exigences fonctionnelles, 19 règles de gestion)
- 3 diagrammes Mermaid (D1 cas d'utilisation, D2 classes, D3 séquence) + D4 bonus états-transitions
- Contrat d'API complété (5 opérations imposées + opérations complémentaires)
- 15 issues créées sur GitHub
- Commit `[JALON] analyse` poussé

**Bloqué :**
- 12 min sur la contradiction Q10/Q15 : Q10 décrit un usage réel (correction provisoire), Q15 décrit la lecture définitive. Tranché : correction possible avant clôture, définitive après.
- 8 min sur le trou : le cycle de vie d'un exercice n'est défini nulle part. Décidé les états EN_ATTENTE → RELU → VALIDEE.

**IA :**
- M'a proposé des tickets techniques supplémentaires (création des entités, config Flyway). Je les ai écartés du backlog : ce ne sont pas des résultats utilisateur. Vérifié en relisant chaque titre : « est-ce que le client le comprendrait ? ».

---

## Étape 2 — Première version

**Fait :**
- Backend Spring Boot : 5 opérations du contrat + assignation de relecteur, clôture, présences manuelles
- Migrations Flyway V1 (schéma) + V2 (données de démo) ; entités, DTOs, gestion d'erreur centralisée
- Commit `[JALON] v0.1` poussé

**Bloqué :**
- 15 min : deux migrations V1 coexistaient (V1__create_items.sql du socle + V1__create_kos_tables.sql) — Flyway refusait de démarrer. Supprimé l'ancienne.
- 20 min : le statut RELU n'était jamais appliqué à l'exercice après une relecture — corrigé dans le service de relectures.

**IA :**
- L'IA a généré le squelette des services ; j'ai vérifié chaque règle métier contre le CLIENT.md (codes de statut du contrat, RG1 à RG6) et testé les endpoints au curl avant de pousser.

---

## Étape 3 — Enveloppe

**Fait :**
- Enveloppe reçue après le push de `[JALON] v0.1` : bug signalé + changement de besoin traités dans des commits séparés (correctif vs évolution), migration versionnée V3, contrat mis à jour, cahier des charges et diagrammes corrigés (version 1.1)

**Bloqué :**
- Précisé dans le CHANGELOG et les commits correspondants.

**IA :**
- Voir l'entrée de l'étape 4 : la correction du socle a été pilotée par vérification systématique au curl.

**Ce que j'ai sorti du périmètre pour absorber le changement, et pourquoi :**
- Les tickets Could (#12 suppression de présence, #13 historique de présences) restent ouverts : la conformité au contrat et les règles Q4/Q10 passaient avant.

---

## Étape 4 — Version finale

**Fait :**
- Socle réparé : compilation (champ dupliqué, imports `@PrePersist`, injection manquante), double migration V1 supprimée, seed corrigé (codes ≤ 10 caractères), V3 (relectures assignées + unicité RG6) et V4 (backfill promotion)
- GET /api/tableau implémenté (opération imposée n°5) ; toutes les erreurs au format {code, message} avec les codes HTTP du contrat (400/403/409/410)
- Q4 : blocage après 5 codes erronés (RG17) ; Q10 : correction de relecture avant clôture ; assignation aléatoire du relecteur parmi les présents (RG7)
- Frontend Next.js reconstruit (3 écrans, couche API dédiée) — l'ancien dépôt était un gitlink fantôme sans contenu
- Tests B6 : 4 unitaires (RG1, RG6, Q4, cas nominal) + 4 d'intégration Testcontainers (201/400/410/409) — tous verts
- CHANGELOG, README testé, backlog trié, commit `[JALON] v1.0`

**Bloqué :**
- 25 min : Spring Boot 4 déplace `@AutoConfigureMockMvc` dans `org.springframework.boot.webmvc.test.autoconfigure` — test d'intégration ne compilait pas.
- 10 min : résidu de build (`target/classes`) contenait encore la vieille migration V1 — nettoyé avant redémarrage.
- 15 min : le tableau renvoyait une liste vide — les étudiants du seed n'avaient pas de promotion_id (colonne ajoutée en V3 après le V2) ; corrigé par V4.

**IA :**
- Demande : réparer la compilation, conformiser les codes HTTP, écrire les tests, générer le frontend.
- Vérification : compilation `mvnw compile`, scénario complet au curl (15 vérifications : 201/400/409/410/403/204, format d'erreur, tableau), `mvnw test` (8 tests verts), `npm run build` (TypeScript strict, 3 routes générées). Chaque affirmation de l'IA confrontée au contrat api/contrat.yaml et au CLIENT.md ; deux écarts trouvés et corrigés (le blocage Q4 non branché, le GET relecteur qui ne renvoyait que les relectures en attente).

---

## Étape 5 — Soumission

**Fait :**
- SOUMISSION.md complété (hash du commit final, dépôts vérifiés en navigation privée)

**Bloqué :**
- —

**Ce que je referais autrement avec une journée de plus :**
- Une branche + une PR par issue dès l'étape 2 (l'historique est allé trop vite sur master)
- Un écran « connexion par sélection » partagé entre étudiant et relecteur plutôt qu'un localStorage
- Des tests sur les autres règles (RG12 remplacement de lien, RG7 tirage au sort)

---

## Étape 6 — Soumission

**Fait :**

**Ce que je referais autrement avec une journée de plus :**
