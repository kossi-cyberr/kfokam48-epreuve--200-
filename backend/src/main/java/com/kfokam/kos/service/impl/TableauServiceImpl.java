package com.kfokam.kos.service.impl;

import com.kfokam.kos.dto.TableauResponse;
import com.kfokam.kos.exception.ResourceNotFoundException;
import com.kfokam.kos.model.Exercice;
import com.kfokam.kos.model.StatutsExercice;
import com.kfokam.kos.repository.ExerciceRepository;
import com.kfokam.kos.repository.PresenceRepository;
import com.kfokam.kos.repository.PromotionRepository;
import com.kfokam.kos.repository.RelectureRepository;
import com.kfokam.kos.repository.StudentRepository;
import com.kfokam.kos.service.TableauService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Q16 : pour chaque étudiant — présence à chaque session, exercices déposés,
 * moyenne des notes reçues, relectures qu'il doit encore faire.
 * La moyenne est calculée ICI : le frontend ne recalcule aucune règle métier (F3, RG15).
 *
 * Enveloppe (étape 3) : deux relecteurs par exercice. La moyenne est marquée
 * PROVISOIRE tant qu'au moins un exercice noté de l'étudiant n'a reçu qu'une
 * seule des deux relectures (statut PROVISOIRE).
 */
@Service
public class TableauServiceImpl implements TableauService {

    private final PromotionRepository promotionRepository;
    private final StudentRepository studentRepository;
    private final PresenceRepository presenceRepository;
    private final ExerciceRepository exerciceRepository;
    private final RelectureRepository relectureRepository;

    public TableauServiceImpl(PromotionRepository promotionRepository,
                              StudentRepository studentRepository,
                              PresenceRepository presenceRepository,
                              ExerciceRepository exerciceRepository,
                              RelectureRepository relectureRepository) {
        this.promotionRepository = promotionRepository;
        this.studentRepository = studentRepository;
        this.presenceRepository = presenceRepository;
        this.exerciceRepository = exerciceRepository;
        this.relectureRepository = relectureRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableauResponse> construire(Long promotionId) {
        promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("PROMOTION_INCONNUE", "Promotion introuvable"));

        return studentRepository.findByPromotionId(promotionId).stream()
                .map(etudiant -> {
                    long presences = presenceRepository.countByEtudiantId(etudiant.getId());
                    long exercices = exerciceRepository.countByEtudiantId(etudiant.getId());

                    List<Exercice> sesExercices = exerciceRepository.findByEtudiantId(etudiant.getId());
                    List<Long> exercicesIds = sesExercices.stream().map(Exercice::getId).toList();

                    Double moyenne = null;
                    boolean moyenneProvisoire = false;
                    long relecturesEnAttente = 0;

                    if (!exercicesIds.isEmpty()) {
                        List<Integer> notes = relectureRepository.findByExerciceIdIn(exercicesIds).stream()
                                .map(r -> r.getNote())
                                .filter(n -> n != null)
                                .toList();
                        if (!notes.isEmpty()) {
                            moyenne = notes.stream().mapToInt(Integer::intValue).average().orElse(0.0);
                            // Enveloppe : provisoire si un exercice n'a reçu qu'une seule des 2 relectures
                            moyenneProvisoire = sesExercices.stream()
                                    .anyMatch(e -> StatutsExercice.PROVISOIRE.equals(e.getStatut()));
                        }
                    }
                    relecturesEnAttente = relectureRepository.countByRelecteurIdAndNoteIsNull(etudiant.getId());

                    return TableauResponse.builder()
                            .etudiantId(etudiant.getId())
                            .nom(etudiant.getPrenom() + " " + etudiant.getNom())
                            .presences((int) presences)
                            .exercicesDeposes((int) exercices)
                            .moyenne(moyenne)
                            .moyenneProvisoire(moyenneProvisoire)
                            .relecturesEnAttente((int) relecturesEnAttente)
                            .build();
                })
                .toList();
    }
}
