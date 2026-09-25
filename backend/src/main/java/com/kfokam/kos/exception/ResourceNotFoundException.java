package com.kfokam.kos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Lever quand une ressource demandée n'existe pas (404).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " introuvable (id=" + id + ")");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
