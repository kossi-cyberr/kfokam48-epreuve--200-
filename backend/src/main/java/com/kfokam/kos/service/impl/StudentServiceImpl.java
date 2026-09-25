package com.kfokam.kos.service.impl;

import com.kfokam.kos.dto.StudentResponse;
import com.kfokam.kos.exception.ResourceNotFoundException;
import com.kfokam.kos.model.Student;
import com.kfokam.kos.repository.StudentRepository;
import com.kfokam.kos.service.StudentService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public List<StudentResponse> findAll() {
        return studentRepository.findAll().stream()
                .map(StudentResponse::from)
                .toList();
    }

    @Override
    public StudentResponse findById(Long id) {
        return StudentResponse.from(getStudent(id));
    }

    @Override
    @Transactional
    public StudentResponse create(String prenom, String nom, String matricule) {
        return StudentResponse.from(studentRepository.save(Student.builder()
                .prenom(prenom.trim())
                .nom(nom.trim())
                .matricule(matricule.trim())
                .build()));
    }

    @Override
    public StudentResponse findByMatricule(String matricule) {
        return StudentResponse.from(studentRepository.findByMatricule(matricule)
                .orElseThrow(() -> new ResourceNotFoundException("ETUDIANT_INCONNU", "Étudiant introuvable")));
    }

    private Student getStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ETUDIANT_INCONNU", "Étudiant introuvable"));
    }
}
