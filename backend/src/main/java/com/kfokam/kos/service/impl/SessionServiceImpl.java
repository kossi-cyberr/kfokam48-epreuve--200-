package com.kfokam.kos.service.impl;

import com.kfokam.kos.dto.SessionRequest;
import com.kfokam.kos.dto.SessionResponse;
import com.kfokam.kos.exception.ResourceNotFoundException;
import com.kfokam.kos.model.Promotion;
import com.kfokam.kos.repository.PromotionRepository;
import com.kfokam.kos.repository.SessionRepository;
import com.kfokam.kos.service.SessionService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final PromotionRepository promotionRepository;
    private final static int SESSION_DUREE_MINUTES = 15;

    public SessionServiceImpl(SessionRepository sessionRepository,
                              PromotionRepository promotionRepository) {
        this.sessionRepository = sessionRepository;
        this.promotionRepository = promotionRepository;
    }

    @Override
    public List<SessionResponse> findAllByPromotionId(Long promotionId) {
        return sessionRepository.findByPromotionIdOrderByOuvertureAtDesc(promotionId)
                .stream()
                .map(SessionResponse::from)
                .toList();
    }

    @Override
    public SessionResponse findById(Long id) {
        return SessionResponse.from(sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session", id)));
    }

    @Override
    public SessionResponse create(SessionRequest request) {
        Promotion promotion = promotionRepository.findById(request.getPromotionId())
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", request.getPromotionId()));

        Session session = Session.builder()
                .titre(request.getTitre().trim())
                .promotionId(promotion.getId())
                .ouvertureAt(Instant.now())
                .expirationAt(Instant.now().plusSeconds(SESSION_DUREE_MINUTES * 60))
                .build();

        sessionRepository.save(session);

        return SessionResponse.builder()
                .id(session.getId())
                .code(session.getCode())
                .ouvertureAt(session.getOuvertureAt())
                .expirationAt(session.getExpirationAt())
                .clotee(session.getClotee())
                .build();
    }

    @Override
    public SessionResponse open(String titre, Long promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", promotionId));

        String code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Session session = Session.builder()
                .titre(titre.trim())
                .promotionId(promotion.getId())
                .code(code)
                .ouvertureAt(Instant.now())
                .expirationAt(Instant.now().plusSeconds(SESSION_DUREE_MINUTES * 60))
                .clotee(false)
                .build();

        sessionRepository.save(session);

        return SessionResponse.builder()
                .id(session.getId())
                .code(session.getCode())
                .ouvertureAt(session.getOuvertureAt())
                .expirationAt(session.getExpirationAt())
                .clotee(session.getClotee())
                .build();
    }

    @Override
    public Boolean close(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", sessionId));

        session.setClotee(true);
        sessionRepository.save(session);
        return true;
    }

    @Override
    public Boolean existsByCode(String code) {
        return sessionRepository.existsByCodeAndExpirationAtAfter(code, Instant.now());
    }
}
