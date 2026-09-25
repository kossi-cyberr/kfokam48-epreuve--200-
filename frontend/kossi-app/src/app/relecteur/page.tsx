"use client";

import { useCallback, useEffect, useState } from "react";
import { api, ApiError, type Exercice, type Relecture } from "@/lib/api";

// =============================================================================
// ÉCRAN 3 — RELECTEUR : voir les relectures qui me sont assignées (Q6/Q7),
// rendre une note entière sur 20 + commentaire (EF4), corriger tant que la
// session n'est pas clôturée (Q10). L'étudiant relu ne verra jamais mon nom (Q8).
// =============================================================================

const CLE_ETUDIANT = "kos-etudiant-id";

export default function PageRelecteur() {
  const [relectures, setRelectures] = useState<Relecture[]>([]);
  const [exercices, setExercices] = useState<Record<number, Exercice>>({});
  const [notes, setNotes] = useState<Record<number, string>>({});
  const [commentaires, setCommentaires] = useState<Record<number, string>>({});
  const [chargement, setChargement] = useState(false);
  const [erreur, setErreur] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  const relecteurId =
    typeof window !== "undefined" ? Number(window.localStorage.getItem(CLE_ETUDIANT)) : NaN;

  const charger = useCallback(async (rid: number) => {
    setChargement(true);
    setErreur(null);
    try {
      const liste = await api.relecturesDuRelecteur(rid);
      setRelectures(liste);
      const details: Record<number, Exercice> = {};
      await Promise.all(
        liste.map(async (r) => {
          try {
            details[r.exerciceId] = await api.exercice(r.exerciceId);
          } catch {
            // exercice introuvable : on laisse undefined
          }
        })
      );
      setExercices(details);
    } catch (e) {
      setErreur(e instanceof ApiError ? `${e.code} — ${e.message}` : "Erreur inattendue");
    } finally {
      setChargement(false);
    }
  }, []);

  useEffect(() => {
    if (Number.isInteger(relecteurId) && relecteurId > 0) void charger(relecteurId);
  }, [relecteurId, charger]);

  async function rendre(relecture: Relecture) {
    const note = Number(notes[relecture.id]);
    if (!Number.isInteger(note) || note < 0 || note > 20) {
      setErreur("RG3 : la note doit être un entier entre 0 et 20.");
      return;
    }
    setErreur(null);
    setMessage(null);
    setChargement(true);
    try {
      if (relecture.note == null) {
        await api.rendreRelecture(relecture.id, note, commentaires[relecture.id] ?? "");
        setMessage("Relecture rendue ✔ L'exercice passe en statut RELU.");
      } else {
        await api.corrigerRelecture(relecture.id, note, commentaires[relecture.id] ?? "");
        setMessage("Relecture corrigée ✔ (possible tant que la session n'est pas clôturée, Q10)");
      }
      if (Number.isInteger(relecteurId)) void charger(relecteurId);
    } catch (e) {
      setErreur(e instanceof ApiError ? `${e.code} — ${e.message}` : "Erreur inattendue");
    } finally {
      setChargement(false);
    }
  }

  if (!Number.isInteger(relecteurId) || relecteurId <= 0) {
    return (
      <>
        <h1>Écran relecteur</h1>
        <p className="erreur">
          Choisissez d&apos;abord votre nom sur l&apos;écran étudiant : votre identité y est mémorisée
          (aucun mot de passe, Q1).
        </p>
      </>
    );
  }

  return (
    <>
      <h1>Écran relecteur</h1>

      <section className="carte">
        <h2>Mes relectures assignées</h2>
        <p className="info">
          Note entière de 0 à 20 (Q9). Un seul relecteur par exercice (Q6). Correction possible jusqu&apos;à
          la clôture de la session (Q10).
        </p>
        {chargement && <p className="info">Chargement…</p>}
        {relectures.length === 0 && !chargement && (
          <p className="info">
            Aucune relectures assignée. Le relecteur est choisi au hasard parmi les étudiants présents (Q7).
          </p>
        )}
        {relectures.map((r) => {
          const exercice = exercices[r.exerciceId];
          return (
            <div key={r.id} style={{ borderTop: "1px solid var(--bordure)", padding: "0.75rem 0" }}>
              <p>
                <strong>Exercice #{r.exerciceId}</strong> —{" "}
                {exercice ? (
                  <a href={exercice.lien} target="_blank" rel="noreferrer">
                    {exercice.lien}
                  </a>
                ) : (
                  "lien indisponible"
                )}{" "}
                — {r.note == null ? "À rendre" : `Rendue : ${r.note}/20`}
              </p>
              <label htmlFor={`note-${r.id}`}>Note /20</label>
              <input
                id={`note-${r.id}`}
                type="number"
                min={0}
                max={20}
                step={1}
                value={notes[r.id] ?? r.note ?? ""}
                onChange={(e) => setNotes({ ...notes, [r.id]: e.target.value })}
              />
              <label htmlFor={`comm-${r.id}`}>Commentaire</label>
              <input
                id={`comm-${r.id}`}
                value={commentaires[r.id] ?? r.commentaire ?? ""}
                onChange={(e) => setCommentaires({ ...commentaires, [r.id]: e.target.value })}
                placeholder="Un commentaire constructif…"
              />
              <button onClick={() => rendre(r)} disabled={chargement}>
                {r.note == null ? "Rendre la relecture" : "Corriger ma relecture"}
              </button>
            </div>
          );
        })}
      </section>

      {message && <p className="succes">{message}</p>}
      {erreur && <p className="erreur">{erreur}</p>}
    </>
  );
}
