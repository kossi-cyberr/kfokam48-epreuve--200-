package com.kfokam.kos.service.impl;

import com.kfokam.kos.dto.PresenceResponse;
import com.kfokam.kos.exception.ResourceNotFoundException;
import com.kfokam.kos.model.Presence;
import com.kfokam.kos.repository.PresenceRepository;
import com.kfokam.kos.repository.SessionRepository;
import com.kfokam.kos.repository.StudentRepository;
import com.kfokam.kos.service.PresenceService;
import com.kfokam.kos.service.SessionService;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PresenceServiceImpl implements PresenceService {

    private final PresenceRepository presenceRepository;
    private final SessionService sessionService;
    private final StudentService studentService;
    private final SessionRepository sessionRepository;

    public PresenceServiceImpl(PresenceRepository presenceRepository,
                               SessionService sessionService,
                               StudentService studentService,
                               SessionRepository sessionRepository) {
        this.presenceRepository = presenceRepository;
        this.sessionService = sessionService;
        this.studentService = studentService;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public PresenceResponse markPresence(String code, Long etudiantId) {
        if (!sessionService.existsByCode(code)) {
            throw new ResourceNotFoundException("CODE_INCONNU", "Le code de présence n'existe pas");
        }

        var sessionOpt = sessionRepository.findByCode(code);
        if (sessionOpt.isEmpty()) {
            throw new ResourceNotFoundException("CODE_INCONNU", "Le code de présence n'existe pas");
        }
        var session = sessionOpt.get();

        if (session.getClotee()) {
            throw new ResourceNotFoundException("SESSION_CLOTUREE", "La session est clôturée");
        }

        if (session.getExpirationAt().isBefore(Instant.now())) {
            throw new ResourceNotFoundException("CODE_EXPIRE", "Le code de présence a expiré");
        }

        if (presenceRepository.existsBySessionIdAndEtudiantId(session.getId(), etudiantId)) {
            throw new ResourceNotFoundException("DEJA_PRESENT", "Déjà présent à cette session");
        }

        Presence presence = Presence.builder()
                .sessionId(session.getId())
                .etudiantId(etudiantId)
                .source("ETUDIANT")
                .build();

        Presence saved = presenceRepository.save(presence);

        return PresenceResponse.from(saved);
    }

    @Override
    public PresenceResponse addManualPresence(Long presenceId, Long etudiantId) {
        var presence = presenceRepository.findById(presenceId)
                .orElseThrow(() -> new ResourceNotFoundException("PRESENCE_INCONNUE", "Présence non trouvée"));

        // Vérifier que la session est ouverte
        var session = sessionRepository.findById(presence.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("SESSION_INCONNUE", "Session non trouvée"));

        if (session.getClotee()) {
            throw new ResourceNotFoundException("SESSION_CLOTUREE", "La session est clôturée");
        }

        if (presenceRepository.existsBySessionIdAndEtudiantId(session.getId(), etudiantId)) {
            throw new ResourceNotFoundException("DEJA_PRESENT", "Déjà présent à cette session");
        }

        presence.setEtudiantId(etudiantId);
        presence.setSource("FORMATEUR");

        Presence saved = presenceRepository.save(presence);

        return PresenceResponse.from(saved);
    }

    @Override
    public List<PresenceResponse> findAllBySessionId(Long sessionId) {
        return presenceRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(PresenceResponse::from)
                .toList();
    }

    @Override
    public long countBySessionIdAndEtudiantId(Long sessionId, Long etudiantId) {
        return presenceRepository.countBySessionIdAndEtudiantId(sessionId, etudiantId);
    }

    @Override
    public Boolean existsBySessionIdAndEtudiantId(Long sessionId, Long etudiantId) {
        return presenceRepository.countBySessionIdAndEtudiantId(sessionId, etudiantId) > 0;
    }

    @Override
    public boolean isCodeValid(String code) {
        return sessionService.existsByCode(code);
    }
}
