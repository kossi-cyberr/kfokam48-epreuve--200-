package com.kfokam.kos.exception;

/**
 * Ressource introuvable : renvoie toujours 404 au format { code, message }.
 * Le code stable (ex. PROMOTION_INCONNUE) est celui documenté dans api/contrat.yaml.
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String code;

    /** Constructeur principal : code stable + message lisible. */
    public ResourceNotFoundException(String code, String message) {
        super(message);
        this.code = code;
    }

    /** Variante technique : ressource + identifiant. */
    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " introuvable (id=" + id + ")");
        this.code = "NOT_FOUND";
    }

    public String getCode() {
        return code;
    }
}
