package com.kfokam.kos.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
