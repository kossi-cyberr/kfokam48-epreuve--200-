package com.kfokam.kos.exception;

import org.springframework.http.HttpStatus;

/**
 * Erreurs du contrat : 400 (code inconnu, lien invalide, note invalide, champ manquant),
 * 403 (auto-relecture), 409 (déjà présent, exercice déjà déposé, relecture déjà rendue),
 * 410 (code expiré).
 */
public class ApiBusinessException extends BusinessException {

    public ApiBusinessException(String code, String message, HttpStatus status) {
        super(code, message, status);
    }
}
