package com.kfokam.kos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Corps de POST /api/presences/manual : le formateur ajoute une présence à la main (Q14). */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManualPresenceRequest {

    @NotNull(message = "La session est obligatoire")
    private Long sessionId;

    @NotNull(message = "L'identifiant de l'étudiant est obligatoire")
    private Long etudiantId;
}
