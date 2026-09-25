package com.kfokam.kos.controller;

import com.kfokam.kos.dto.ItemRequest;
import com.kfokam.kos.dto.ItemResponse;
import com.kfokam.kos.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * Couche API : validation + renvoi des DTO. Aucune logique métier ici,
 * tout est délégué au {@link ItemService}.
 */
@RestController
@RequestMapping("/api/items")
@Tag(name = "Items", description = "CRUD des items (exemple à adapter au sujet)")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    @Operation(summary = "Lister tous les items")
    public List<ItemResponse> getAll() {
        return itemService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un item par son id")
    public ItemResponse getById(@PathVariable Long id) {
        return itemService.findById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des items par nom")
    public List<ItemResponse> search(@RequestParam String name) {
        return itemService.search(name);
    }

    @PostMapping
    @Operation(summary = "Créer un item")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse create(@Valid @RequestBody ItemRequest request) {
        return itemService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un item")
    public ItemResponse update(@PathVariable Long id, @Valid @RequestBody ItemRequest request) {
        return itemService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un item")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
