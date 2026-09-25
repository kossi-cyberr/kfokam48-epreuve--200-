package com.kfokam.kos.service.impl;

import com.kfokam.kos.dto.SessionRequest;
import com.kfokam.kos.dto.SessionResponse;
import com.kfokam.kos.exception.ApiBusinessException;
import com.kfokam.kos.exception.ResourceNotFoundException;
import com.kfokam.kos.model.Promotion;
import com.kfokam.kos.model.Session;
import com.kfokam.kos.repository.PromotionRepository;
import com.kfokam.kos.repository.SessionRepository;
import com.kfokam.kos.service.SessionService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionServiceImpl implements SessionService {

    /** RG1 / Q2 : le code de présence expire 15 minutes après l'ouverture. */
    static final int SESSION_DUREE_MINUTES = 15;

    private final SessionRepository sessionRepository;
    private final PromotionRepository promotionRepository;

    public SessionServiceImpl(SessionRepository sessionRepository,
                              PromotionRepository promotionRepository) {
        this.sessionRepository = sessionRepository;
        this.promotionRepository = promotionRepository;
    }

    @Override
    public List<SessionResponse> findAllByPromotionId(Long promotionId) {
        promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("PROMOTION_INCONNUE", "Promotion introuvable"));
        return sessionRepository.findByPromotionIdOrderByOuvertureAtDesc(promotionId).stream()
                .map(SessionResponse::from)
                .toList();
    }

    @Override
    public SessionResponse findById(Long id) {
        return SessionResponse.from(getSession(id));
    }

    @Override
    @Transactional
    public SessionResponse create(SessionRequest request) {
        return open(request.getTitre(), request.getPromotionId());
    }

    @Override
    @Transactional
    public SessionResponse open(String titre, Long promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("PROMOTION_INCONNUE", "Promotion introuvable"));

        Instant maintenant = Instant.now();
        Session session = Session.builder()
                .titre(titre.trim())
                .promotionId(promotion.getId())
                .code(genererCode())
                .ouvertureAt(maintenant)
                .expirationAt(maintenant.plusSeconds(SESSION_DUREE_MINUTES * 60L))
                .clotee(false)
                .build();
        return SessionResponse.from(sessionRepository.save(session));
    }

    @Override
    @Transactional
    public Boolean close(Long sessionId) {
        Session session = getSession(sessionId);
        if (session.getClotee()) {
            throw new ApiBusinessException("DEJA_CLOTUREE",
                    "Cette session est déjà clôturée.", HttpStatus.CONFLICT);
        }
        session.setClotee(true);
        sessionRepository.save(session);
        return true;
    }

    @Override
    public Boolean existsByCode(String code) {
        return sessionRepository.existsByCodeAndExpirationAtAfter(code, Instant.now());
    }

    private Session getSession(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SESSION_INCONNUE", "Session introuvable"));
    }

    private String genererCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
