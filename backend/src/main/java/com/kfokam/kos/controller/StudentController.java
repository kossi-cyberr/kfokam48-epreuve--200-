package com.kfokam.kos.controller;

import com.kfokam.kos.dto.StudentResponse;
import com.kfokam.kos.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Étudiants", description = "Gestion des étudiants")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    @Operation(summary = "Lister les étudiants")
    public List<StudentResponse> getAll() {
        return studentService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un étudiant")
    public StudentResponse getById(@PathVariable Long id) {
        return studentService.findById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des étudiants par nom")
    public List<StudentResponse> search(@RequestParam String nom) {
        return studentService.findAll();
    }

    @PostMapping
    @Operation(summary = "Créer un étudiant")
    public StudentResponse create(@RequestBody StudentResponse request) {
        return studentService.create(request.getPrenom(), request.getNom(), request.getMatricule());
    }
}
