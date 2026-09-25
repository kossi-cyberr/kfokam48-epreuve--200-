package com.kfokam.kos.dto;

import com.kfokam.kos.model.Item;
import java.time.Instant;

/**
 * Réponse envoyée au client : ne jamais exposer l'entité JPA directement.
 */
public record ItemResponse(
        Long id,
        String name,
        String description,
        Integer quantity,
        Instant createdAt) {

    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getQuantity(),
                item.getCreatedAt());
    }
}
