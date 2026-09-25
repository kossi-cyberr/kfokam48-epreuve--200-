package com.kfokam.kos.service.impl;

import com.kfokam.kos.dto.RelectureRequest;
import com.kfokam.kos.dto.RelectureResponse;
import com.kfokam.kos.exception.ApiBusinessException;
import com.kfokam.kos.exception.ResourceNotFoundException;
import com.kfokam.kos.model.Exercice;
import com.kfokam.kos.model.Relecture;
import com.kfokam.kos.model.Session;
import com.kfokam.kos.model.StatutsExercice;
import com.kfokam.kos.repository.ExerciceRepository;
import com.kfokam.kos.repository.PresenceRepository;
import com.kfokam.kos.repository.RelectureRepository;
import com.kfokam.kos.repository.SessionRepository;
import com.kfokam.kos.repository.StudentRepository;
import com.kfokam.kos.service.RelectureService;
import java.util.List;
import java.util.Random;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RelectureServiceImpl implements RelectureService {

    private final RelectureRepository relectureRepository;
    private final ExerciceRepository exerciceRepository;
    private final StudentRepository studentRepository;
    private final PresenceRepository presenceRepository;
    private final SessionRepository sessionRepository;
    private final Random random = new Random();

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
    @Transactional
    public RelectureResponse assigner(Long exerciceId, Long relecteurId) {
        Exercice exercice = exerciceRepository.findById(exerciceId)
                .orElseThrow(() -> new ResourceNotFoundException("EXERCICE_INCONNU", "Exercice introuvable"));
        Session session = sessionRepository.findById(exercice.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("SESSION_INCONNUE", "Session introuvable"));

        // RG8 / Q6 : un seul relecteur par exercice
        if (relectureRepository.existsByExerciceId(exerciceId)) {
            throw new ApiBusinessException("RELECTURE_DEJA_ASSIGNEE",
                    "Un relecteur est déjà assigné à cet exercice.", HttpStatus.CONFLICT);
        }

        Long choisi = (relecteurId != null) ? relecteurId : choisirAuHasard(exercice);
        verifierElegibilite(exercice, session, choisi);

        Relecture relecture = relectureRepository.save(Relecture.builder()
                .exerciceId(exerciceId)
                .relecteurId(choisi)
                .build());
        return RelectureResponse.from(relecture);
    }

    /** RG7 / Q7 : le relecteur est choisi au hasard parmi les étudiants présents à la session. */
    private Long choisirAuHasard(Exercice exercice) {
        List<Long> presents = presenceRepository.findBySessionIdOrderByCreatedAtAsc(exercice.getSessionId())
                .stream()
                .map(p -> p.getEtudiantId())
                .filter(id -> !id.equals(exercice.getEtudiantId()))
                .toList();
        if (presents.isEmpty()) {
            throw new ApiBusinessException("AUCUN_RELECTEUR_DISPONIBLE",
                    "Aucun autre étudiant présent à cette session.", HttpStatus.CONFLICT);
        }
        return presents.get(random.nextInt(presents.size()));
    }

    /** RG2 / Q5 : interdiction de relire son propre exercice. */
    private void verifierElegibilite(Exercice exercice, Session session, Long relecteurId) {
        if (!studentRepository.existsById(relecteurId)) {
            throw new ResourceNotFoundException("ETUDIANT_INCONNU", "Étudiant introuvable");
        }
        if (exercice.getEtudiantId().equals(relecteurId)) {
            throw new ApiBusinessException("AUTO_RELECTURE",
                    "Impossible de relire son propre exercice.", HttpStatus.FORBIDDEN);
        }
        if (!presenceRepository.existsBySessionIdAndEtudiantId(session.getId(), relecteurId)) {
            throw new ApiBusinessException("RELECTEUR_ABSENT",
                    "Le relecteur doit être présent à la session.", HttpStatus.CONFLICT);
        }
    }

    @Override
    @Transactional
    public RelectureResponse rendre(Long id, RelectureRequest request) {
        Relecture relecture = relectureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RELECTURE_INCONNUE", "Relecture introuvable"));
        Exercice exercice = exerciceRepository.findById(relecture.getExerciceId())
                .orElseThrow(() -> new ResourceNotFoundException("EXERCICE_INCONNU", "Exercice introuvable"));
        Session session = sessionRepository.findById(exercice.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("SESSION_INCONNUE", "Session introuvable"));

        if (session.getClotee()) {
            throw new ApiBusinessException("SESSION_CLOTUREE", "La session est clôturée.",
                    HttpStatus.CONFLICT);
        }
        if (relecture.getNote() != null) {
            throw new ApiBusinessException("RELECTURE_DEJA_RENDUE",
                    "Une relecture a déjà été rendue pour cet exercice.", HttpStatus.CONFLICT);
        }

        relecture.setNote(request.getNote());
        relecture.setCommentaire(request.getCommentaire() != null ? request.getCommentaire() : "");
        Relecture saved = relectureRepository.save(relecture);

        exercice.setStatut(StatutsExercice.RELU);
        exerciceRepository.save(exercice);

        return RelectureResponse.from(saved);
    }

    @Override
    @Transactional
    public RelectureResponse corriger(Long id, RelectureRequest request) {
        Relecture relecture = relectureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RELECTURE_INCONNUE", "Relecture introuvable"));
        Exercice exercice = exerciceRepository.findById(relecture.getExerciceId())
                .orElseThrow(() -> new ResourceNotFoundException("EXERCICE_INCONNU", "Exercice introuvable"));
        Session session = sessionRepository.findById(exercice.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("SESSION_INCONNUE", "Session introuvable"));

        // RG9 / Q10 : correction possible tant que le formateur n'a pas clôturé la session
        if (session.getClotee()) {
            throw new ApiBusinessException("RELECTURE_VALIDEE",
                    "La session est clôturée : la relecture est définitive.", HttpStatus.CONFLICT);
        }
        if (relecture.getNote() == null) {
            throw new ApiBusinessException("RELECTURE_NON_RENDUE",
                    "Rendez d'abord la relecture avant de la corriger.", HttpStatus.CONFLICT);
        }

        relecture.setNote(request.getNote());
        relecture.setCommentaire(request.getCommentaire() != null ? request.getCommentaire() : "");
        return RelectureResponse.from(relectureRepository.save(relecture));
    }

    @Override
    public RelectureResponse findById(Long id) {
        return RelectureResponse.from(relectureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RELECTURE_INCONNUE", "Relecture introuvable")));
    }

    @Override
    public RelectureResponse findByExerciceId(Long exerciceId) {
        return RelectureResponse.from(relectureRepository.findByExerciceId(exerciceId)
                .orElseThrow(() -> new ResourceNotFoundException("RELECTURE_INCONNUE",
                        "Pas de relecture pour cet exercice")));
    }

    @Override
    public List<RelectureResponse> findEnAttente(Long relecteurId) {
        return relectureRepository.findByRelecteurIdAndNoteIsNull(relecteurId).stream()
                .map(RelectureResponse::from)
                .toList();
    }
}
