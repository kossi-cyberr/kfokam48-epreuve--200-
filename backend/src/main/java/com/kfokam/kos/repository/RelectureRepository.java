package com.kfokam.kos.repository;

import com.kfokam.kos.model.Relecture;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelectureRepository extends JpaRepository<Relecture, Long> {

    List<Relecture> findByExerciceIdOrderByCreatedAtAsc(Long exerciceId);

    Optional<Relecture> findByExerciceId(Long exerciceId);

    long countByExerciceId(Long exerciceId);

    List<Relecture> findByExerciceIdIn(List<Long> exerciceIds);

    boolean existsByExerciceId(Long exerciceId);
}
