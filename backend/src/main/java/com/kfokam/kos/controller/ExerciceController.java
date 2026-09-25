package com.kfokam.kos.controller;

import com.kfokam.kos.dto.ExerciceRequest;
import com.kfokam.kos.dto.ExerciceResponse;
import com.kfokam.kos.dto.LienRequest;
import com.kfokam.kos.service.ExerciceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exercices")
@Tag(name = "Exercices", description = "Dépôt et remplacement des exercices")
public class ExerciceController {

    private final ExerciceService exerciceService;

    public ExerciceController(ExerciceService exerciceService) {
        this.exerciceService = exerciceService;
    }

    @PostMapping
    @Operation(summary = "L'étudiant dépose le lien de son exercice (contrat imposé)")
    @ResponseStatus(HttpStatus.CREATED)
    public ExerciceResponse create(@Valid @RequestBody ExerciceRequest request) {
        return exerciceService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Remplacer le lien tant que personne n'a relu (Q13, RG12)")
    public ExerciceResponse update(@PathVariable Long id, @Valid @RequestBody LienRequest request) {
        return exerciceService.updateLink(id, request.getLien());
    }

    @GetMapping(params = "sessionId")
    @Operation(summary = "Lister les exercices d'une session")
    public List<ExerciceResponse> getAll(@RequestParam Long sessionId) {
        return exerciceService.findAllBySessionId(sessionId);
    }

    @GetMapping(params = "etudiantId")
    @Operation(summary = "Lister les exercices d'un étudiant")
    public List<ExerciceResponse> getByEtudiant(@RequestParam Long etudiantId) {
        return exerciceService.findByEtudiantId(etudiantId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un exercice")
    public ExerciceResponse getById(@PathVariable Long id) {
        return exerciceService.findById(id);
    }
}
