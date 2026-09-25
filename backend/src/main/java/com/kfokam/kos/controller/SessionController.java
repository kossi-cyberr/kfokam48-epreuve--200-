package com.kfokam.kos.controller;

import com.kfokam.kos.dto.SessionRequest;
import com.kfokam.kos.dto.SessionResponse;
import com.kfokam.kos.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/sessions")
@Tag(name = "Sessions", description = "Sessions de cours et codes de présence")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    @Operation(summary = "Le formateur ouvre une session et obtient un code (contrat imposé)")
    @ResponseStatus(HttpStatus.CREATED)
    public SessionResponse create(@Valid @RequestBody SessionRequest request) {
        return sessionService.open(request.getTitre(), request.getPromotionId());
    }

    @PostMapping("/{id}/cloture")
    @Operation(summary = "Le formateur clôture la session (Q12 : plus de dépôts ensuite)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cloturer(@PathVariable Long id) {
        sessionService.close(id);
    }

    @GetMapping
    @Operation(summary = "Lister les sessions d'une promotion")
    public List<SessionResponse> getAll(@RequestParam Long promotionId) {
        return sessionService.findAllByPromotionId(promotionId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une session")
    public SessionResponse getById(@PathVariable Long id) {
        return sessionService.findById(id);
    }
}
