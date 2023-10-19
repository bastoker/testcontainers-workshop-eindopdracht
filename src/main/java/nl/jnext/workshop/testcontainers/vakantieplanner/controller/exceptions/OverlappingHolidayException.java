package nl.jnext.workshop.testcontainers.vakantieplanner.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Requested holiday overlaps with an existing holiday")
public class OverlappingHolidayException extends RuntimeException {
}