package com.kfokam.kos.repository;

import com.kfokam.kos.model.Presence;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresenceRepository extends JpaRepository<Presence, Long> {

    Optional<Presence> findBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    List<Presence> findBySessionIdOrderByCreatedAtAsc(Long sessionId);

    long countBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    List<Presence> findBySessionIdAndCreatedAtAfter(Long sessionId, Instant instant);
}
