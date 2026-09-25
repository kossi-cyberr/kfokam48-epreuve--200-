# Changement de besoin — Épreuve KFOKAM48

## Contexte

Le client a testé la première version et est revenue avec deux demandes :

### 1. Bug signalé
> « J'ai ouvert une session ce matin avec deux étudiants côte à côte. Ils ont tapé le code presque en même temps et il n'y en a qu'un seul qui apparaît dans ma liste. »

### 2. Changement de besoin
> « Finalement, un seul relecteur ça ne marche pas : quand il ne rend rien, l'étudiant n'a aucune note. À partir de maintenant, chaque exercice est relu par deux pairs différents, et la note retenue est la moyenne des deux. Si un seul des deux a rendu, on affiche sa note en attendant, mais marquée comme provisoire. »

---

## Impact du changement

### 1. Base de données
- L'entité `Relecture` doit supporter **deux relecteurs par exercice**
- La division `Statut` de l'exercice doit être mise à jour :
  - `EN_ATTENTE` : aucun relecteur n'a rendu
  - `PROVISOIRE` : un seul relecteur a rendu (note affichée mais marquée provisoire)
  - `RELEVE` : les deux relecteurs ont rendu, note est la moyenne

### 2. Contrat d'API
- `POST /api/relectures` : le corps de la requête doit accepter `relecteurId` (algorithme d'attribution) — jusqu'à 2 relecteurs par exercice
- La réponse de `GET /api/tableau` doit inclure les notes des deux relecteurs

### 3. Frontend
- L'écran de relecture doit permettre de sélectionner jusqu'à 2 relecteurs
- L'écran du tableau doit afficher les notes des deux relecteurs et indiquer si elles sont provisoires ou définitives

---

## Règles de gestion modifiées

### RG6 — Un seul relecteur par exercice → supprimé
**Ancienne règle** : « Un seul relecteur par exercice »

**Nouvelle règle** : « Deux relecteurs par exercice. La note retenue est la moyenne des deux. Si un seul a rendu, la note est affichée mais marquée comme provisoire. »

**Source** : Changement de besoin du client

### RG16 — Le relecteur voit la note et le commentaire mais pas son identité
**Modifiée** : Lorsqu'il y a deux relecteurs, les deux doivent être masqués (la note est affichée avec « Note provisoire » si seulement un a rendu)

---

## Zon d'ombre

### Les deux relecteurs
- Qui sont-ils ? : Les deux étudiants présents à la session, choisis aléatoirement, pas d'ordre de priorité.
- Quelle note ? : La moyenne des deux notes (arrondie à l'entier le plus proche).
- Si un seul a rendu ? : On affiche la note de celui qui a rendu, mais elle est marquée comme « provisoire ». Le formulaire de relecture reste ouvert jusqu'à ce que les deux aient rendu.

### Implémentation
- La modification est réalisée en **deux commit** distincts :
  1. Le **correctif** du bug (présence parallèle) — sans modification de la base
  2. **L'évolution** du projet pour deux relecteurs — avec nouvelle migration

---

## Titre des commits

### Correctif du bug
```
fix: corriger le bug de présence en parallèle (BUG-001)

- Ajouter un test qui reproduit le bug
- Corriger le sérialiseur de la présence
- Mettre à jour le journal
```

### Évolution pour deux relecteurs
```
feat: ajouter le second relecteur pour les relectures

- Modifier la migration de la table relectures
- Mettre à jour le contrat d'API
- Mettre à jour le cahier des charges
```

---

## Stratégie de sacrification de périmètre

### Prix à payer
- L'**attribution aléatoire** des deux relecteurs n'est pas implémentée dans la première version.
- Le **choix des deux relecteurs** est laissé à l'étudiant (il choisit lui-même ses deux pairs).

### Justification
- L'attribution aléatoire est un changement de besoin important qui nécessite une migration de la base de données.
- Le périmètre est réduit pour rester dans le temps imparti.
- Note : l'attribution aléatoire pourra être ajoutée dans une future version.

---

## Statut

| Action | Statut |
|--------|--------|
| Ouvrir une issue | ✅ OUVERTE |
| Lire et comprendre le bug | ✅ Fait |
| Écrire un test | ❌ À faire |
| Corriger le bug | ❌ À faire |
| Mettre à jour l'analyse | ❌ À faire |
| Nouvelle migration | ❌ À faire |
| Mise à jour du contrat d'API | ❌ À faire |
| Commit de correction | ❌ À faire |
| Commit d'évolution | ❌ À faire |
| Push | ❌ À faire |