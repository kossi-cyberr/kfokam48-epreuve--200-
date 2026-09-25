package com.kfokam.kos.dto;

import com.kfokam.kos.model.Exercice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciceResponse {

    private Long id;
    private Long sessionId;
    private Long etudiantId;
    private String lien;
    private String statut;

    public static ExerciceResponse from(Exercice e) {
        return ExerciceResponse.builder()
                .id(e.getId())
                .sessionId(e.getSessionId())
                .etudiantId(e.getEtudiantId())
                .lien(e.getLien())
                .statut(e.getStatut())
                .build();
    }
}
