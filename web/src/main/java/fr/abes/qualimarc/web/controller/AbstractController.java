package fr.abes.qualimarc.web.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class AbstractController {
    protected ResponseEntity<Object> buildResponseEntity(Object message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        if (message instanceof String) {
            return new ResponseEntity<>(new Message((String)message), headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    private class Message {
        @JsonProperty("message")
        private String message;

        public Message(String message) {
            this.message = message;
        }
    }
}
