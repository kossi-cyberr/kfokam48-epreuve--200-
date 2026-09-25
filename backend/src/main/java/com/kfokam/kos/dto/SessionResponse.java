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
    private String code;
    private Instant ouvertureAt;
    private Instant expirationAt;
    private Boolean clotee;
}
