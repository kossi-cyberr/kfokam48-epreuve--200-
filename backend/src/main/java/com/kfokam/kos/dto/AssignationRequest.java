package com.kfokam.kos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Corps de POST /api/relectures/{id}/assignation : le système (ou le formateur)
 * désigne au hasard un relecteur parmi les étudiants présents à la session (Q6/Q7).
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignationRequest {

    /** Optionnel : absent, le choix du relecteur est aléatoire (RG7). */
    private Long relecteurId;
}
