package com.kfokam.kos.service;

import com.kfokam.kos.dto.ExerciceRequest;
import com.kfokam.kos.dto.ExerciceResponse;
import java.util.List;

public interface ExerciceService {

    List<ExerciceResponse> findAllBySessionId(Long sessionId);

    ExerciceResponse findById(Long id);

    ExerciceResponse findBySessionAndEtudiant(Long sessionId, Long etudiantId);

    List<ExerciceResponse> findByEtudiantId(Long etudiantId);

    ExerciceResponse create(ExerciceRequest request);

    ExerciceResponse updateLink(Long id, String lien);

    List<ExerciceResponse> findBySessionIdAndStatut(Long sessionId, String statut);

    long countBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);
}
