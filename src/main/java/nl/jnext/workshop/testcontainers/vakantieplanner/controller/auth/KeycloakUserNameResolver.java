package nl.jnext.workshop.testcontainers.vakantieplanner.controller.auth;

import nl.jnext.workshop.testcontainers.vakantieplanner.controller.HolidayController;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class KeycloakUserNameResolver {
    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserNameResolver.class);
    /**
     * Credits to https://stackoverflow.com/a/60012403
     */
    @Bean
    public Function<Object, String> fetchUser() {
        return (principal -> {
            if (principal instanceof KeycloakPrincipal p) {
                AccessToken token = p.getKeycloakSecurityContext().getToken();
                return token.getPreferredUsername();
            } else if (principal instanceof String s) {
                logger.warn("Principal object is of type String: {}", s);
                return "[user name could not be determined viaa KeyCloak Principle, user object was '%s']"
                        .formatted(s);
            } else {
                logger.error("Principal object is of unknown type");
                throw new IllegalStateException("Principal object is of unknown type");
            }
        });
    }
}

