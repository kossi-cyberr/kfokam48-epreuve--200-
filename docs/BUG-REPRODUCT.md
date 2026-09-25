# Bug : Deux étudiants ne peuvent pas marquer leur présence simultanément

## Description du bug

Un formateur a ouvert une session avec deux étudiants côte à côte. Les deux étudiants ont tapé le code de présence presque en même temps. Seul un des deux étudiants apparaît dans la liste des présences du formateur.

Le formateur a réessayé une fois, cette fois les deux étudiants sont passés. Seul le premier essaie a réussi.

## Étapes pour reproduire

1. Ouvrir une session (POST /api/sessions)
2. Avoir deux étudiants avec des identifiants différents
3. Les deux étudiants saisissent le code dans le même temps (parallèle)
4. Vérifier la présence dans le tableau du formateur (GET /api/tableau?promotionId=)

## Comportement attendu

Les deux étudiants doivent pouvoir marquer leur présence, même en parallèle.

## Comportement actuel

Un seul étudiant apparaît dans la liste.

## Impact

L'absence de présence d'un étudiant signifie qu'il est incapable de déposer son exercice. Cela a un impact direct sur le tableau du formateur.

## Statut

- **Bug signalé** : Oui
- **Test échouant** : À créer
- **Correction** : À appliquer
- **Issue** : OUVERTE
- **Commit** : À pousser

---

*Document créé dans le cadre de l'étape 3 - enveloppe de l'épreuve KFOKAM48*
