package com.kfokam.kos.exception;

import org.springframework.http.HttpStatus;

/**
 * Base de toutes les erreurs métier : porte le code d'erreur stable
 * (ex. CODE_EXPIRE) et le statut HTTP imposé par le contrat api/contrat.yaml.
 * Le corps renvoyé au client est toujours { code, message }.
 */
public abstract class BusinessException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    protected BusinessException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
