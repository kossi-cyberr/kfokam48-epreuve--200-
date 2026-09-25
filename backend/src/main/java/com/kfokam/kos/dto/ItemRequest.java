package com.kfokam.kos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Requête de création / mise à jour d'un item.
 * Les contraintes Bean Validation sont déclarées ici, jamais dans le controller.
 */
public record ItemRequest(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 120, message = "Le nom ne peut pas dépasser 120 caractères")
        String name,

        @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
        String description,

        @Min(value = 0, message = "La quantité ne peut pas être négative")
        Integer quantity) {
}
