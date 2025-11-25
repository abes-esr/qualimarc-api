package fr.abes.qualimarc.web.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

/**
 * Représente une erreur au format JSON de l'API
 */
@Data
public class ApiReturnError {
    private HttpStatusCode status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String message;
    private String debugMessage;


    private ApiReturnError() {
        timestamp = LocalDateTime.now();
    }

    ApiReturnError(HttpStatusCode status) {
        this();
        this.status = status;
    }

    ApiReturnError(HttpStatusCode status, Throwable ex) {
        this();
        this.status = status;
        this.message = "Unexpected error";
        this.debugMessage = ex.getLocalizedMessage();
    }

    ApiReturnError(HttpStatusCode status, String message, Throwable ex) {
        this();
        this.status = status;
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
    }

    ApiReturnError(HttpStatusCode status, String message) {
        this();
        this.status = status;
        this.message = message;

    }

}
