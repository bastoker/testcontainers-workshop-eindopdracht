package nl.jnext.workshop.testcontainers.vakantieplanner.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Username does not match Keycloak logged in username")
public class NoMatchingUsernamesException extends RuntimeException {
    public NoMatchingUsernamesException() {
    }

    public NoMatchingUsernamesException(String message) {
        super(message);
    }
}