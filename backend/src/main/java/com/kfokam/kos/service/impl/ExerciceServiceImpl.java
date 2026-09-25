package com.kfokam.kos.service.impl;

import com.kfokam.kos.dto.ExerciceRequest;
import com.kfokam.kos.dto.ExerciceResponse;
import com.kfokam.kos.exception.ApiBusinessException;
import com.kfokam.kos.exception.ResourceNotFoundException;
import com.kfokam.kos.model.Exercice;
import com.kfokam.kos.model.Session;
import com.kfokam.kos.model.StatutsExercice;
import com.kfokam.kos.repository.ExerciceRepository;
import com.kfokam.kos.repository.PresenceRepository;
import com.kfokam.kos.repository.RelectureRepository;
import com.kfokam.kos.repository.SessionRepository;
import com.kfokam.kos.service.ExerciceService;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExerciceServiceImpl implements ExerciceService {

    private final ExerciceRepository exerciceRepository;
    private final SessionRepository sessionRepository;
    private final PresenceRepository presenceRepository;
    private final RelectureRepository relectureRepository;

    public ExerciceServiceImpl(ExerciceRepository exerciceRepository,
                               SessionRepository sessionRepository,
                               PresenceRepository presenceRepository,
                               RelectureRepository relectureRepository) {
        this.exerciceRepository = exerciceRepository;
        this.sessionRepository = sessionRepository;
        this.presenceRepository = presenceRepository;
        this.relectureRepository = relectureRepository;
    }

    @Override
    public List<ExerciceResponse> findAllBySessionId(Long sessionId) {
        return exerciceRepository.findBySessionIdOrderByCreatedAtAsc(sessionId).stream()
                .map(ExerciceResponse::from)
                .toList();
    }

    @Override
    public ExerciceResponse findById(Long id) {
        return ExerciceResponse.from(getExercice(id));
    }

    @Override
    public ExerciceResponse findBySessionAndEtudiant(Long sessionId, Long etudiantId) {
        return ExerciceResponse.from(exerciceRepository.findBySessionIdAndEtudiantId(sessionId, etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("EXERCICE_INCONNU",
                        "Pas de dépôt pour cet étudiant à cette session")));
    }

    @Override
    public List<ExerciceResponse> findByEtudiantId(Long etudiantId) {
        return exerciceRepository.findByEtudiantIdOrderByCreatedAtAsc(etudiantId).stream()
                .map(ExerciceResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public ExerciceResponse create(ExerciceRequest request) {
        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("SESSION_INCONNUE", "Session introuvable"));

        if (session.getClotee()) {
            // RG13 / Q3+Q12 : dépôt impossible après la clôture de la session
            throw new ApiBusinessException("SESSION_CLOTUREE",
                    "La session est clôturée : dépôt impossible.", HttpStatus.CONFLICT);
        }
        if (!isValidUrl(request.getLien())) {
            throw new ApiBusinessException("LIEN_INVALIDE",
                    "Le lien n'est pas valide.", HttpStatus.BAD_REQUEST);
        }
        if (exerciceRepository.existsBySessionIdAndEtudiantId(request.getSessionId(), request.getEtudiantId())) {
            throw new ApiBusinessException("EXERCICE_DEJA_DEPOSE",
                    "Vous avez déjà déposé un exercice pour cette session.", HttpStatus.CONFLICT);
        }

        Exercice saved = exerciceRepository.save(Exercice.builder()
                .sessionId(request.getSessionId())
                .etudiantId(request.getEtudiantId())
                .lien(request.getLien().trim())
                .statut(StatutsExercice.EN_ATTENTE)
                .build());
        return ExerciceResponse.from(saved);
    }

    @Override
    @Transactional
    public ExerciceResponse updateLink(Long id, String lien) {
        Exercice exercice = getExercice(id);
        Session session = sessionRepository.findById(exercice.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("SESSION_INCONNUE", "Session introuvable"));

        if (session.getClotee()) {
            throw new ApiBusinessException("SESSION_CLOTUREE",
                    "La session est clôturée : le lien ne peut plus être remplacé.", HttpStatus.CONFLICT);
        }
        // RG12 / Q13 : remplacement possible tant que personne n'a commencé la relecture
        if (relectureRepository.existsByExerciceId(id)) {
            throw new ApiBusinessException("RELECTURE_DEBUTEE",
                    "La relecture a déjà commencé : le lien ne peut plus être remplacé.", HttpStatus.FORBIDDEN);
        }
        if (!isValidUrl(lien)) {
            throw new ApiBusinessException("LIEN_INVALIDE", "Le lien n'est pas valide.", HttpStatus.BAD_REQUEST);
        }

        exercice.setLien(lien.trim());
        return ExerciceResponse.from(exerciceRepository.save(exercice));
    }

    @Override
    public List<ExerciceResponse> findBySessionIdAndStatut(Long sessionId, String statut) {
        return exerciceRepository.findBySessionIdAndStatutOrderByCreatedAtAsc(sessionId, statut).stream()
                .map(ExerciceResponse::from)
                .toList();
    }

    @Override
    public long countBySessionIdAndEtudiantId(Long sessionId, Long etudiantId) {
        return exerciceRepository.countBySessionIdAndEtudiantId(sessionId, etudiantId);
    }

    private Exercice getExercice(Long id) {
        return exerciceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EXERCICE_INCONNU", "Exercice introuvable"));
    }

    private boolean isValidUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        try {
            URI uri = URI.create(url.trim());
            String scheme = uri.getScheme();
            return ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                    && uri.getHost() != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
