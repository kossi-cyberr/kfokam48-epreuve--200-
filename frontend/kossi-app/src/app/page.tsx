"use client";

import { useCallback, useEffect, useState } from "react";
import { api, ApiError, type Promotion, type Session } from "@/lib/api";

// =============================================================================
// ÉCRAN 1 — FORMATEUR : ouvrir une session (code de présence), clôturer,
// consulter le tableau récapitulatif (EF5, EF6, EF7).
// La moyenne affichée VIENT de l'API — elle n'est jamais recalculée ici (F3).
// =============================================================================

export default function PageFormateur() {
  const [promotions, setPromotions] = useState<Promotion[]>([]);
  const [promotionId, setPromotionId] = useState<number | null>(null);
  const [titre, setTitre] = useState("");
  const [sessionOuverte, setSessionOuverte] = useState<Session | null>(null);
  const [sessions, setSessions] = useState<Session[]>([]);
  const [tableau, setTableau] = useState<Awaited<ReturnType<typeof api.tableau>>>([]);
  const [chargement, setChargement] = useState(false);
  const [erreur, setErreur] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);

  const chargerTout = useCallback(async (pid: number) => {
    setChargement(true);
    setErreur(null);
    try {
      const [s, t] = await Promise.all([api.listerSessions(pid), api.tableau(pid)]);
      setSessions(s);
      setTableau(t);
    } catch (e) {
      setErreur(e instanceof ApiError ? `${e.code} — ${e.message}` : "Erreur inattendue");
    } finally {
      setChargement(false);
    }
  }, []);

  useEffect(() => {
    api
      .listerPromotions()
      .then((p) => {
        setPromotions(p);
        if (p.length > 0) {
          setPromotionId(p[0].id);
          void chargerTout(p[0].id);
        }
      })
      .catch((e) => setErreur(e instanceof ApiError ? e.message : "Erreur inattendue"));
  }, [chargerTout]);

  async function ouvrir() {
    if (promotionId == null || !titre.trim()) return;
    setErreur(null);
    setMessage(null);
    try {
      const s = await api.ouvrirSession(titre.trim(), promotionId);
      setSessionOuverte(s);
      setTitre("");
      setMessage(`Session ouverte — code de présence : ${s.code} (valide 15 minutes)`);
      void chargerTout(promotionId);
    } catch (e) {
      setErreur(e instanceof ApiError ? `${e.code} — ${e.message}` : "Erreur inattendue");
    }
  }

  async function cloturer(id: number) {
    setErreur(null);
    setMessage(null);
    try {
      await api.cloturerSession(id);
      setMessage("Session clôturée.");
      if (promotionId != null) void chargerTout(promotionId);
    } catch (e) {
      setErreur(e instanceof ApiError ? `${e.code} — ${e.message}` : "Erreur inattendue");
    }
  }

  return (
    <>
      <h1>Écran formateur</h1>

      <section className="carte">
        <h2>Ouvrir une session</h2>
        {promotions.length === 0 && <p className="info">Aucune promotion (vérifiez que le backend est démarré).</p>}
        <label htmlFor="titre">Titre de la session</label>
        <input
          id="titre"
          value={titre}
          onChange={(e) => setTitre(e.target.value)}
          placeholder="Ex. Séance 3 — Design patterns"
        />
        <button onClick={ouvrir} disabled={!titre.trim() || promotionId == null}>
          Ouvrir la session
        </button>
        {sessionOuverte && (
          <p className="succes">
            Code de présence : <strong>{sessionOuverte.code}</strong> — expire à{" "}
            {new Date(sessionOuverte.expirationAt).toLocaleTimeString("fr-FR")}
          </p>
        )}
        {message && <p className="succes">{message}</p>}
        {erreur && <p className="erreur">{erreur}</p>}
      </section>

      <section className="carte">
        <h2>Sessions récentes</h2>
        {chargement && <p className="info">Chargement…</p>}
        {sessions.length === 0 && !chargement && <p className="info">Aucune session pour cette promotion.</p>}
        <ul>
          {sessions.map((s) => (
            <li key={s.id} style={{ marginBottom: "0.5rem" }}>
              <strong>{s.titre}</strong> — code {s.code} —{" "}
              {s.clotee ? "clôturée" : `ouverte jusqu'à ${new Date(s.expirationAt).toLocaleTimeString("fr-FR")}`}{" "}
              {!s.clotee && (
                <button className="secondaire" onClick={() => cloturer(s.id)}>
                  Clôturer
                </button>
              )}
            </li>
          ))}
        </ul>
      </section>

      <section className="carte">
        <h2>Tableau récapitulatif (moyenne calculée par l&apos;API)</h2>
        <table>
          <thead>
            <tr>
              <th>Étudiant</th>
              <th>Présences</th>
              <th>Exercices déposés</th>
              <th>Moyenne /20</th>
              <th>Relectures en attente</th>
            </tr>
          </thead>
          <tbody>
            {tableau.map((ligne) => (
              <tr key={ligne.etudiantId}>
                <td>{ligne.nom}</td>
                <td>{ligne.presences}</td>
                <td>{ligne.exercicesDeposes}</td>
                <td>{ligne.moyenne == null ? "—" : ligne.moyenne.toFixed(2)}</td>
                <td>{ligne.relecturesEnAttente}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {tableau.length === 0 && !chargement && <p className="info">Tableau vide.</p>}
      </section>
    </>
  );
}
