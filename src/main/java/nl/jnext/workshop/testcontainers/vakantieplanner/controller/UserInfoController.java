package nl.jnext.workshop.testcontainers.vakantieplanner.controller;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collection;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class UserInfoController extends AbstractController {

    @Value("${keycloak.auth-server-url}")
    private String keyCloakAuthServerUrl;

    @Value("classpath:j-next-logo.base64")
    Resource jnextLogoAsBase64;

    @GetMapping(path = "/keycloak-url", produces = APPLICATION_JSON_VALUE)
    public String getKeycloakUrl() {
        return String.format("""
                {"keycloakUrl": "%s"}
                """, keyCloakAuthServerUrl);
    }

    @GetMapping(path = "/userinfo", produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('user')")
    public String getUserInfo(KeycloakAuthenticationToken authToken) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        System.out.println(authorities);
        String userIdByToken = "";
        String name = "";

        Principal principal = (Principal) authToken.getPrincipal();
        if (principal instanceof KeycloakPrincipal<?> kcPrincipal) {
            AccessToken token = kcPrincipal.getKeycloakSecurityContext().getToken();
            name = token.getPreferredUsername();
            userIdByToken = token.getId();
        }

        return "{\"name\": \"" + name + "\", \"userIdByToken\": \"" + userIdByToken + "\"}";
    }

    @GetMapping(path = "/superuserinfo", produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('superuser')")
    public String getSuperUserInfo(KeycloakAuthenticationToken authToken) {
        String userIdByToken = "";
        String name = "";

        Principal principal = (Principal) authToken.getPrincipal();
        if (principal instanceof KeycloakPrincipal<?> kcPrincipal) {
            AccessToken token = kcPrincipal.getKeycloakSecurityContext().getToken();
            name = token.getPreferredUsername();
            userIdByToken = token.getId();
        }

        return "{\"name\": \"" + name + "\", \"userIdByToken\": \"" + userIdByToken + "\"}";
    }


    @GetMapping(path = "/swagger-ui/custom-keycloak-bearer-insert.js", produces = "application/javascript")
    public String preAuthSwaggerUIForBob() throws Exception {
        String bearerTokenForBob = getBearerTokenFor("bob", "bob");
        String logoBase64 = new String(jnextLogoAsBase64.getInputStream().readAllBytes());
        return """
                    // Credits to https://stackoverflow.com/questions/45199989/how-do-i-automatically-authorize-all-endpoints-with-swagger-ui
                    // for this workarround, er, hack.
                    // 
                    setTimeout(() => {
                    document.getElementsByClassName("topbar-wrapper")[0].getElementsByTagName("a")[0].getElementsByTagName("img")[0].src = "%s";
                      window.ui.preauthorizeApiKey("bearerAuth", "%s");
                    }, 3000);
                    
                """.formatted(logoBase64, bearerTokenForBob);
    }


    private Keycloak getKeycloakClientAsNormalUser(String username, String password) {
        return Keycloak.getInstance(
                keyCloakAuthServerUrl,
                "vakantieplanner",
                username,
                password,
                "login-app");
    }

    private String getBearerTokenFor(String username, String password) {
        Keycloak kc = getKeycloakClientAsNormalUser(username, password);
        TokenManager tokenmanager = kc.tokenManager();
        return tokenmanager.getAccessTokenString();
    }

    @GetMapping(path = "/swagger-ui/index.html", produces = "text/html")
    public String getSwaggerIndexHtml() {
        return """
                <!-- HTML for static distribution bundle build -->
                <!DOCTYPE html>
                <html lang="en">
                  <head>
                    <meta charset="UTF-8">
                    <title>Vakantieplanner API Docs - J-next ©️2022</title>
                    <link rel="stylesheet" type="text/css" href="./swagger-ui.css" />
                    <link rel="stylesheet" type="text/css" href="index.css" />
                    <link rel="icon" type="image/png" href="./favicon-32x32.png" sizes="32x32" />
                    <link rel="icon" type="image/png" href="./favicon-16x16.png" sizes="16x16" />
                  </head>
                                    
                  <body>
                    <div id="swagger-ui"></div>
                    <script src="./swagger-ui-bundle.js" charset="UTF-8"> </script>
                    <script src="./swagger-ui-standalone-preset.js" charset="UTF-8"> </script>
                    <script src="./swagger-initializer.js" charset="UTF-8"> </script>
                    <script src="./custom-keycloak-bearer-insert.js" charset="UTF-8"> </script>
                  </body>
                </html>
                """;
    }
}
