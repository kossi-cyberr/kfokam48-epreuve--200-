package com.kfokam.kos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Format d'erreur imposé pour TOUTES les erreurs, sans exception.
 * Une stack trace, un corps vide ou la page d'erreur par défaut de Spring
 * valent zéro sur ce critère.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {

    private String code;
    private String message;
}
