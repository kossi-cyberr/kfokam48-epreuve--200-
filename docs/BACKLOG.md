# Backlog — Épreuve finale fullstack KFOKAM48

Ce backlog est organisé par priorité (Must / Should / Could) et couvre l'étape 2 (Première version).

## Must — Tout déployer

| # | Titre | Critères d'acceptation | Priorité | Réf |
|---|---|---|---|---|
| 1 | Ouvrir une session et obtenir un code de présence | Quand le formateur appelle POST /api/sessions avec {titre, promotionId}, il obtient un code de 8 caractères et la date d'expiration dans les 15 minutes | Must | EF1 |
| 2 | Marquer sa présence avec un code | Quand un étudiant saisit un code valide et non expiré, sa présence est enregistrée avec source=ETUDIANT | Must | EF1 |
| 3 | Voir le tableau du formateur | Quand le formateur appelle GET /api/tableau?promotionId=1, il obtient la liste des étudiants avec présence, exercices déposés, moyenne, relectures en attente | Must | EF5 |
| 4 | Déposer un lien d'exercice | Quand un étudiant dépose un lien valide pour une session, l'exercice est enregistré avec statut=EN_ATTENTE | Must | EF2 |
| 5 | Remplacer le lien d'un exercice | Quand un étudiant remplace son lien tant que personne n'a commencé la relecture, le lien est mis à jour | Must | EF13 |
| 6 | Ajouter une présence à la main | Quand le formateur ajoute une présence manuellement, elle est visible avec source=FORMATEUR | Must | EF6 |
| 7 | Clôturer une session | Quand le formateur clôture une session, le code ne fonctionne plus et les étudiants ne peuvent plus déposer d'exercice | Must | EF7 |
| 8 | Relire un exercice | Quand un relecteur rend une note (0-20) et un commentaire, l'exercice passe en statut REVU | Must | EF4 |
| 9 | Voir les relectures en attente | Dans le tableau, l'étudiant voit le nombre de relectures qui lui restent à faire | Must | EF10 |

## Should — Agréments

| # | Titre | Critères d'acceptation | Priorité | Réf |
|---|---|---|---|---|
| 10 | Attribuer aléatoirement un relecteur | Quand un exercice est en attente, le système attribue un relecteur parmi les étudiants présents à la session | Should | EF3 |
| 11 | Voir son propre exercice relu | Quand un étudiant a un exercice relu, il voit la note et le commentaire mais pas le nom du relecteur | Should | EF8 |

## Could — Améliorations

| # | Titre | Critères d'acceptation | Priorité | Réf |
|---|---|---|---|---|
| 12 | Enlever la présence d'un étudiant | Le formateur peut supprimer une présence, elle disparaît du tableau | Could | — |
| 13 | Voir l'historique de ses présences | Un étudiant voit ses présences passées | Could | — |