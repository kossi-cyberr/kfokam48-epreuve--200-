package com.kfokam.kos.controller;

import com.kfokam.kos.dto.PromotionResponse;
import com.kfokam.kos.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/promotions")
@Tag(name = "Promotions", description = "Gestion des promotions")
public class PromotionController {

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping
    @Operation(summary = "Lister les promotions")
    public List<PromotionResponse> getAll() {
        return promotionService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une promotion")
    public PromotionResponse getById(@PathVariable Long id) {
        return promotionService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Créer une promotion")
    public PromotionResponse create(@RequestBody PromotionResponse request) {
        return promotionService.create(request.getNom());
    }
}
