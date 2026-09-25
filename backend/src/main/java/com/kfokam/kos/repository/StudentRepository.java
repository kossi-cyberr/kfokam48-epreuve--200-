package com.kfokam.kos.repository;

import com.kfokam.kos.model.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByMatricule(String matricule);

    List<Student> findByNomContainingIgnoreCase(String nom);

    List<Student> findByIdIn(List<Long> ids);

    List<Student> findByPromotionId(Long promotionId);
}
