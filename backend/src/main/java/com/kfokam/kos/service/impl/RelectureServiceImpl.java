package com.kfokam.kos.service.impl;

import com.kfokam.kos.dto.RelectureRequest;
import com.kfokam.kos.dto.RelectureResponse;
import com.kfokam.kos.exception.ResourceNotFoundException;
import com.kfokam.kos.model.Exercice;
import com.kfokam.kos.model.Relecture;
import com.kfokam.kos.model.Student;
import com.kfokam.kos.model.Session;
import com.kfokam.kos.repository.ExerciceRepository;
import com.kfokam.kos.repository.PresenceRepository;
import com.kfokam.kos.repository.RelectureRepository;
import com.kfokam.kos.repository.StudentRepository;
import com.kfokam.kos.repository.SessionRepository;
import com.kfokam.kos.service.RelectureService;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class RelectureServiceImpl implements RelectureService {

    private final RelectureRepository relectureRepository;
    private final ExerciceRepository exerciceRepository;
    private final StudentRepository studentRepository;
    private final PresenceRepository presenceRepository;
    private final SessionRepository sessionRepository;

    public RelectureServiceImpl(RelectureRepository relectureRepository,
                                ExerciceRepository exerciceRepository,
                                StudentRepository studentRepository,
                                PresenceRepository presenceRepository,
                                SessionRepository sessionRepository) {
        this.relectureRepository = relectureRepository;
        this.exerciceRepository = exerciceRepository;
        this.studentRepository = studentRepository;
        this.presenceRepository = presenceRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public RelectureResponse create(RelectureRequest request) {
        // Vérifier l'exercice
        var exerciceOpt = exerciceRepository.findById(request.getExerciceId());
        if (exerciceOpt.isEmpty()) {
            throw new ResourceNotFoundException("EXERCICE_INCONNU", "L'exercice n'existe pas");
        }
        var exercice = exerciceOpt.get();

        // Vérifier que la session est ouverte
        var session = sessionRepository.findById(exercice.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("SESSION_INCONNUE", "Session non trouvée"));

        if (session.getClotee()) {
            throw new ResourceNotFoundException("SESSION_CLOTUREE", "La session est clôturée");
        }

        // Vérifier que le relecteur n'est pas l'étudiant qui a déposé l'exercice
        if (exercice.getEtudiantId().equals(request.getRelecteurId())) {
            throw new ResourceNotFoundException("AUTO_RELECTURE", "Impossible de relire son propre exercice");
        }

        // Vérifier que le relecteur est présent à la session
        var presence = presenceRepository.findBySessionIdAndEtudiantId(exercice.getSessionId(), request.getRelecteurId());
        if (presence.isEmpty()) {
            throw new ResourceNotFoundException("PAS_PRESENT", "Le relecteur n'est pas présent à la session");
        }

        // Vérifier qu'une relecture n'a pas déjà été rendue
        if (relectureRepository.existsByExerciceId(request.getExerciceId())) {
            throw new ResourceNotFoundException("REELECTURE_DEJA_RENDUE", "Une relecture a déjà été rendue");
        }

        // Valider la note
        if (request.getNote() == null || request.getNote() < 0 || request.getNote() > 20) {
            throw new ResourceNotFoundException("NOTE_INVALIDE", "La note doit être entre 0 et 20");
        }

        Relecture relecture = Relecture.builder()
                .exerciceId(request.getExerciceId())
                .relecteurId(request.getRelecteurId())
                .note(request.getNote())
                .commentaire(request.getCommentaire() != null ? request.getCommentaire() : "")
                .build();

        Relecture saved = relectureRepository.save(relecture);

        return RelectureResponse.builder()
                .id(saved.getId())
                .note(saved.getNote())
                .commentaire(saved.getCommentaire())
                .build();
    }

    @Override
    public RelectureResponse findById(Long id) {
        return RelectureResponse.from(relectureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Relecture", id)));
    }

    @Override
    public RelectureResponse findByExerciceId(Long exerciceId) {
        return RelectureResponse.from(relectureRepository.findByExerciceId(exerciceId)
                .orElseThrow(() -> new ResourceNotFoundException("Relecture", "Pas de relecture pour cet exercice")));
    }

    @Override
    public List<RelectureResponse> findAllByExerciceId(Long exerciceId) {
        return relectureRepository.findByExerciceIdOrderByCreatedAtAsc(exerciceId)
                .stream()
                .map(RelectureResponse::from)
                .toList();
    }

    @Override
    public boolean existsByExerciceId(Long exerciceId) {
        return relectureRepository.existsByExerciceId(exerciceId);
    }

    @Override
    public boolean canModify(Long relectureId, Long sessionId) {
        var relecture = relectureRepository.findById(relectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Relecture", relectureId));

        var session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", sessionId));

        return !session.getClotee();
    }
}
