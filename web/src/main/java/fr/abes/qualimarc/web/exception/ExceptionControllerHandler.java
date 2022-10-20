package fr.abes.qualimarc.web.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

/**
 * Gestionnaire des exceptions de l'API.
 * Cette classe récupère toutes les exceptions et renvoie un message d'erreur
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ExceptionControllerHandler extends ResponseEntityExceptionHandler {
    private ResponseEntity<Object> buildResponseEntity(ApiReturnError apiReturnError) {
        return new ResponseEntity<>(apiReturnError, apiReturnError.getStatus());
    }

    /**
     * Erreur de lecture / décodage des paramètres d'une requête HTTP
     *
     * @param ex      : l'exception catchée
     * @param headers headers de la requête http
     * @param status  status de renvoie
     * @param request requête http
     * @return l'objet du message d'erreur
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Requête YAML mal formée";
        if (ex.getCause() instanceof InvalidTypeIdException) {
            log.debug(ex.getLocalizedMessage());
            return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, new MismatchedYamlTypeException("L'attribut type ne peut prendre que les valeurs " + StringUtils.substringBetween(ex.getLocalizedMessage(), "[", "]"))));
        }
        if (ex.getCause() instanceof UnrecognizedPropertyException) {
            log.debug(ex.getLocalizedMessage());
            return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, new MismatchedYamlTypeException("La propriété " + ((UnrecognizedPropertyException) ex.getCause()).getPropertyName() + " n'existe pas dans le modèle")));
        }
        if (ex.getCause() instanceof MismatchedInputException) {
            String targetType = ((MismatchedInputException) ex.getCause()).getTargetType().getSimpleName();

            List<JsonMappingException.Reference> errors = ((MismatchedInputException) ex.getCause()).getPath();
            String property = errors.get(errors.size() - 1).getFieldName();
            log.debug(ex.getLocalizedMessage());
            if (property.equals("priorite") || property.equals("operateur-booleen")) {
                return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, new MismatchedYamlTypeException("L'attribut " + property + " ne peut prendre que les valeurs " + StringUtils.substringBetween(ex.getLocalizedMessage(), "[", "]"))));
            }
            return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, new MismatchedYamlTypeException("L'attribut " + property + " doit être de type '" + targetType + "'")));
        }

        log.error(ex.getLocalizedMessage());
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    /**
     * Erreur dans la validité des paramètres de la requête
     *
     * @param ex : l'exception catchée
     * @return l'objet du message d'erreur
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        String error = "Contenu de la requête YAML invalide";
        log.debug(ex.getLocalizedMessage());
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    /**
     * Vérifier les méthodes correspondent avec les URI dans le controller
     *
     * @param ex      : l'exception catchée
     * @param headers headers de la requête http
     * @param status  status de renvoie
     * @param request requête http
     * @return l'objet du message d'erreur
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Method is not supported for this request";
        log.error(ex.getLocalizedMessage());
        return buildResponseEntity(new ApiReturnError(HttpStatus.METHOD_NOT_ALLOWED, error, ex));
    }

    /**
     * Vérifier la validité (@Valid) des paramètres de la requête
     *
     * @param ex      : l'exception catchée
     * @param headers headers de la requête http
     * @param status  status de renvoie
     * @param request requête http
     * @return l'objet du message d'erreur
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Les paramètres ne sont pas valides";
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        String msg = "";
        for (FieldError fieldError : fieldErrors) {
            msg += fieldError.getDefaultMessage() + " ";
        }
        log.error(msg);
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    /**
     * Page 404
     *
     * @param ex      : l'exception catchée
     * @param headers headers de la requête http
     * @param status  status de renvoie
     * @param request requête http
     * @return l'objet du message d'erreur
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Page not found";
        log.error(ex.getLocalizedMessage());
        return buildResponseEntity(new ApiReturnError(HttpStatus.NOT_FOUND, error, ex));
    }

    /**
     * Erreur de paramètre
     *
     * @param ex      : l'exception catchée
     * @param headers headers de la requête http
     * @param status  status de renvoie
     * @param request requête http
     * @return l'objet du message d'erreur
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Missing request parameter";
        log.error(ex.getLocalizedMessage());
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, error, ex));
    }

    /**
     * Erreur de validation de contrainte sur un champ
     *
     * @param ex : l'exception catchée
     * @return l'objet du message d'erreur
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handle(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String errorMessage;
        if (!violations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            violations.forEach(violation -> builder.append(violation.getPropertyPath() + " " + violation.getMessage()));
            errorMessage = builder.toString();
        } else {
            errorMessage = "Une contrainte de validation n'est pas respectée";
        }
        return buildResponseEntity(new ApiReturnError(HttpStatus.BAD_REQUEST, errorMessage, ex));
    }
}
