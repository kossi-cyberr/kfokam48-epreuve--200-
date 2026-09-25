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

/**
 * Enveloppe (étape 3) : chaque exercice est relu par DEUX relecteurs différents.
 * Un seul des deux a rendu => exercice PROVISOIRE (note affichée mais provisoire).
 * Les deux ont rendu => RELEVE, la note retenue est la moyenne des deux.
 */
@Service
public class RelectureServiceImpl implements RelectureService {

    /** Nombre de relecteurs par exercice (changement de besoin du client). */
    static final int NB_RELECTEURS = 2;

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

        List<Relecture> existantes = relectureRepository.findByExerciceId(exerciceId);
        if (existantes.size() >= NB_RELECTEURS) {
            throw new ApiBusinessException("DEUX_RELECTEURS_DEJA_ASSIGNE",
                    "Les deux relecteurs de cet exercice sont déjà désignés.", HttpStatus.CONFLICT);
        }

        Long choisi = (relecteurId != null) ? relecteurId : choisirAuHasard(exercice, existantes);
        verifierElegibilite(exercice, session, choisi, existantes);

        Relecture relecture = relectureRepository.save(Relecture.builder()
                .exerciceId(exerciceId)
                .relecteurId(choisi)
                .build());
        return RelectureResponse.from(relecture);
    }

    /**
     * Tirage au hasard parmi les étudiants présents à la session, en excluant
     * l'auteur de l'exercice (RG2/Q5) et les relecteurs déjà assignés.
     */
    private Long choisirAuHasard(Exercice exercice, List<Relecture> existantes) {
        List<Long> dejaAssignes = existantes.stream().map(Relecture::getRelecteurId).toList();
        List<Long> eligibles = presenceRepository.findBySessionIdOrderByCreatedAtAsc(exercice.getSessionId())
                .stream()
                .map(p -> p.getEtudiantId())
                .filter(id -> !id.equals(exercice.getEtudiantId()))
                .filter(id -> !dejaAssignes.contains(id))
                .toList();
        if (eligibles.isEmpty()) {
            throw new ApiBusinessException("AUCUN_RELECTEUR_DISPONIBLE",
                    "Aucun autre étudiant présent à cette session.", HttpStatus.CONFLICT);
        }
        return eligibles.get(random.nextInt(eligibles.size()));
    }

    /** RG2 / Q5 : interdiction de relire son propre exercice. */
    private void verifierElegibilite(Exercice exercice, Session session, Long relecteurId,
                                     List<Relecture> existantes) {
        if (!studentRepository.existsById(relecteurId)) {
            throw new ResourceNotFoundException("ETUDIANT_INCONNU", "Étudiant introuvable");
        }
        if (exercice.getEtudiantId().equals(relecteurId)) {
            throw new ApiBusinessException("AUTO_RELECTURE",
                    "Impossible de relire son propre exercice.", HttpStatus.FORBIDDEN);
        }
        boolean dejaAssignee = existantes.stream()
                .anyMatch(r -> r.getRelecteurId().equals(relecteurId));
        if (dejaAssignee) {
            throw new ApiBusinessException("RELECTEUR_DEJA_ASSIGNE",
                    "Cet étudiant relit déjà cet exercice.", HttpStatus.CONFLICT);
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
                    "Ce relecteur a déjà rendu sa relecture.", HttpStatus.CONFLICT);
        }

        relecture.setNote(request.getNote());
        relecture.setCommentaire(request.getCommentaire() != null ? request.getCommentaire() : "");
        Relecture saved = relectureRepository.save(relecture);

        reevaluerStatut(exercice);
        return RelectureResponse.from(saved);
    }

    /** PROVISOIRE quand un seul des deux a rendu, RELEVE quand les deux ont rendu. */
    private void reevaluerStatut(Exercice exercice) {
        long rendues = relectureRepository.findByExerciceId(exercice.getId()).stream()
                .filter(r -> r.getNote() != null)
                .count();
        if (rendues >= NB_RELECTEURS) {
            exercice.setStatut(StatutsExercice.RELEVE);
        } else {
            exercice.setStatut(StatutsExercice.PROVISOIRE);
        }
        exerciceRepository.save(exercice);
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
        Relecture saved = relectureRepository.save(relecture);

        reevaluerStatut(exercice);
        return RelectureResponse.from(saved);
    }

    @Override
    public RelectureResponse findById(Long id) {
        return RelectureResponse.from(relectureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RELECTURE_INCONNUE", "Relecture introuvable")));
    }

    @Override
    public RelectureResponse findByExerciceId(Long exerciceId) {
        return relectureRepository.findByExerciceId(exerciceId).stream()
                .filter(r -> r.getNote() != null)
                .findFirst()
                .map(RelectureResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("RELECTURE_INCONNUE",
                        "Pas de relecture rendue pour cet exercice"));
    }

    @Override
    public List<RelectureResponse> findAllByExerciceId(Long exerciceId) {
        return relectureRepository.findByExerciceId(exerciceId).stream()
                .map(RelectureResponse::from)
                .toList();
    }

    @Override
    public List<RelectureResponse> findParRelecteur(Long relecteurId) {
        return relectureRepository.findByRelecteurId(relecteurId).stream()
                .map(RelectureResponse::from)
                .toList();
    }
}
