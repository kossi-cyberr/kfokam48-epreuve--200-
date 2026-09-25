package com.kfokam.kos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Corps de PUT /api/exercices/{id} : remplacement du lien (Q13). */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LienRequest {

    @NotBlank(message = "Le lien est obligatoire")
    private String lien;
}
