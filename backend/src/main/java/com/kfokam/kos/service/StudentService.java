package com.kfokam.kos.service;

import com.kfokam.kos.dto.StudentResponse;
import java.util.List;

public interface StudentService {

    List<StudentResponse> findAll();

    StudentResponse findById(Long id);

    StudentResponse create(String prenom, String nom, String matricule);

    StudentResponse findByMatricule(String matricule);
}
