package nl.jnext.workshop.testcontainers.vakantieplanner.model;

import java.time.LocalDate;

public record Holiday(int id, String description, LocalDate from, LocalDate to) {
}
