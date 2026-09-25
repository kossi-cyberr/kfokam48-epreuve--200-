package com.kfokam.kos.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelectureRequest {

    @NotNull(message = "L'identifiant de l'exercice est obligatoire")
    private Long exerciceId;

    @NotNull(message = "La note est obligatoire")
    @Min(value = 0, message = "La note doit être comprise entre 0 et 20")
    @Max(value = 20, message = "La note doit être comprise entre 0 et 20")
    private Integer note;

    private String commentaire;
}
