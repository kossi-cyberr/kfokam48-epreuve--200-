# D2 — Diagramme de classes (modèle de données)

```mermaid
classDiagram
    class Promotion {
        +Long id
        +String nom
    }

    class Student {
        +Long id
        +String nom
        +String prenom
        +String matricule
        +Long promotionId
    }

    class Session {
        +Long id
        +String titre
        +String code
        +Instant ouvertureAt
        +Instant expirationAt
        +Boolean clotee
        +Long promotionId
    }

    class Presence {
        +Long id
        +Long sessionId
        +Long etudiantId
        +String source
    }

    class Exercice {
        +Long id
        +Long sessionId
        +Long etudiantId
        +String lien
        +String statut
    }

    class Relecture {
        +Long id
        +Long exerciceId
        +Long relecteurId
        +Integer note
        +String commentaire
    }

    Promotion "1" --> "0..*" Session : organise
    Promotion "1" --> "0..*" Student : regroupe
    Session "1" --> "0..*" Presence : recueille
    Session "1" --> "0..*" Exercice : recoit
    Student "1" --> "0..*" Presence : marque
    Student "1" --> "0..*" Exercice : depose
    Student "1" --> "0..*" Relecture : effectue
    Exercice "1" --> "0..1" Relecture : subit
```

## Correspondance avec les migrations (V1 + V3)

- **promotions** (V1) : id, nom.
- **students** (V1 + V3) : nom, prenom, matricule UNIQUE, promotion_id (tableau par promotion, Q16).
- **sessions** (V1) : titre, code, ouverture_at, expiration_at (RG1 : 15 min), clotee, promotion_id.
- **presences** (V1 + V3) : source ETUDIANT|FORMATEUR (Q14), UNIQUE (session_id, etudiant_id) = RG6.
- **exercices** (V1 + V3) : lien, statut EN_ATTENTE|RELU|VALIDEE, UNIQUE (session_id, etudiant_id).
- **relectures** (V1 + V3) : relecteur_id, note nullable (null tant que non rendue), commentaire nullable, UNIQUE (exercice_id) = RG8 (un seul relecteur, Q6).

L'attribut `source` de Presence est `ETUDIANT` ou `FORMATEUR`, conformément à Q14.

Le statut de Exercice : `EN_ATTENTE`, `RELU`, `VALIDEE`, correspondant à Q11 (en attente) et à Q15 (validée).
