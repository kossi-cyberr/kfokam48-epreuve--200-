package com.kfokam.kos.exception;

import java.time.Instant;
import java.util.Map;

/**
 * Corps d'erreur standardisé retourné par le {@link GlobalExceptionHandler}.
 * Format stable pour que le front puisse lire le message d'erreur.
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors) {

    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(Instant.now(), status, error, message, path, Map.of());
    }
}
