package com.kfokam.kos.controller;

import com.kfokam.kos.dto.TableauResponse;
import com.kfokam.kos.service.TableauService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrat imposé (annexe B) : GET /api/tableau?promotionId=
 * 200 [ { etudiantId, nom, presences, exercicesDeposes, moyenne, relecturesEnAttente } ]
 * 404 promotion inconnue.
 */
@RestController
@Tag(name = "Tableau", description = "Tableau récapitulatif du formateur")
public class TableauController {

    private final TableauService tableauService;

    public TableauController(TableauService tableauService) {
        this.tableauService = tableauService;
    }

    @GetMapping("/api/tableau")
    @Operation(summary = "Le tableau du formateur : présence, dépôts, moyenne, relectures en attente")
    public List<TableauResponse> tableau(@RequestParam Long promotionId) {
        return tableauService.construire(promotionId);
    }
}
