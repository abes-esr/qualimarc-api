package fr.abes.qualimarc.web.exception;

/**
 * Si le critère de tri n'existe pas ou s'il n'est pas autorisé
 */
public class MismatchedYamlTypeException extends Exception {

    public MismatchedYamlTypeException(final String msg) {
        super(msg);
    }

}

