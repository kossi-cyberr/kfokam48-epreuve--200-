package com.kfokam.kos.service;

import com.kfokam.kos.dto.PresenceResponse;
import java.util.List;

public interface PresenceService {

    PresenceResponse markPresence(String code, Long etudiantId);

    /** RG14 : présence ajoutée à la main par le formateur (source = FORMATEUR). */
    PresenceResponse addManualPresence(Long sessionId, Long etudiantId);

    List<PresenceResponse> findAllBySessionId(Long sessionId);

    long countBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    Boolean existsBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    boolean isCodeValid(String code);
}
