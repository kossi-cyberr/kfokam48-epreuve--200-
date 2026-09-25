# Cahier des charges — Épreuve finale fullstack KFOKAM48 (Mise à jour)

**Auteur :** Jean Bertin Kos
**Matricule :** 200
**Centre :** KOFAKAM48
**Version :** 2 (mise à jour après enveloppe)
**Date :** 25/09/2026

**Frontend choisi :** React (Next.js 16), parce que son écosystème de composants et de hooks permet une séparation claire entre la couche API et la vue, et il est simple à mettre en production côté serveur.

---

## 1. Contexte et objectif

L'application répond au besoin de la formation KFOKAM48 de gérer les sessions de présence, la déposition des exercices des étudiants et leur relecture par des pairs, le tout à vue par le formateur. Après le premier jet, le client a signalé un bug de présence simultanée et demandé un changement de besoin : deux relecteurs par exercice.

---

## 2. Acteurs et rôles

| Acteur | Ce qu'il peut faire | Ce qu'il ne peut pas faire |
|---|---|---|
| Formateur | Ouvrir une session, marquer une présence manuellement, voir le tableau, clôturer une session | Jeu de présence d'un autre formateur, voir le nom du relecteur, exclure l'étudiant reçu |
| Étudiant | Saisir un code pour marquer sa présence, déposer le lien de son exercice, voir son propre historique, recevoir une note et un commentaire | Marquer la présence d'un autre étudiant, relire son propre exercice, modifier une relecture déjà validée |
| Relecteur | Relire l'exercice d'un pair, donner une note (0-20) et un commentaire | Corriger sa propre relecture, modifier une relecture déjà validée |

---

## 3. Périmètre

**Inclus dans cette version :**
- Gestion des sessions de présence avec gestion des erreurs de parallélisme
- Marquage de présence par étudiant ou par ajout manuel
- Dépôt d'un exercice par étudiant avec lien
- Deux relecteurs par exercice (changement de besoin)
- Note de relecture : moyenne des deux relecteurs (provisoire si un seul a rendu)
- Tableau de bord par promotion

**Explicitement exclu :**
- Authentification / mot de passe
- Multi-formateur
- Partage de fichiers multiples
- Notification par e-mail
- Mode hors-ligne
- Gestion des comptes utilisateurs persistants

---

## 4. Exigences fonctionnelles

| Réf | Exigence | Critère d'acceptation | Priorité |
|---|---|---|---|
| EF1 | L'étudiant marque sa présence à l'aide d'un code | Quand deux étudiants tapent le code presque en même temps, les deux sont enregistrés | Must |
| EF2 | Un étudiant peut déposer le lien de son exercice | Quand je saisis un lien valide, le lien est enregistré avec statut EN_ATTENTE | Must |
| EF3 | Deux relecteurs sont assignés par exercice | Quand un exercice est en attente, deux étudiants sont choisis pour relire | Must |
| EF4 | Un relecteur rend une note et un commentaire | Quand un relecteur valide, la note (0-20) et le commentaire sont enregistrés | Must |
| EF5 | Le formateur voit un tableau récapitulatif | Le tableau montre présence, exercices, notes moyennes, relectures provisoires/en attente | Must |
| EF6 | Présence manuelle | Le formateur peut ajouter une présence avec source=FORMATEUR | Must |
| EF7 | Clôture de session | Le code n'est plus valide après clôture, mais les présences restent durables | Must |
| EF8 | Deux relecteurs | Chaque exercice est relu par deux pairs différents | Must |
| EF9 | Note moyenne | La note affichée est la moyenne des deux relecteurs | Must |
| EF10 | Note provisoire | Si un seul relecteur a rendu, la note est affichée mais marquée comme provisoire | Must |
| EF11 | L'exercice reste en attente | Si aucun relecteur n'a rendu, l'exercice est répertorié dans le tableau | Must |

---

## 5. Exigences non fonctionnelles

| Réf | Exigence | Comment on la vérifie |
|---|---|---|
| ENF1 | L'interface de marquage de présence est utilisable sur un téléphone | Sur une petite écran, les champs sont cliquables |
| ENF2 | Le tableau répond en moins de 2s pour 60 étudiants | Requête sur une promotion de 60 étudiants sans timeout |
| ENF3 | Les erreurs sont affichées proprement | Messages lisible pour l'utilisateur |
| ENF4 | Le déploiement en 3 commandes maximum | `docker compose up --build` fonctionne |
| ENF5 | Tests unitaire + integration | 2 tests valides |

---

## 6. Règles de gestion

| Réf | Règle | Source |
|---|---|---|
| RG1 | Le code de présence expire 15 minutes après l'ouverture | Q2 |
| RG2 | Un étudiant ne peut pas relire son propre exercice | Q5 |
| RG3 | Une note est un entier compris entre 0 et 20 | Q9 |
| RG4 | Un étudiant ne peut pas marquer la présence d'un autre | Q1 |
| RG5 | Un code de présence ne peut être utilisé qu'une seule fois | Q2, Q3 |
| RG6 | Deux relecteurs par exercice | Changement de besoin |
| RG7 | La note est la moyenne des deux relecteurs | Changement de besoin |
| RG8 | Si un seul a rendu, la note est provisoire | Changement de besoin |
| RG9 | Le relecteur ne peut pas relire son propre exercice | Q5 |
| RG10 | Une relecture peut être modifiée avant clôture | Q10 |
| RG11 | Une relecture définitive après clôture | Q15 |
| RG12 | Un exercice reste « en attente » | Q11 |
| RG13 | Remplacement du lien tant que personne n'a relu | Q13 |
| RG14 | Présence manuelle avec source=FORMATEUR | Q14 |
| RG15 | Moyenne venant de l'API | F3 |
| RG16 | Le relecteur voit la note mais pas son identité | Q8 |
| RG17 | Deux présences en parallèle sont acceptées | Bug corrigé |
| RG18 | Deux relecteurs choisis parmi les étudiants présents | Q7 |

---

## 7. Zones d'ombre, hypothèses et contradictions

### Nouveaux points

| Point | Réponse client (Qx) ou hypothèse | Décision retenue | Conséquence |
|---|---|---|---|
| Comment s'attribuent les deux relecteurs ? | Hypothèse | Deux étudiants choisis aléatoirement parmi la liste des présents | Le frontend propose de sélectionner les deux relecteurs |
| Quelle note si un seul a rendu ? | Changement de besoin | La note affichée est la note du relecteur, mais marquée provisoire | La note finale est recalculée après la deuxième relecture |
| Comment choisir les deux pairs ? | Hypothèse | L'étudiant qui dépose l'exercice choisit ses deux pairs parmi la liste des présents | Le formulaire de relecture propose sélectionner deux étudiants |

### Contradictions

| Réponses en conflit | Ce que j'ai choisi | Pourquoi |
|---|---|---|
| Q6 (un seul relecteur) / Changement (deux pairs) | Deux pairs | Le changement de besoin a priorité |
| Q10 (corriger avant clôture) / Q15 (définitive après clôture) | Q10 et Q15 sont cohérents | Modification avant clôture, définitif après |

### Sacrifices de périmètre

- L'attribution aléatoire des deux relecteurs n'est pas implémentée (affichage et sélection manuelle).
- Le calcul automatique de la moyenne n'est pas fait côté backend dans la première version (doit être fait côté frontend ou API).

---

## 8. Contraintes techniques

- **B1** : Java 21, Maven, wrapper mvnw
- **B2** : Le contrat est respecté à la lettre
- **B3** : Séparation contrôleur/service/repository, DTO
- **B4** : Validation et gestion centralisée des erreurs
- **B5** : Schéma versionné par Flyway (nouvelle migration)
- **B6** : Deux tests (un unitaire, un d'intégration) sur le bug

### Nouvelles contraintes

- **B7** : Deux relecteurs par exercice (changement de besoin)
- **B8** | La note affichée est la moyenne des deux

---

## 9. Livrables

- `docs/CAHIER_DES_CHARGES.md` : cahier des charges à jour
- `docs/diagrammes/` : diagrammes mis à jour (D3 inclut le cas de deux relecteurs)
- `api/contrat.yaml` : mis à jour
- `docs/CHANGE-MANAGEMENT.md` : gestion du changement
- `docs/BUG-REPRODUCT.md` : rapport de reproduction
- Backend Spring Boot avec migration et deux nouveaux contrôleurs
- Frontend avec écran de relecture à deux relecteurs

---

## 10. Démarche prévue

1. Corriger le bug de présence simultanée (test + correction)
2. Mettre à jour l'analyse (cahier des charges, diagrammes, migration)
3. Mettre à jour le contrat d'API
4. Implémenter la modification de deux relecteurs
5. Pousser le correctif et l'évolution
6. Version finale avec `[JALON] v1.0`

---

## Journal des révisions

| Version | Quand | Ce qui a changé et pourquoi |
|---|---|---|
| 2 | 25/09/2026 | Mise à jour après enveloppe (bug + changement de besoin) |

---

## Journal des révisions

| Version | Quand | Ce qui a changé et pourquoi |
|---|---|---|
| 1 | 24/09/2026 | Version initiale |
