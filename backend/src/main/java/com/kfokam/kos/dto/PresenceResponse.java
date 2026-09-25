package com.kfokam.kos.dto;

import com.kfokam.kos.model.Presence;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresenceResponse {

    private Long id;
    private Long sessionId;
    private Long etudiantId;
    private String source;

    public static PresenceResponse from(Presence p) {
        return PresenceResponse.builder()
                .id(p.getId())
                .sessionId(p.getSessionId())
                .etudiantId(p.getEtudiantId())
                .source(p.getSource())
                .build();
    }
}
