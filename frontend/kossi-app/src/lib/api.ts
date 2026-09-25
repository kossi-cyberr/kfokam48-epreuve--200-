// =============================================================================
// Couche API dédiée (F3) : TOUTES les appellations HTTP passent par ce module.
// Aucun composant ne fait de fetch directement, aucune règle métier n'est
// dupliquée : la moyenne affichée vient de l'API (RG15).
// =============================================================================

const BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8090";

/** Format d'erreur imposé par le contrat : { code, message }. */
export class ApiError extends Error {
  readonly code: string;

  constructor(code: string, message: string) {
    super(message);
    this.code = code;
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  let response: Response;
  try {
    response = await fetch(`${BASE_URL}${path}`, {
      ...init,
      headers: { "Content-Type": "application/json", ...(init?.headers ?? {}) },
      cache: "no-store",
    });
  } catch {
    throw new ApiError("API_INACCESSIBLE", "Impossible de joindre le serveur. Vérifiez que le backend est démarré.");
  }

  if (!response.ok) {
    let code = `HTTP_${response.status}`;
    let message = `Erreur ${response.status}`;
    try {
      const body = (await response.json()) as { code?: string; message?: string };
      if (body.code) code = body.code;
      if (body.message) message = body.message;
    } catch {
      // corps non JSON : on garde les valeurs par défaut
    }
    throw new ApiError(code, message);
  }

  if (response.status === 204) {
    return undefined as T;
  }
  return (await response.json()) as T;
}

// ---------------------------------------------------------------------------
// Types du domaine (miroir du contrat api/contrat.yaml)
// ---------------------------------------------------------------------------

export interface Session {
  id: number;
  titre: string;
  code: string;
  ouvertureAt: string;
  expirationAt: string;
  clotee: boolean;
}

export interface Presence {
  id: number;
  sessionId: number;
  etudiantId: number;
  source: "ETUDIANT" | "FORMATEUR";
}

export interface Exercice {
  id: number;
  sessionId: number;
  etudiantId: number;
  lien: string;
  statut: "EN_ATTENTE" | "RELU" | "VALIDEE" | string;
}

export interface Relecture {
  id: number;
  exerciceId: number;
  relecteurId: number;
  note: number | null;
  commentaire: string | null;
}

export interface Etudiant {
  id: number;
  nom: string;
  prenom: string;
  matricule: string;
  promotionId: number | null;
}

export interface Promotion {
  id: number;
  nom: string;
}

export interface LigneTableau {
  etudiantId: number;
  nom: string;
  presences: number;
  exercicesDeposes: number;
  moyenne: number | null;
  relecturesEnAttente: number;
}

// ---------------------------------------------------------------------------
// Opérations du contrat + compléments
// ---------------------------------------------------------------------------

export const api = {
  // --- Promotions / étudiants (écrans de sélection d'identité, Q1) ---
  listerPromotions: () => request<Promotion[]>("/api/promotions"),
  listerEtudiants: () => request<Etudiant[]>("/api/students"),

  // --- Sessions ---
  ouvrirSession: (titre: string, promotionId: number) =>
    request<Session>("/api/sessions", {
      method: "POST",
      body: JSON.stringify({ titre, promotionId }),
    }),
  listerSessions: (promotionId: number) =>
    request<Session[]>(`/api/sessions?promotionId=${promotionId}`),
  cloturerSession: (id: number) =>
    request<void>(`/api/sessions/${id}/cloture`, { method: "POST" }),

  // --- Présences ---
  marquerPresence: (code: string, etudiantId: number) =>
    request<Presence>("/api/presences", {
      method: "POST",
      body: JSON.stringify({ code, etudiantId }),
    }),
  listerPresences: (sessionId: number) =>
    request<Presence[]>(`/api/presences?sessionId=${sessionId}`),
  ajouterPresenceManuelle: (sessionId: number, etudiantId: number) =>
    request<Presence>("/api/presences/manual", {
      method: "POST",
      body: JSON.stringify({ sessionId, etudiantId }),
    }),

  // --- Exercices ---
  deposerExercice: (sessionId: number, etudiantId: number, lien: string) =>
    request<Exercice>("/api/exercices", {
      method: "POST",
      body: JSON.stringify({ sessionId, etudiantId, lien }),
    }),
  remplacerLien: (exerciceId: number, lien: string) =>
    request<Exercice>(`/api/exercices/${exerciceId}`, {
      method: "PUT",
      body: JSON.stringify({ lien }),
    }),
  listerExercices: (sessionId: number) =>
    request<Exercice[]>(`/api/exercices?sessionId=${sessionId}`),
  listerExercicesParEtudiant: (etudiantId: number) =>
    request<Exercice[]>(`/api/exercices?etudiantId=${etudiantId}`),

  // --- Relectures ---
  assignerRelecteur: (exerciceId: number) =>
    request<Relecture>(`/api/relectures/${exerciceId}/assignation`, {
      method: "POST",
      body: JSON.stringify({}),
    }),
  rendreRelecture: (relectureId: number, note: number, commentaire: string) =>
    request<Relecture>(`/api/relectures/${relectureId}`, {
      method: "POST",
      body: JSON.stringify({ note, commentaire }),
    }),
  corrigerRelecture: (relectureId: number, note: number, commentaire: string) =>
    request<Relecture>(`/api/relectures/${relectureId}`, {
      method: "PUT",
      body: JSON.stringify({ note, commentaire }),
    }),
  relecturesDuRelecteur: (relecteurId: number) =>
    request<Relecture[]>(`/api/relectures?relecteurId=${relecteurId}`),
  exercice: (exerciceId: number) => request<Exercice>(`/api/exercices/${exerciceId}`),

  // --- Tableau (la moyenne VIENT de l'API — jamais recalculée ici, RG15) ---
  tableau: (promotionId: number) =>
    request<LigneTableau[]>(`/api/tableau?promotionId=${promotionId}`),
};
