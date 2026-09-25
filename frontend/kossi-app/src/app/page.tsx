"use client";

import { useCallback, useEffect, useState } from "react";
import { api, ApiError, type Etudiant, type Promotion, type Session } from "@/lib/api";

// =============================================================================
// ÉCRAN 1 — FORMATEUR : ouvrir une session (code de présence), clôturer,
// ajouter une présence à la main (EF6, ticket #33), consulter le tableau
// récapitulatif (EF5, EF7).
// La moyenne affichée VIENT de l'API — elle n'est jamais recalculée ici (F3).
// =============================================================================

function BadgeSession({ clotee }: { clotee: boolean }) {
  return clotee ? (
    <span className="badge badge-gris">Clôturée</span>
  ) : (
    <span className="badge badge-vert">Ouverte</span>
  );
}

export default function PageFormateur() {
  const [promotions, setPromotions] = useState<Promotion[]>([]);
  const [promotionId, setPromotionId] = useState<number | null>(null);
  const [titre, setTitre] = useState("");
  const [sessionOuverte, setSessionOuverte] = useState<Session | null>(null);
  const [sessions, setSessions] = useState<Session[]>([]);
  const [tableau, setTableau] = useState<Awaited<ReturnType<typeof api.tableau>>>([]);
  const [etudiants, setEtudiants] = useState<Etudiant[]>([]);
  const [etudiantManuel, setEtudiantManuel] = useState<number | null>(null);
  const [sessionManuelle, setSessionManuelle] = useState<number | null>(null);
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
    api
      .listerEtudiants()
      .then(setEtudiants)
      .catch(() => {
        // non bloquant : la liste sert aux présences manuelles
      });
  }, [chargerTout]);

  // Les sessions ouvertes (non clôturées) sont les seules éligibles à une présence manuelle
  const sessionsOuvertes = sessions.filter((s) => !s.clotee);

  useEffect(() => {
    // Si la session sélectionnée vient d'être clôturée, on resélectionne
    if (sessionManuelle != null && !sessionsOuvertes.some((s) => s.id === sessionManuelle)) {
      setSessionManuelle(sessionsOuvertes[0]?.id ?? null);
    } else if (sessionManuelle == null && sessionsOuvertes.length > 0) {
      setSessionManuelle(sessionsOuvertes[0].id);
    }
  }, [sessions, sessionManuelle, sessionsOuvertes]);

  async function ouvrir() {
    if (promotionId == null || !titre.trim()) return;
    setErreur(null);
    setMessage(null);
    try {
      const s = await api.ouvrirSession(titre.trim(), promotionId);
      setSessionOuverte(s);
      setTitre("");
      setMessage(`Session « ${s.titre} » ouverte.`);
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

  // EF6 / RG14 / Q14 : présence ajoutée par le formateur (source=FORMATEUR)
  async function ajouterPresenceManuelle() {
    if (sessionManuelle == null || etudiantManuel == null) return;
    setErreur(null);
    setMessage(null);
    setChargement(true);
    try {
      const p = await api.ajouterPresenceManuelle(sessionManuelle, etudiantManuel);
      const qui = etudiants.find((e) => e.id === etudiantManuel);
      setMessage(
        `Présence ajoutée pour ${qui ? `${qui.prenom} ${qui.nom}` : `l'étudiant ${p.etudiantId}`} `
        + `(source : ${p.source}).`
      );
      setEtudiantManuel(null);
      if (promotionId != null) void chargerTout(promotionId);
    } catch (e) {
      setErreur(e instanceof ApiError ? `${e.code} — ${e.message}` : "Erreur inattendue");
    } finally {
      setChargement(false);
    }
  }

  return (
    <>
      <h1>Écran formateur</h1>

      <section className="carte">
        <h2>Ouvrir une session</h2>
        {promotions.length === 0 && (
          <p className="info">Aucune promotion (vérifiez que le backend est démarré).</p>
        )}
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
          <div className="code-encadre" role="status">
            <span className="code-valeur">{sessionOuverte.code}</span>
            <span className="info">
              Code de présence — expire à{" "}
              {new Date(sessionOuverte.expirationAt).toLocaleTimeString("fr-FR")} (RG1 : 15 min)
            </span>
          </div>
        )}
        {message && <p className="succes">{message}</p>}
        {erreur && <p className="erreur">{erreur}</p>}
      </section>

      <section className="carte">
        <h2>Ajouter une présence à la main</h2>
        <p className="info">
          Pour un étudiant dont le téléphone a un souci (Q14). La présence est tracée avec la mention
          « ajouté par le formateur » (source FORMATEUR, RG14).
        </p>
        {sessionsOuvertes.length === 0 ? (
          <p className="info">Aucune session ouverte : ouvrez d&apos;abord une session.</p>
        ) : (
          <>
            <label htmlFor="session-manuelle">Session (ouvertes uniquement)</label>
            <select
              id="session-manuelle"
              value={sessionManuelle ?? ""}
              onChange={(e) => setSessionManuelle(Number(e.target.value))}
            >
              {sessionsOuvertes.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.titre} — code {s.code}
                </option>
              ))}
            </select>
            <label htmlFor="etudiant-manuel">Étudiant</label>
            <select
              id="etudiant-manuel"
              value={etudiantManuel ?? ""}
              onChange={(e) =>
                setEtudiantManuel(e.target.value ? Number(e.target.value) : null)
              }
            >
              <option value="">— choisir l&apos;étudiant —</option>
              {etudiants.map((e) => (
                <option key={e.id} value={e.id}>
                  {e.prenom} {e.nom} ({e.matricule})
                </option>
              ))}
            </select>
            <button
              onClick={ajouterPresenceManuelle}
              disabled={sessionManuelle == null || etudiantManuel == null || chargement}
            >
              Ajouter la présence
            </button>
          </>
        )}
      </section>

      <section className="carte">
        <h2>Sessions récentes</h2>
        {chargement && <p className="info">Chargement…</p>}
        {sessions.length === 0 && !chargement && (
          <p className="info">Aucune session pour cette promotion.</p>
        )}
        <ul className="liste-sessions">
          {sessions.map((s) => (
            <li key={s.id}>
              <div>
                <strong>{s.titre}</strong>
                <div className="info">
                  code <code>{s.code}</code> —{" "}
                  {s.clotee
                    ? "clôturée"
                    : `ouverte jusqu'à ${new Date(s.expirationAt).toLocaleTimeString("fr-FR")}`}
                </div>
              </div>
              <div className="ligne-actions">
                <BadgeSession clotee={s.clotee} />
                {!s.clotee && (
                  <button className="secondaire" onClick={() => cloturer(s.id)}>
                    Clôturer
                  </button>
                )}
              </div>
            </li>
          ))}
        </ul>
      </section>

      <section className="carte">
        <h2>Tableau récapitulatif</h2>
        <p className="info">Moyenne calculée par l&apos;API — jamais recalculée ici (RG15).</p>
        <div className="tableau-scroll">
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
                  <td>
                    {ligne.moyenne == null ? (
                      "—"
                    ) : (
                      <>
                        {ligne.moyenne.toFixed(2)}{" "}
                        {ligne.moyenneProvisoire && <span className="badge badge-orange">provisoire</span>}
                      </>
                    )}
                  </td>
                  <td>{ligne.relecturesEnAttente}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {tableau.length === 0 && !chargement && <p className="info">Tableau vide.</p>}
      </section>
    </>
  );
}
