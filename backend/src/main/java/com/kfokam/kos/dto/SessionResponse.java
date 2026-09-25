package com.kfokam.kos.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionResponse {

    private Long id;
    private String titre;
    private String code;
    private Instant ouvertureAt;
    private Instant expirationAt;
    private Boolean clotee;

    public static SessionResponse from(com.kfokam.kos.model.Session s) {
        return SessionResponse.builder()
                .id(s.getId())
                .titre(s.getTitre())
                .code(s.getCode())
                .ouvertureAt(s.getOuvertureAt())
                .expirationAt(s.getExpirationAt())
                .clotee(s.getClotee())
                .build();
    }
}
