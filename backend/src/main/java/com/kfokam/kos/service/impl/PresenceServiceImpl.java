package com.kfokam.kos.service.impl;

import com.kfokam.kos.dto.PresenceResponse;
import com.kfokam.kos.exception.ApiBusinessException;
import com.kfokam.kos.exception.ResourceNotFoundException;
import com.kfokam.kos.model.Presence;
import com.kfokam.kos.model.Session;
import com.kfokam.kos.repository.PresenceRepository;
import com.kfokam.kos.repository.SessionRepository;
import com.kfokam.kos.repository.StudentRepository;
import com.kfokam.kos.service.PresenceService;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PresenceServiceImpl implements PresenceService {

    /** Q4 : au bout de 5 codes erronés, l'étudiant est bloqué 2 minutes. */
    static final int MAX_ESSAIS = 5;
    static final Duration BLOCAGE = Duration.ofMinutes(2);

    private final PresenceRepository presenceRepository;
    private final SessionRepository sessionRepository;
    private final StudentRepository studentRepository;

    private final java.util.Map<Long, Integer> essaisParEtudiant = new java.util.HashMap<>();
    private final java.util.Map<Long, Instant> bloqueJusqua = new java.util.HashMap<>();

    public PresenceServiceImpl(PresenceRepository presenceRepository,
                               SessionRepository sessionRepository,
                               StudentRepository studentRepository) {
        this.presenceRepository = presenceRepository;
        this.sessionRepository = sessionRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public PresenceResponse markPresence(String code, Long etudiantId) {
        verifierBlocage(etudiantId);
        if (etudiantId == null || !studentRepository.existsById(etudiantId)) {
            throw new ResourceNotFoundException("ETUDIANT_INCONNU", "Étudiant introuvable");
        }

        Session session = sessionRepository.findByCode(code)
                .orElseThrow(() -> new ApiBusinessException(
                        "CODE_INCONNU", "Le code de présence n'existe pas",
                        org.springframework.http.HttpStatus.BAD_REQUEST));

        if (session.getClotee()) {
            throw new ApiBusinessException("SESSION_CLOTUREE", "La session est clôturée",
                    org.springframework.http.HttpStatus.BAD_REQUEST);
        }
        if (session.getExpirationAt().isBefore(Instant.now())) {
            // RG1 / Q2 : le code expire 15 minutes après l'ouverture de la session
            throw new ApiBusinessException("CODE_EXPIRE", "Le code de présence a expiré.",
                    org.springframework.http.HttpStatus.GONE);
        }
        // RG6 / Q3 : une seule présence par étudiant et par session
        if (presenceRepository.existsBySessionIdAndEtudiantId(session.getId(), etudiantId)) {
            throw new ApiBusinessException("DEJA_PRESENT", "Vous êtes déjà présent à cette session.",
                    org.springframework.http.HttpStatus.CONFLICT);
        }

        Presence presence = Presence.builder()
                .sessionId(session.getId())
                .etudiantId(etudiantId)
                .source("ETUDIANT")
                .build();
        return PresenceResponse.from(presenceRepository.save(presence));
    }

    /** Q4 : 5 codes erronés => blocage de 2 minutes. */
    private void verifierBlocage(Long etudiantId) {
        Instant limite = bloqueJusqua.get(etudiantId);
        if (limite != null) {
            if (Instant.now().isBefore(limite)) {
                throw new ApiBusinessException("ETUDIANT_BLOQUE",
                        "Trop de codes erronés. Réessayez dans 2 minutes.",
                        org.springframework.http.HttpStatus.TOO_MANY_REQUESTS);
            }
            bloqueJusqua.remove(etudiantId);
            essaisParEtudiant.remove(etudiantId);
        }
    }

    private void noterEchec(Long etudiantId) {
        int essais = essaisParEtudiant.merge(etudiantId, 1, Integer::sum);
        if (essais >= MAX_ESSAIS) {
            bloqueJusqua.put(etudiantId, Instant.now().plus(BLOCAGE));
            essaisParEtudiant.remove(etudiantId);
        }
    }

    @Override
    @Transactional
    public PresenceResponse addManualPresence(Long sessionId, Long etudiantId) {
        // RG14 / Q14 : présence ajoutée par le formateur, traçable via source=FORMATEUR
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("SESSION_INCONNUE", "Session introuvable"));
        if (etudiantId == null || !studentRepository.existsById(etudiantId)) {
            throw new ResourceNotFoundException("ETUDIANT_INCONNU", "Étudiant introuvable");
        }
        if (session.getClotee()) {
            throw new ApiBusinessException("SESSION_CLOTUREE", "La session est clôturée",
                    org.springframework.http.HttpStatus.CONFLICT);
        }
        if (presenceRepository.existsBySessionIdAndEtudiantId(sessionId, etudiantId)) {
            throw new ApiBusinessException("DEJA_PRESENT", "Cet étudiant est déjà présent.",
                    org.springframework.http.HttpStatus.CONFLICT);
        }

        Presence presence = Presence.builder()
                .sessionId(sessionId)
                .etudiantId(etudiantId)
                .source("FORMATEUR")
                .build();
        return PresenceResponse.from(presenceRepository.save(presence));
    }

    @Override
    public List<PresenceResponse> findAllBySessionId(Long sessionId) {
        return presenceRepository.findBySessionIdOrderByCreatedAtAsc(sessionId).stream()
                .map(PresenceResponse::from)
                .toList();
    }

    @Override
    public long countBySessionIdAndEtudiantId(Long sessionId, Long etudiantId) {
        return presenceRepository.countBySessionIdAndEtudiantId(sessionId, etudiantId);
    }

    @Override
    public Boolean existsBySessionIdAndEtudiantId(Long sessionId, Long etudiantId) {
        return presenceRepository.existsBySessionIdAndEtudiantId(sessionId, etudiantId);
    }

    @Override
    public boolean isCodeValid(String code) {
        return sessionRepository.existsByCodeAndExpirationAtAfter(code, Instant.now());
    }
}
