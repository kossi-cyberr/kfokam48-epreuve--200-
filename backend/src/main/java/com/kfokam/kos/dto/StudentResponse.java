package com.kfokam.kos.dto;

import com.kfokam.kos.model.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String matricule;
    private Long promotionId;

    public static StudentResponse from(Student s) {
        return StudentResponse.builder()
                .id(s.getId())
                .nom(s.getNom())
                .prenom(s.getPrenom())
                .matricule(s.getMatricule())
                .promotionId(s.getPromotionId())
                .build();
    }
}
