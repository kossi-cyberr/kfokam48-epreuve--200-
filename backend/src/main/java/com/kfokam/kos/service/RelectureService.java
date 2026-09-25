package com.kfokam.kos.service;

import com.kfokam.kos.dto.RelectureRequest;
import com.kfokam.kos.dto.RelectureResponse;
import java.util.List;

public interface RelectureService {

    /** Q6/Q7 : assigne au hasard un relecteur parmi les étudiants présents à la session. */
    RelectureResponse assigner(Long exerciceId, Long relecteurId);

    /** Contrat imposé : POST /api/relectures/{id} — le relecteur rend note et commentaire. */
    RelectureResponse rendre(Long id, RelectureRequest request);

    /** Q10/RG9 : le relecteur peut corriger sa note tant que le formateur n'a pas clôturé. */
    RelectureResponse corriger(Long id, RelectureRequest request);

    RelectureResponse findById(Long id);

    RelectureResponse findByExerciceId(Long exerciceId);

    /** Toutes les relectures assignées à un relecteur (à rendre + rendues, Q10/Q16). */
    List<RelectureResponse> findParRelecteur(Long relecteurId);
}
