package com.kfokam.kos.model;

/**
 * Statuts du cycle de vie d'un exercice.
 * Enveloppe (étape 3) : deux relecteurs par exercice —
 * PROVISOIRE : un seul a rendu (note affichée mais provisoire) ;
 * RELEVE : les deux ont rendu, note retenue = moyenne des deux.
 */
public final class StatutsExercice {

    public static final String EN_ATTENTE = "EN_ATTENTE";
    public static final String PROVISOIRE = "PROVISOIRE";
    public static final String RELEVE = "RELEVE";
    public static final String VALIDEE = "VALIDEE";

    private StatutsExercice() {
    }
}
