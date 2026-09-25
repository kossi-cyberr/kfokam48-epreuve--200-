package com.kfokam.kos.service;

import com.kfokam.kos.dto.RelectureRequest;
import com.kfokam.kos.dto.RelectureResponse;
import java.util.List;

public interface RelectureService {

    RelectureResponse create(RelectureRequest request);

    RelectureResponse findById(Long id);

    RelectureResponse findByExerciceId(Long exerciceId);

    List<RelectureResponse> findAllByExerciceId(Long exerciceId);

    boolean existsByExerciceId(Long exerciceId);

    boolean canModify(Long relectureId, Long sessionId);
}
