package com.kfokam.kos.controller;

import com.kfokam.kos.dto.PresenceResponse;
import com.kfokam.kos.dto.PresenceRequest;
import com.kfokam.kos.dto.ManualPresenceRequest;
import com.kfokam.kos.service.PresenceService;
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
@RequestMapping("/api/presences")
@Tag(name = "Présences", description = "Gestion des présences des étudiants")
public class PresenceController {

    private final PresenceService presenceService;

    public PresenceController(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    @PostMapping
    @Operation(summary = "Marquer sa présence avec un code")
    @ResponseStatus(HttpStatus.CREATED)
    public PresenceResponse create(@RequestBody PresenceRequest request) {
        return presenceService.markPresence(request.getCode(), request.getEtudiantId());
    }

    @PostMapping("/{id}/manual")
    @Operation(summary = "Ajouter une présence à la main (formateur)")
    @ResponseStatus(HttpStatus.CREATED)
    public PresenceResponse createManual(@PathVariable Long id, @RequestBody ManualPresenceRequest request) {
        return presenceService.addManualPresence(id, request.getEtudiantId());
    }

    @GetMapping
    @Operation(summary = "Lister les présences d'une session")
    public List<PresenceResponse> getAll(@RequestParam Long sessionId) {
        return presenceService.findAllBySessionId(sessionId);
    }
}
