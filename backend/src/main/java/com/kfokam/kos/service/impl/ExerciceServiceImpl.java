package com.kfokam.kos.service.impl;

import com.kfokam.kos.dto.ExerciceRequest;
import com.kfokam.kos.dto.ExerciceResponse;
import com.kfokam.kos.exception.ResourceNotFoundException;
import com.kfokam.kos.model.Exercice;
import com.kfokam.kos.repository.ExerciceRepository;
import com.kfokam.kos.repository.SessionRepository;
import com.kfokam.kos.service.ExerciceService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ExerciceServiceImpl implements ExerciceService {

    private static final String STATUT_EN_ATTENTE = "EN_ATTENTE";
    private static final String STATUT_RELU = "RELU";
    private static final String STATUT_VALIDEE = "VALIDEE";

    private final ExerciceRepository exerciceRepository;
    private final SessionRepository sessionRepository;

    public ExerciceServiceImpl(ExerciceRepository exerciceRepository,
                               SessionRepository sessionRepository) {
        this.exerciceRepository = exerciceRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public List<ExerciceResponse> findAllBySessionId(Long sessionId) {
        return exerciceRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(ExerciceResponse::from)
                .toList();
    }

    @Override
    public ExerciceResponse findById(Long id) {
        return ExerciceResponse.from(exerciceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercice", id)));
    }

    @Override
    public ExerciceResponse findBySessionAndEtudiant(Long sessionId, Long etudiantId) {
        return ExerciceResponse.from(exerciceRepository.findBySessionIdAndEtudiantId(sessionId, etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercice", "Pas de dépôt pour cet étudiant")));
    }

    @Override
    public List<ExerciceResponse> findByEtudiantId(Long etudiantId) {
        return exerciceRepository.findByEtudiantIdOrderByCreatedAtAsc(etudiantId)
                .stream()
                .map(ExerciceResponse::from)
                .toList();
    }

    @Override
    public ExerciceResponse create(ExerciceRequest request) {
        // Vérifier que la session existe
        var session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Session", request.getSessionId()));

        if (session.getClotee()) {
            throw new ResourceNotFoundException("SESSION_CLOTUREE", "La session est clôturée");
        }

        // Vérifier qu'il y a une présence pour cet étudiant
        var presenceOpt = presenceRepository.findBySessionIdAndEtudiantId(request.getSessionId(), request.getEtudiantId());
        if (presenceOpt.isEmpty()) {
            throw new ResourceNotFoundException("PRESENCE_MANQUANTE", "Vous devez être présent à la session pour déposer un exercice");
        }

        // Vérifier s'il a déjà déposé un exercice
        var existing = exerciceRepository.findBySessionIdAndEtudiantId(request.getSessionId(), request.getEtudiantId());
        if (existing.isPresent()) {
            throw new ResourceNotFoundException("EXERCICE_DEJA_DEPOSE", "Vous avez déjà déposé un exercice pour cette session");
        }

        // Vérifier le lien
        if (!isValidUrl(request.getLien())) {
            throw new ResourceNotFoundException("LIEN_INVALIDE", "Le lien n'est pas valide");
        }

        Exercice exercice = Exercice.builder()
                .sessionId(request.getSessionId())
                .etudiantId(request.getEtudiantId())
                .lien(request.getLien().trim())
                .statut(STATUT_EN_ATTENTE)
                .build();

        Exercice saved = exerciceRepository.save(exercice);

        return ExerciceResponse.builder()
                .id(saved.getId())
                .statut(saved.getStatut())
                .build();
    }

    @Override
    public ExerciceResponse updateLink(Long id, String lien) {
        Exercice exercice = exerciceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercice", id));

        if (!isValidUrl(lien)) {
            throw new ResourceNotFoundException("LIEN_INVALIDE", "Le lien n'est pas valide");
        }

        exercice.setLien(lien.trim());
        Exercice saved = exerciceRepository.save(exercice);

        return ExerciceResponse.builder()
                .id(saved.getId())
                .statut(saved.getStatut())
                .build();
    }

    @Override
    public List<ExerciceResponse> findBySessionIdAndStatut(Long sessionId, String statut) {
        return exerciceRepository.findBySessionIdAndStatutOrderByCreatedAtAsc(sessionId, statut)
                .stream()
                .map(ExerciceResponse::from)
                .toList();
    }

    @Override
    public long countBySessionIdAndEtudiantId(Long sessionId, Long etudiantId) {
        return exerciceRepository.countBySessionIdAndEtudiantId(sessionId, etudiantId);
    }

    private boolean isValidUrl(String url) {
        try {
            java.net.URL u = new java.net.URL(url);
            return u.getProtocol().startsWith("http");
        } catch (Exception e) {
            return false;
        }
    }
}
