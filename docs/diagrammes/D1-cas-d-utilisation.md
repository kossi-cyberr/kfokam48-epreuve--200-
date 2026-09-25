# D1 — Cas d'utilisation

```mermaid
flowchart LR
    subgraph Système KOS
        ouvrir("Ouvrir une session<br/>et obtenir un code")
        cloturer("Clôturer la session")
        manuelle("Ajouter une présence<br/>à la main")
        tableau("Consulter le tableau<br/>récapitulatif")
        identite("Choisir son nom<br/>(sans mot de passe)")
        presence("Marquer sa présence<br/>avec un code")
        deposer("Déposer le lien<br/>de son exercice")
        remplacer("Remplacer son lien<br/>(tant que non relu)")
        assigner("Assigner un relecteur<br/>(tirage au hasard)")
        rendre("Rendre une note<br/>et un commentaire")
        corriger("Corriger sa relecture<br/>(avant clôture)")
        vonote("Consulter sa note<br/>(sans le nom du relecteur)")
        ecran("Voir les relectures<br/>à faire / en attente")
    end

    F([Formateur])
    E([Étudiant déposant])
    R([Relecteur])

    F --> ouvrir
    F --> cloturer
    F --> manuelle
    F --> tableau

    E --> identite
    E --> presence
    E --> deposer
    E --> remplacer
    E --> vonote

    R --> identite
    R --> ecran
    R --> rendre
    R --> corriger

    assigner -.->|le système exécute<br/>la règle RG7| R
    vonote -.->|Q8 : nom du<br/>relecteur jamais montré| E
```

## Lecture du diagramme

- **Formateur** : pilote le cycle de la session (ouverture → code → clôture), a une
  action de secours (présence manuelle, Q14) et dispose du tableau récapitulatif (Q16).
- **Étudiant déposant** : s'identifie par choix de nom (Q1), marque sa présence (EF1),
  dépose et remplace son lien (EF2, Q13), consulte sa note sans connaître le relecteur (Q8).
- **Relecteur** : un étudiant présent désigné au hasard (Q7) — il voit la liste de ses
  relectures, rend sa note /20 + commentaire (EF4) et peut corriger avant clôture (Q10).
- L'**assignation** n'est pas un cas d'utilisation humain : c'est le système qui tire
  au hasard parmi les présents, auteur exclu (RG7, RG18).

## Erreurs associées (contrat)

Chaque cas d'utilisation a ses erreurs au format `{code, message}` — voir D3 pour le
détail de « marquer sa présence » (400 CODE_INCONNU, 409 DEJA_PRESENT, 410 CODE_EXPIRE,
429 ETUDIANT_BLOQUE) et `api/contrat.yaml` pour l'exhaustivité.
