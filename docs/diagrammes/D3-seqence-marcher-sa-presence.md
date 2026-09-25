# D3 — Séquence : marquer sa présence

```mermaid
sequenceDiagram
    participant E as Étudiant
    participant F as Front
    participant API as PresenceController
    participant S as PresenceService
    E->>F: saisit le code
    F->>API: POST /api/presences { code, etudiantId }
    API->>S: markPresence(code, etudiantId)
    alt code inconnu (400)
        S-->>API: ApiBusinessException CODE_INCONNU
        API-->>F: 400 { code: "CODE_INCONNU", message: "..." }
    else code expiré (RG1, 15 min — Q2)
        S-->>API: ApiBusinessException CODE_EXPIRE
        API-->>F: 410 { code: "CODE_EXPIRE", message: "Le code de présence a expiré." }
    else déjà présent (RG6 — Q3)
        S-->>API: ApiBusinessException DEJA_PRESENT
        API-->>F: 409 { code: "DEJA_PRESENT", message: "..." }
    else 5e code erroné (RG17 — Q4)
        S-->>API: ApiBusinessException ETUDIANT_BLOQUE
        API-->>F: 429 { code: "ETUDIANT_BLOQUE", message: "Réessayez dans 2 minutes." }
    else cas nominal
        S-->>API: Presence (source=ETUDIANT)
        API-->>F: 201 { id, sessionId, etudiantId, source }
    end
```

Le cas nominal est le suivant :

1. L'étudiant saisit le code.
2. Le front appelle `POST /api/presences` avec le code et l'identifiant de l'étudiant.
3. Le contrôleur valide le corps (`@Valid`) puis appelle le service.
4. Le service vérifie : code inconnu (400), session clôturée (409), code expiré (410),
   déjà présent (409), blocage après 5 erreurs (429).
5. Sinon, il enregistre la présence et renvoie 201.

Toutes les erreurs respectent le format imposé `{ code, message }` —
jamais de stack trace (B4).

---

# D4 (bonus) — États-transitions du cycle de vie d'un exercice

> Mis à jour à l'étape 3 (enveloppe) : chaque exercice est relu par **deux relecteurs**.
> Les anciens statuts RELU ont évolué en PROVISOIRE / RELEVE.

```mermaid
stateDiagram-v2
    direction LR
    [*] --> EN_ATTENTE : POST /api/exercices (201)
    EN_ATTENTE --> EN_ATTENTE : lien remplacé (PUT, Q13) tant qu'aucune relecture assignée
    EN_ATTENTE --> PROVISOIRE : 1re relecture rendue (POST /api/relectures/{id})
    PROVISOIRE --> PROVISOIRE : relecture corrigée (PUT, Q10) tant que session ouverte
    PROVISOIRE --> RELEVE : 2e relecture rendue (note = moyenne des deux)
    RELEVE --> RELEVE : relecture corrigée (PUT, Q10) tant que session ouverte
    RELEVE --> VALIDEE : formateur clôture la session (RG10, Q15)
    VALIDEE --> [*]
```

Un exercice passe de `EN_ATTENTE` à `PROVISOIRE` quand le **premier** des deux relecteurs
rend sa relecture, puis de `PROVISOIRE` à `RELEVE` quand le **second** rend : la note
retenue est alors la **moyenne des deux** (RG8, enveloppe).

- **EN_ATTENTE** : déposé, aucun relecteur n'a encore rendu. Le lien reste
  remplaçable tant que personne n'a relu (Q13, RG12) — une relecture **assignée**
  mais non rendue ne bloque pas le remplacement.
- **PROVISOIRE** : un seul des deux relecteurs a rendu — la note est affichée
  mais marquée provisoire ; correction possible tant que la session est ouverte (Q10, RG9).
- **RELEVE** : les deux ont rendu, la note est la moyenne des deux ; les corrections
  restent possibles tant que la session est ouverte (Q10, RG9).
- **VALIDEE** : la session est clôturée, les relectures sont définitives (Q15, RG10).
