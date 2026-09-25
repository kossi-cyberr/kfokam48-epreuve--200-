# Cahier des charges — Épreuve finale fullstack KFOKAM48

**Auteur :** Jean Bertin Kos
**Matricule :** 200
**Centre :** KOFAKAM48
**Version :** 1
**Date :** 24/09/2026
**Frontend choisi :** React, parce que son écosystème de composants et de hooks permet une séparation claire entre la couche API et la vue, et il est simple à mettre en production côté serveur.

> Ce squelette est à remplir, pas à recopier. Les dix sections sont imposées et dans cet ordre.
> Tout ce qui reste en `<...>` ou en italique à la remise compte pour zéro.

---

## 1. Contexte et objectif

L'application répond au besoin de la formation KFOKAM48 de gérer les sessions de présence, la déposition des exercices des étudiants et leur relecture par des pairs, le tout à vue par le formateur. L'objectif est de remplacer une logique papier / e-mail partagée par un outil centralisé, avec un tableau de bord récapitulatif par promotion.

L'application est conçue pour être utilisée en salle de cours par un formateur et plusieurs étudiants, et en réunion par le formateur pour visualiser le tableau de bord de sa promotion.

---

## 2. Acteurs et rôles

| Acteur | Ce qu'il peut faire | Ce qu'il ne peut pas faire |
|---|---|---|
| Formateur | Ouvrir une session de présence (code), voir le tableau de bord, clôturer une session, ajouter une présence à la main | Jeu de présence d'un autre formateur, voir le nom du relecteur, exclure l'étudiant reçu |
| Étudiant | Saisir un code pour marquer sa présence, déposer le lien de son exercice, voir son propre historique (présences, exercices déposés, relectures en attente), recevoir une note et un commentaire sur son exercise relu | Marquer la présence d'un autre étudiant, relire son propre exercice, modifier un exercice déjà relu, voir la note d'un autre étudiant sauf si relecteur |
| Relecteur | Relire l'exercice d'un pair, donner une note (0-20) et un commentaire | Corriger sa propre relecture, modifier une relecture déjà validée si le formateur a clôturé la session |

---

## 3. Périmètre

**Inclus dans cette version :**
- Gestion des sessions de présence : ouverture, code valide, expiration 15 min, interdit après clôture.
- Marquage de présence : par étudiant (code) ou ajouté manuellement par le formateur (source = FORMATEUR), visible dans le tableau.
- Dépôt d'un exercice par étudiant pour une session : lien, statut, remplacement autorisé tant que personne n'a commencé la relecture.
- Attribution d'un relecteur au hasard parmi les étudiants présents à la session.
- Note de relecture : entière sur 20 (0-20), commentaire, modifiable tant que le formateur n'a pas clôturé la session.
- Tableau de bord par promotion : présence, nombre d'exercices déposés, moyenne, relectures en attente.

**Explicitement exclu :**
- Authentification / mot de passe pour des étudiants ou le formateur. L'étudiant choisit son identifiant dans une liste (Q1).
- Multi-formateur (une session appartient à un formateur unique).
- Partage de fichiers (lien vers un PDF, une image, un répertoire), seulement un lien unique.
- Notification par e-mail, push, SMS.
- Mode hors-ligne, journalisation avancée, export API, multi-langue.
- Gestion des comptes utilisateurs persistants : l'étudiant est identifié par son identifiant (prénom + nom + numéro d'inscription par exemple), pas de compte.

---

## 4. Exigences fonctionnelles

| Réf | Exigence | Critère d'acceptation | Priorité |
|---|---|---|---|
| EF1 | L'étudiant marque sa présence à l'aide d'un code | Quand je saisis un code valide et non expiré, ma présence apparaît dans le tableau du formateur | Must |
| EF2 | Un étudiant peut déposer le lien de son exercice pour une session | Quand je saisis un lien valide et que je n'ai pas déposé d'exercice pour cette session, le lien est enregistré avec le statut "en attente de relecture" | Must |
| EF3 | Un étudiant est assigné à la relecture d'un exercice pair | Quand un exercice est en attente de relecture, le système attribue un relecteur parmi les étudiants présents à la session | Must |
| EF4 | Un relecteur rend une note et un commentaire | Quand je valide une relecture, la note (0-20) et le commentaire sont enregistrés et le statut passe à "relu" | Must |
| EF5 | Le formateur voit un tableau récapitulatif | Quand je demande le tableau d'une promotion, j'obtiens pour chaque étudiant : présence, nombre d'exercices déposés, moyenne des notes reçues, relectures en attente | Must |
| EF6 | Le formateur peut ajouter une présence à la main | Quand je marque une présence manuellement, elle est visible avec la mention "ajouté par le formateur" | Must |
| EF7 | Le formateur peut clôturer une session | Quand je clôture une session, le code ne fonctionne plus, les étudiants ne peuvent plus déposer d'exercice, mais les étudiants peuvent encore marquer leur présence avant la clôture | Must |
| EF8 | Le formateur peut relire une relecture publiée avant la clôture | Quand le formateur n'a pas clôturé la session, le relecteur peut modifier sa note et son commentaire | Must |
| EF9 | La note est visible par l'étudiant relu mais pas par le nom du relecteur | Quand la relecture est validée et le formateur a clôturé la session, l'étudiant voit la note et le commentaire, mais ne voit pas qui a relu son exercice | Should |
| EF10 | L'exercice d'un étudiant est visible en "en attente" si le relecteur ne rend pas | Quand un exercice n'a pas de relecture, il est affiché comme "en attente" dans le tableau du formateur | Must |

---

## 5. Exigences non fonctionnelles

| Réf | Exigence | Comment on la vérifie |
|---|---|---|
| ENF1 | L'interface de marquage de présence est utilisable sur un téléphone | Sur un écran d'ordinateur de poche, le champ de code et le bouton de validation sont cliquables, pas besoin de zoom excessif |
| ENF2 | Le tableau du formateur répond en moins de 2 s pour une promotion de 60 étudiants | Fais une requête depuis un poste avec une promotion de 60 étudiants et vérifie l'absence de timeout |
| ENF3 | L'interface répond à une erreur proprement | Une erreur de présence (code invalide, expiré) est affichée à l'utilisateur sous forme de message lisible, pas d'erreur système |
| ENF4 | Le déploiement se fait en trois commandes maximum | Sur un poste vierge, `docker compose up` ou les trois commandes de l'installation documentées dans le README fonctionnent de manière répétée |
| ENF5 | La couverture des tests doit montrer la règle métier et l'endpoint | Un test unitaire sur une règle métier réelle et un test d'intégration sur un endpoint sont présents |

---

## 6. Règles de gestion

| Réf | Règle | Source |
|---|---|---|
| RG1 | Le code de présence expire 15 minutes après l'ouverture de la session | Q2 |
| RG2 | Un étudiant ne peut pas relire son propre exercice | Q5 |
| RG3 | Une note est un entier compris entre 0 et 20 | Q9 |
| RG4 | Un étudiant ne peut pas marquer la présence d'un autre étudiant | Q1 |
| RG5 | Un code de présence ne peut être utilisé qu'une seule fois | Q2, Q3 |
| RG6 | La présence d'un étudiant existe une seule fois par session | Q3 |
| RG7 | Un relecteur est choisi au hasard parmi les étudiants présents à la session | Q7 |
| RG8 | Deux relecteurs par exercice (enveloppe, étape 3) ; la note retenue est la moyenne des deux ; si un seul a rendu, la note est affichée mais marquée PROVISOIRE | Q6 + enveloppe |
| RG9 | Une relecture peut être modifiée tant que le formateur n'a pas clôturé la session | Q10 |
| RG10 | Une relecture validée reste définitif une fois le formateur clôturé la session | Q15 |
| RG11 | Un exercice reste "en attente" tant que le relecteur ne l'a pas relu | Q11 |
| RG12 | Un étudiant peut remplacer son lien d'exercice tant que personne n'a commencé la relecture | Q13 |
| RG13 | Un étudiant ne peut pas déposer un exercice après clôture de la session | Q3, Q12 |
| RG14 | La présence ajoutée manuellement est marquée "ajouté par le formateur" | Q14 |
| RG15 | La moyenne des notes affichée dans le tableau vient de l'API | F3 |
| RG16 | Le relecteur voit la note et le commentaire mais pas son identité | Q8 |

---

## 7. Zones d'ombre, hypothèses et contradictions

**Points que la demande ne tranche pas :**

| Point | Réponse client (Qx) ou hypothèse | Décision retenue | Conséquence |
|---|---|---|---|
| Sur quoi s'appuie l'étudiant ? | Q1 | L'étudiant est identifié par un identifiant libre (prénom + nom + matricule par exemple), pas de mot de passe | Pas de gestion de compte, pas de mot de passe à stocker |
| Qu'est-ce qui détermine la note moyenne d'un étudiant ? | Q16 | La moyenne des notes de ses relectures | Le calcul est fait côté API |
| Qu'est-ce que "relire" et qu'est-ce que "validé" ? | Q15 | Une relecture est "en attente" tant que le relecteur n'a pas validé | L'état "en attente" est affiché dans le tableau |
| Qu'est-ce qui se passe quand un étudiant quitte la salle ? | Hypothèse | Aucune déconnexion côté client, le code reste valide jusqu'à expiration | Principe de Q2 |

**Contradictions relevées :**

| Réponses en conflit | Ce que j'ai choisi | Pourquoi |
|---|---|---|
| Q10 (Oui, corriger avant clôture) / Q15 (Oui, définitive une fois envoyée) | Q10 l'emporte sur la saisie, Q15 l'emporte sur la lecture | Q10 décrit un usage réel du formateur : la correction est provisoire jusqu'à clôture. Q15 décrit la lecture définitive. On distingue donc "peut corriger avant clôture" (Q10) et "note définitive une fois le formateur clôturé" (Q15). |
| Q11 (reste "en attente" si le relecteur ne rend pas) / pas de réponse sur les relectures expirées | Hypothèse | On considère que le temps de relecture n'est pas limité, mais l'état "en attente" reste visible. |
| Q14 (présence ajoutée à la main) / pas de réponse sur le mot de passe | Hypothèse | L'ajout manuel est possible, mais il faut que ça se voie, marquez "ajouté par le formateur" | À toi de vérifier si c'est dans le périmètre |

**Trou identifié :**

Aucune des 16 questions ne précise le cycle de vie d'un exercice. Le sujet parle de "statut" dans le contrat, mais pas de l'évolution possible. On décide donc d'introduire un état "reçu" qui correspond à un exercice qui a été relu et dont la note a été validée par le formateur. Cela permet de distinguer un exercice "en attente", un exercice "relu", et un exercice "validé" (une fois le formateur clôturé la session).

---

## 8. Contraintes techniques

- **B1** : Java 17 ou plus, Maven, wrapper `mvnw` commité
- **B2** : Le contrat `api/contrat.yaml` est respecté à la lettre : chemins, verbes, codes de statut, format d'erreur
- **B3** : Séparation des couches contrôleur / service / repository. Aucune requête base dans un contrôleur, aucune entité JPA exposée en JSON — on passe par des DTO
- **B4** : Validation des entrées et gestion centralisée des erreurs (`@RestControllerAdvice`). Une stack trace renvoyée au client est une faute
- **B5** : Schéma versionné par Flyway ou Liquibase, migrations commitées. `ddl-auto=update` interdit hors tests
- **B6** : Deux tests qui prouvent quelque chose : un test unitaire sur une règle métier réelle, un test d'intégration sur un endpoint. Ils tournent sur un poste vierge, sans ta base locale
- **F1** : Framework déclaré et justifié en une ligne dans le `README`, et le build passe
- **F2** : Trois écrans : formateur (ouvrir une session, voir le tableau), étudiant (marquer sa présence, déposer son exercice), relecteur (faire une relecture)
- **F3** : Appels API dans une couche dédiée, pas de `fetch` dispersé · états de chargement et d'erreur gérés · aucune règle métier dupliquée : la moyenne affichée vient de l'API, tu ne la recalcules pas

---

## 9. Livrables

- `docs/CAHIER_DES_CHARGES.md` : cahier des charges rempli (10 sections)
- `docs/diagrammes/` : 3 diagrammes Mermaid (D1, D2, D3) + bonus si disponible
- `api/contrat.yaml` : contrat complet, figé avant le premier commit de code
- `docs/` : backlog en issues (GitHub)
- Backend Spring Boot (Java 17+, Maven, wrapper `mvnw` commité)
- Frontend React (ou Angular/Next.js)
- `docs/JOURNAL.md` : journal de bord, une entrée par étape
- `docs/SOUMISSION.md` : fichier de soumission
- Tous les commits poussés sur GitHub le dépôt `kfokam48-epreuve-<matricule>`

---

## 10. Démarche prévue

La démarche est la suivante, dans l'ordre :

1. **Analyse et conception** (étape 1) : cahier des charges, diagrammes, backlog en issues, contrat complété. Commit `[JALON] analyse`.
2. **Première version** (étape 2) : implémentation des tickets Must, commit `[JALON] v0.1`. C'est le moment où l'enveloppe est ouverte, si elle est fournie.
3. **Enveloppe** (étape 3) : correction du bug signalé par le client et intégration du changement de besoin. Update du cahier des charges et des diagrammes.
4. **Version finale** (étape 4) : commit `[JALON] v1.0`, `CHANGELOG.md`, README testé à partir d'un clone vierge, backlog trié.
5. **Épreuve Git** (étape 5) : résolution des 5 situations de l'épreuve Git, sur un second dépôt `kfokam48-gitlab-<matricule>`.
6. **Soumission** (étape 6) : remplissage de `docs/SOUMISSION.md` et téléversement sur la plateforme avant 18h00.

**Definition of Done — un ticket est terminé quand :**
- Les critères d'acceptation sont vérifiés.
- Le ticket est lié à une issue GitHub.
- La migration (si changement de base) est commitée et versionnée.
- Le code est testé.
- Le ticket est clôturé dans l'ordre de la branche.

---

## Journal des révisions

| Version | Quand | Ce qui a changé et pourquoi |
|---|---|---|
| 1 | 24/09/2026 | Version initiale |
