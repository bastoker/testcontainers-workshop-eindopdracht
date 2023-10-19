package nl.jnext.workshop.testcontainers.vakantieplanner.controller;

import io.swagger.v3.oas.annotations.Parameter;
import nl.jnext.workshop.testcontainers.vakantieplanner.controller.auth.CurrentUser;
import nl.jnext.workshop.testcontainers.vakantieplanner.controller.exceptions.ForbiddenException;
import nl.jnext.workshop.testcontainers.vakantieplanner.controller.exceptions.NoMatchingUsernamesException;
import nl.jnext.workshop.testcontainers.vakantieplanner.controller.exceptions.OverlappingHolidayException;
import nl.jnext.workshop.testcontainers.vakantieplanner.dao.VakantieRepository;
import nl.jnext.workshop.testcontainers.vakantieplanner.model.Holiday;
import nl.jnext.workshop.testcontainers.vakantieplanner.model.MemberWithHolidays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/holiday")
public class HolidayController extends AbstractController {

    Logger logger = LoggerFactory.getLogger(HolidayController.class);

    @Autowired
    VakantieRepository vakantieRepository;

    private void checkMatchingUsernames(String user, String keycloakUser) {
        if (user == null || keycloakUser == null) {
            throw new IllegalStateException("null args found");
        }
        if (!user.equals(keycloakUser)) {
            logger.error("Given username {} does not match keycloak username {}", user, keycloakUser);
            throw new NoMatchingUsernamesException("Given username does not match keycloak username");
        }
    }

    @DeleteMapping(path = "/{user}/{holiday-id}")
    @PreAuthorize("hasAnyRole('user', 'superuser')")
    public void deleteHoliday(@Parameter(hidden = true) @CurrentUser String keycloakUser,
                              @PathVariable("user") String user,
                              @PathVariable("holiday-id") int holidayId
    ) {
        logger.info("Endpoint DELETE /holiday/{}/{} is called as keycloak user {}", user, holidayId, keycloakUser);
        checkMatchingUsernames(user, keycloakUser);
        vakantieRepository.deleteHoliday(holidayId);
    }

    @PostMapping(path = "/{user}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('user', 'superuser')")
    public Holiday addHoliday(
            @Parameter(hidden = true) @CurrentUser String keycloakUser,
            @PathVariable("user") String user,
            @RequestBody Holiday holiday) {
        logger.info("Endpoint POST /holiday/{} is called as keycloak user {}", user, keycloakUser);
        checkMatchingUsernames(user, keycloakUser);
        return vakantieRepository.addHoliday(user, holiday);
    }

    @GetMapping(path = "/{user}", produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('user', 'superuser')")
    public List<Holiday> getHolidays(
            @Parameter(hidden = true) @CurrentUser String keycloakUser,
            @PathVariable("user") String user
    ) {
        logger.info("Endpoint GET /holiday/{} is called as keycloak user {}", user, keycloakUser);
        checkMatchingUsernames(user, keycloakUser);
        return vakantieRepository.retrieveHolidaysForMemberWithName(user);
    }

    @GetMapping(path = "/all", produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('user', 'superuser')")
    public List<MemberWithHolidays> getHolidays(
            @Parameter(hidden = true) @CurrentUser String keycloakUser
    ) {
        logger.info("Admin Endpoint GET /holidays/all is called as keycloak user {}", keycloakUser);
        return vakantieRepository.retrieveAllHolidays();
    }

    @GetMapping(path = "/{user}/{holiday-id}", produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('user', 'superuser')")
    public Holiday getSingleHoliday(
            @Parameter(hidden = true) @CurrentUser String keycloakUser,
            @PathVariable("user") String user,
            @PathVariable("holiday-id") int holidayId
    ) {
        logger.info("Endpoint GET /holiday/{}/{} is called as keycloak user {}", user, holidayId, keycloakUser);
        checkMatchingUsernames(user, keycloakUser);
        return vakantieRepository.retrieveHoliday(holidayId);
    }

    @PutMapping(path = "/{user}/{holiday-id}", consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('user', 'superuser')")
    public void updateSingleHoliday(
            @Parameter(hidden = true) @CurrentUser String keycloakUser,
            @PathVariable("user") String user,
            @PathVariable("holiday-id") int holidayId,
            @RequestBody Holiday holiday
    ) {
        logger.info("Endpoint PUT /holiday/{}/{} is called as keycloak user {}", user, holidayId, keycloakUser);
        checkMatchingUsernames(user, keycloakUser);
        if (holidayId != holiday.id()) {
            logger.error("Id of holiday does not match request path param");
            throw new IllegalArgumentException("Id of holiday does not match request path param");
        }
        vakantieRepository.updateHoliday(user, holiday);
    }
}
