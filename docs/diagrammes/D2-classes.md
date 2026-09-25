# D2 — Diagramme de classes (modèle de données)

```mermaid
classDiagram
    class Session {
        +Long id
        +String code
        +LocalDateTime ouvertureAt
        +LocalDateTime expirationAt
        +Boolean clotee
        +String titre
        +Long promotionId
        +Date creationAt
    }

    class Presence {
        +Long id
        +Long etudiantId
        +Long sessionId
        +String source
        +LocalDateTime createdAt
    }

    class Exercice {
        +Long id
        +Long etudiantId
        +Long sessionId
        +String lien
        +String statut
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }

    class Relecture {
        +Long id
        +Long exerciceId
        +Long relecteurId
        +Integer note
        +String commentaire
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }

    Session "1" --> "0..*" Presence : contient
    Session "1" --> "0..*" Exercice : contient
    Presence "1" --> "1" Session : appartient à
    Exercice "1" --> "1" Session : appartient à
    Relecture "1" --> "1" Exercice : corrige
    Relecture "1" --> "1" Presence : du relecteur
```

# D2 — Diagramme de classes (modèle de données)

Les entités principales sont :

- **Session** : contient le code de présence, la date d'ouverture, l'expiration (15 min après), le statut (ouverte/clôturée), le titre, la promotion.
- **Presence** : associé à un étudiant et à une session, avec une source (ETUDIANT ou FORMATEUR), correspondant à Q14 (ajout manuel par le formateur).
- **Exercice** : lié à un étudiant et à une session, avec un lien, un statut (en attente / relu / validé), la date de création et de mise à jour.
- **Relecture** : note (0-20) et commentaire, liée à un exercice et à un relecteur, avec les dates de création et de mise à jour.

L'attribut `source` de Presence est `ETUDIANT` ou `FORMATEUR`, conformément à Q14.

L'état du statut de Exercice est : `EN_ATTENTE`, `RELU`, `VALIDEE`, correspondant à Q11 (en attente) et à Q15 (validée).

# D2 — Diagramme de classes (modèle de données)

```mermaid
classDiagram
    class Session {
        +Long id
        +String code
        +LocalDateTime ouvertureAt
        +LocalDateTime expirationAt
        +Boolean clotee
        +String titre
        +Long promotionId
        +Date creationAt
    }

    class Presence {
        +Long id
        +Long etudiantId
        +Long sessionId
        +String source
        +LocalDateTime createdAt
    }

    class Exercice {
        +Long id
        +Long etudiantId
        +Long sessionId
        +String lien
        +String statut
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }

    class Relecture {
        +Long id
        +Long exerciceId
        +Long relecteurId
        +Integer note
        +String commentaire
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }

    Session "1" --> "0..*" Presence : contient
    Session "1" --> "0..*" Exercice : contient
    Presence "1" --> "1" Session : appartient à
    Exercice "1" --> "1" Session : appartient à
    Relecture "1" --> "1" Exercice : corrige
    Relecture "1" --> "1" Presence : du relecteur
```

# D2 — Diagramme de classes (modèle de données)

Les entités principales sont :

- **Session** : contient le code de présence, la date d'ouverture, l'expiration (15 min après), le statut (ouverte/clôturée), le titre, la promotion.
- **Presence** : associé à un étudiant et à une session, avec une source (ETUDIANT ou FORMATEUR), correspondant à Q14 (ajout manuel par le formateur).
- **Exercice** : lié à un étudiant et à une session, avec un lien, un statut (en attente / relu / validé), la date de création et de mise à jour.
- **Relecture** : note (0-20) et commentaire, liée à un exercice et à un relecteur, avec les dates de création et de mise à jour.

L'attribut `source` de Presence est `ETUDIANT` ou `FORMATEUR`, conformément à Q14.

L'état du statut de Exercice est : `EN_ATTENTE`, `RELU`, `VALIDEE`, correspondant à Q11 (en attente) et à Q15 (validée).
