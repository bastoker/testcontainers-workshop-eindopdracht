package nl.jnext.workshop.testcontainers.vakantieplanner.controller;

import nl.jnext.workshop.testcontainers.vakantieplanner.controller.exceptions.ForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081"})
public class AbstractController {
    private static final Logger logger = LoggerFactory.getLogger(AbstractController.class);

    protected static void checkMatchingUsers(String keycloakUser, String user, String message) {
        if (! keycloakUser.equals(user)) {
            logger.warn(message, keycloakUser, user);
            throw new ForbiddenException(); // user can only see own holidays
        }
    }
}
