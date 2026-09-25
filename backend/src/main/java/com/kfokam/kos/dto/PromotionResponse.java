package com.kfokam.kos.dto;

import com.kfokam.kos.model.Promotion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionResponse {

    private Long id;
    private String nom;

    public static PromotionResponse from(Promotion p) {
        return PromotionResponse.builder()
                .id(p.getId())
                .nom(p.getNom())
                .build();
    }
}
