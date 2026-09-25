package com.kfokam.kos.repository;

import com.kfokam.kos.model.Exercice;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciceRepository extends JpaRepository<Exercice, Long> {

    List<Exercice> findBySessionIdOrderByCreatedAtAsc(Long sessionId);

    List<Exercice> findByEtudiantIdOrderByCreatedAtAsc(Long etudiantId);

    List<Exercice> findBySessionIdAndStatutOrderByCreatedAtAsc(Long sessionId, String statut);

    Optional<Exercice> findBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    boolean existsBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    long countBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    long countByEtudiantId(Long etudiantId);

    List<Exercice> findByEtudiantId(Long etudiantId);
}
