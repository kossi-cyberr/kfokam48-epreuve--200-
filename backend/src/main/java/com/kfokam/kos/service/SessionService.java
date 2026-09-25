package com.kfokam.kos.service;

import com.kfokam.kos.dto.SessionRequest;
import com.kfokam.kos.dto.SessionResponse;
import java.util.List;

public interface SessionService {

    List<SessionResponse> findAllByPromotionId(Long promotionId);

    SessionResponse findById(Long id);

    SessionResponse create(SessionRequest request);

    SessionResponse open(String titre, Long promotionId);

    Boolean close(Long sessionId);

    Boolean existsByCode(String code);
}
