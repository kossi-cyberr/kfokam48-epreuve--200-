package com.kfokam.kos.controller;

import com.kfokam.kos.dto.RelectureRequest;
import com.kfokam.kos.dto.RelectureResponse;
import com.kfokam.kos.service.RelectureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relectures")
@Tag(name = "Relectures", description = "Gestion des relectures des exercices")
public class RelectureController {

    private final RelectureService relectureService;

    public RelectureController(RelectureService relectureService) {
        this.relectureService = relectureService;
    }

    @PostMapping
    @Operation(summary = "Rendre une note et un commentaire")
    @ResponseStatus(HttpStatus.CREATED)
    public RelectureResponse create(@RequestBody RelectureRequest request) {
        return relectureService.create(request);
    }

    @GetMapping("/exercice/{id}")
    @Operation(summary = "Récupérer la relecture d'un exercice")
    public RelectureResponse getByExercice(@PathVariable Long id) {
        return relectureService.findByExerciceId(id);
    }

    @GetMapping
    @Operation(summary = "Lister les relectures d'un exercice")
    public List<RelectureResponse> getAll(@RequestParam Long exerciceId) {
        return relectureService.findAllByExerciceId(exerciceId);
    }
}
