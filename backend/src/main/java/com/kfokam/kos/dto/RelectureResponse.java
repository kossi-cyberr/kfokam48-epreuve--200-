package com.kfokam.kos.dto;

import com.kfokam.kos.model.Relecture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelectureResponse {

    private Long id;
    private Long exerciceId;
    private Long relecteurId;
    private Integer note;
    private String commentaire;

    public static RelectureResponse from(Relecture r) {
        return RelectureResponse.builder()
                .id(r.getId())
                .exerciceId(r.getExerciceId())
                .relecteurId(r.getRelecteurId())
                .note(r.getNote())
                .commentaire(r.getCommentaire())
                .build();
    }
}
