package com.kfokam.kos.controller;

import com.kfokam.kos.dto.RelectureRequest;
import com.kfokam.kos.dto.RelectureResponse;
import com.kfokam.kos.dto.AssignationRequest;
import com.kfokam.kos.service.RelectureService;
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

/**
 * Contrat imposé (annexe B) : POST /api/relectures/{id} avec { note, commentaire }.
 * Le {id} est celui de la relecture ASSIGNÉE au relecteur (V3) :
 * l'assignation se fait via POST /api/relectures/{exerciceId}/assignation.
 */
@RestController
@RequestMapping("/api/relectures")
@Tag(name = "Relectures", description = "Assignation et rendu des relectures")
public class RelectureController {

    private final RelectureService relectureService;

    public RelectureController(RelectureService relectureService) {
        this.relectureService = relectureService;
    }

    @PostMapping("/{exerciceId}/assignation")
    @Operation(summary = "Assigner un relecteur (au hasard parmi les présents, Q6/Q7)")
    @ResponseStatus(HttpStatus.CREATED)
    public RelectureResponse assigner(@PathVariable Long exerciceId,
                                      @RequestBody(required = false) AssignationRequest request) {
        Long relecteurId = request != null ? request.getRelecteurId() : null;
        return relectureService.assigner(exerciceId, relecteurId);
    }

    @PostMapping("/{id}")
    @Operation(summary = "Le relecteur rend sa note et son commentaire (contrat imposé)")
    public RelectureResponse rendre(@PathVariable Long id, @Valid @RequestBody RelectureRequest request) {
        return relectureService.rendre(id, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Le relecteur corrige sa relecture tant que la session n'est pas clôturée (Q10)")
    public RelectureResponse corriger(@PathVariable Long id, @Valid @RequestBody RelectureRequest request) {
        return relectureService.corriger(id, request);
    }

    @GetMapping(params = "relecteurId")
    @Operation(summary = "Relectures en attente pour un relecteur (Q11, Q16)")
    public List<RelectureResponse> enAttente(@RequestParam Long relecteurId) {
        return relectureService.findEnAttente(relecteurId);
    }

    @GetMapping(params = "exerciceId")
    @Operation(summary = "Relecture d'un exercice donné")
    public RelectureResponse getByExercice(@RequestParam Long exerciceId) {
        return relectureService.findByExerciceId(exerciceId);
    }
}
