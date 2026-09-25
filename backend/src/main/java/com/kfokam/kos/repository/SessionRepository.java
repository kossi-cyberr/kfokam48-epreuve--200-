package com.kfokam.kos.repository;

import com.kfokam.kos.model.Session;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByPromotionIdOrderByOuvertureAtDesc(Long promotionId);

    Optional<Session> findByCode(String code);

    List<Session> findByPromotionIdAndCloteeFalse(Long promotionId);

    boolean existsByCodeAndExpirationAtAfter(String code, Instant now);
}
