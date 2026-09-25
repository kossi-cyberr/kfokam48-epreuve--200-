"use client";

import { useCallback, useEffect, useState } from "react";
import { api, ApiError, type Etudiant, type Exercice } from "@/lib/api";

// =============================================================================
// ÉCRAN 2 — ÉTUDIANT : choisir son nom dans la liste (Q1, aucun mot de passe),
// marquer sa présence avec le code (EF1), déposer / remplacer son exercice (EF2, Q13).
// =============================================================================

const CLE_ETUDIANT = "kos-etudiant-id";

function BadgeStatut({ statut }: { statut: string }) {
  if (statut === "EN_ATTENTE") return <span className="badge badge-gris">En attente</span>;
  if (statut === "PROVISOIRE") return <span className="badge badge-orange">Provisoire</span>;
  if (statut === "RELEVE") return <span className="badge badge-vert">Relu</span>;
  return <span className="badge badge-vert">{statut}</span>;
}

export default function PageEtudiant() {
  const [etudiants, setEtudiants] = useState<Etudiant[]>([]);
  const [etudiantId, setEtudiantId] = useState<number | null>(null);
  const [code, setCode] = useState("");
  const [lien, setLien] = useState("");
  const [mesExercices, setMesExercices] = useState<Exercice[]>([]);
  const [chargement, setChargement] = useState(false);
  const [erreur, setErreur] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    const garde = typeof window !== "undefined" ? window.localStorage.getItem(CLE_ETUDIANT) : null;
    api
      .listerEtudiants()
      .then((liste) => {
        setEtudiants(liste);
        const gardeValide = garde != null && liste.some((e) => e.id === Number(garde));
        if (gardeValide) setEtudiantId(Number(garde));
      })
      .catch((e) => setErreur(e instanceof ApiError ? e.message : "Erreur inattendue"));
  }, []);

  const chargerMesExercices = useCallback(async (id: number) => {
    try {
      setMesExercices(await api.listerExercicesParEtudiant(id));
    } catch {
      // non bloquant
    }
  }, []);

  function choisirEtudiant(id: number) {
    setEtudiantId(id);
    window.localStorage.setItem(CLE_ETUDIANT, String(id));
    void chargerMesExercices(id);
  }

  async function marquerPresence() {
    if (etudiantId == null || !code.trim()) return;
    setErreur(null);
    setMessage(null);
    setChargement(true);
    try {
      await api.marquerPresence(code.trim().toUpperCase(), etudiantId);
      setMessage("Présence enregistrée ✔");
      setCode("");
    } catch (e) {
      setErreur(e instanceof ApiError ? `${e.code} — ${e.message}` : "Erreur inattendue");
    } finally {
      setChargement(false);
    }
  }

  async function deposer() {
    if (etudiantId == null || !lien.trim()) return;
    setErreur(null);
    setMessage(null);
    setChargement(true);
    try {
      await api.deposerExercice(await demanderSession(), etudiantId, lien.trim());
      setMessage("Exercice déposé ✔");
      setLien("");
      void chargerMesExercices(etudiantId);
    } catch (e) {
      setErreur(e instanceof ApiError ? `${e.code} — ${e.message}` : "Erreur inattendue");
    } finally {
      setChargement(false);
    }
  }

  // La session de dépôt est demandée à l'étudiant : on liste les sessions de sa
  // promotion et il choisit. Simplification assumée (sujet : pas de login, Q1).
  // Ticket #34 (Should) propose de remplacer ce prompt par une liste déroulante.
  async function demanderSession(): Promise<number> {
    const saisie = window.prompt("Numéro de la session pour ce dépôt :");
    const n = Number(saisie);
    if (!Number.isInteger(n) || n <= 0) {
      throw new ApiError("SESSION_INVALIDE", "Numéro de session invalide.");
    }
    return n;
  }

  async function remplacerLien(exercice: Exercice) {
    const nouveau = window.prompt("Nouveau lien de l'exercice :", exercice.lien);
    if (!nouveau || etudiantId == null) return;
    setErreur(null);
    setMessage(null);
    try {
      await api.remplacerLien(exercice.id, nouveau.trim());
      setMessage("Lien remplacé ✔");
      void chargerMesExercices(etudiantId);
    } catch (e) {
      setErreur(e instanceof ApiError ? `${e.code} — ${e.message}` : "Erreur inattendue");
    }
  }

  return (
    <>
      <h1>Écran étudiant</h1>

      <section className="carte">
        <h2>Qui êtes-vous ? (aucun mot de passe, Q1)</h2>
        <label htmlFor="etudiant">Choisissez votre nom</label>
        <select
          id="etudiant"
          value={etudiantId ?? ""}
          onChange={(e) => choisirEtudiant(Number(e.target.value))}
        >
          <option value="">— choisir —</option>
          {etudiants.map((e) => (
            <option key={e.id} value={e.id}>
              {e.prenom} {e.nom} ({e.matricule})
            </option>
          ))}
        </select>
      </section>

      <section className="carte">
        <h2>Marquer ma présence</h2>
        <p className="info">Saisissez le code affiché par le formateur (valide 15 minutes, RG1).</p>
        <label htmlFor="code">Code de présence</label>
        <input
          id="code"
          value={code}
          onChange={(e) => setCode(e.target.value)}
          placeholder="Ex. 3EDDCBCC"
          maxLength={10}
          autoCapitalize="characters"
        />
        <button onClick={marquerPresence} disabled={etudiantId == null || !code.trim() || chargement}>
          Marquer ma présence
        </button>
      </section>

      <section className="carte">
        <h2>Déposer mon exercice</h2>
        <p className="info">
          Dépôt possible jusqu&apos;à la clôture de la session (Q12). Remplacement possible tant que
          personne n&apos;a commencé la relecture (Q13).
        </p>
        <label htmlFor="lien">Lien de l&apos;exercice</label>
        <input
          id="lien"
          value={lien}
          onChange={(e) => setLien(e.target.value)}
          placeholder="https://github.com/…"
        />
        <button onClick={deposer} disabled={etudiantId == null || !lien.trim() || chargement}>
          Déposer
        </button>

        {mesExercices.length > 0 && (
          <div style={{ marginTop: "1rem" }}>
            <h2>Mes dépôts</h2>
            {mesExercices.map((ex) => (
              <div key={ex.id} className="bloc-relecture">
                <div className="entete-bloc">
                  <p>
                    <strong>Session {ex.sessionId}</strong>
                  </p>
                  <BadgeStatut statut={ex.statut} />
                </div>
                <p className="info" style={{ wordBreak: "break-all" }}>
                  {ex.lien}
                </p>
                <button className="secondaire" onClick={() => remplacerLien(ex)}>
                  Remplacer le lien
                </button>
              </div>
            ))}
          </div>
        )}
      </section>

      {message && <p className="succes">{message}</p>}
      {erreur && <p className="erreur">{erreur}</p>}
    </>
  );
}
