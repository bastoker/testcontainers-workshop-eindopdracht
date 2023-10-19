package nl.jnext.workshop.testcontainers.vakantieplanner.controller;

import io.swagger.v3.oas.annotations.Parameter;
import nl.jnext.workshop.testcontainers.vakantieplanner.controller.auth.CurrentUser;
import nl.jnext.workshop.testcontainers.vakantieplanner.controller.exceptions.NoMatchingUsernamesException;
import nl.jnext.workshop.testcontainers.vakantieplanner.controller.exceptions.OverlappingHolidayException;
import nl.jnext.workshop.testcontainers.vakantieplanner.dao.VakantieRepository;
import nl.jnext.workshop.testcontainers.vakantieplanner.model.Holiday;
import nl.jnext.workshop.testcontainers.vakantieplanner.model.MemberWithHolidays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/holiday-admin")
public class HolidayAdminController extends AbstractController {

    Logger logger = LoggerFactory.getLogger(HolidayAdminController.class);

    @Autowired
    VakantieRepository vakantieRepository;

    @GetMapping(path = "/all", produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('superuser')")
    public List<MemberWithHolidays> getHolidays(
            @Parameter(hidden = true) @CurrentUser String keycloakUser
    ) {
        logger.info("Admin Endpoint GET /holidays/all is called as keycloak user {}", keycloakUser);
        return vakantieRepository.retrieveAllHolidays();
    }
}
