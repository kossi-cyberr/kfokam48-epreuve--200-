package com.kfokam.kos.repository;

import com.kfokam.kos.model.Relecture;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelectureRepository extends JpaRepository<Relecture, Long> {

    /** Toutes les relectures d'un exercice (1 ou 2 depuis l'enveloppe : deux relecteurs). */
    List<Relecture> findByExerciceId(Long exerciceId);

    boolean existsByExerciceId(Long exerciceId);

    List<Relecture> findByExerciceIdIn(List<Long> exerciceIds);

    /** Relectures assignées à un relecteur. */
    List<Relecture> findByRelecteurId(Long relecteurId);

    /** Relectures assignées mais pas encore rendues (note en attente). */
    List<Relecture> findByRelecteurIdAndNoteIsNull(Long relecteurId);

    long countByRelecteurIdAndNoteIsNull(Long relecteurId);
}
