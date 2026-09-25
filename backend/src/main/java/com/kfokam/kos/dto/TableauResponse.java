package com.kfokam.kos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Réponse du tableau du formateur (contrat imposé, annexe B).
 * La moyenne est calculée côté API — le frontend ne la recalcule jamais (F3, RG15).
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableauResponse {

    private Long etudiantId;
    private String nom;
    private Integer presences;
    private Integer exercicesDeposes;
    private Double moyenne;
    private Integer relecturesEnAttente;
}
