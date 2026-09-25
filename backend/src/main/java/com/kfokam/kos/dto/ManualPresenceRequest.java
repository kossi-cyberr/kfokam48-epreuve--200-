package com.kfokam.kos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManualPresenceRequest {

    @NotNull(message = "L'identifiant de l'étudiant est obligatoire")
    private Long etudiantId;
}
