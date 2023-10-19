package helper;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.core.Response;

import static java.util.Collections.singletonList;

public class KeycloakTestcontainer {
    private static final String VAKANTIEPLANNER_REALM = "vakantieplanner";

    private static final String APP_CLIENT_ID = "login-app";
    public static final String NORMAL_USER_ROLE = "user";
    public static final String SUPER_USER_ROLE = "superuser";
    static KeycloakContainer keycloak = new KeycloakContainer()
            .withRealmImportFile("/kc-realm.json");

    public void start() {
        keycloak.start();
    }

    private Keycloak getKeycloakClientAsAdmin() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloak.getAuthServerUrl())
                .realm("master")
                .clientId("admin-cli")
                .username(keycloak.getAdminUsername())
                .password(keycloak.getAdminPassword())
                .build();
    }

    private Keycloak getKeycloakClientAsNormalUser(String username, String password) {
        return Keycloak.getInstance(
                keycloak.getAuthServerUrl(),
                VAKANTIEPLANNER_REALM,
                username,
                password,
                APP_CLIENT_ID);
    }

    public String getBearerTokenFor(String username, String password) {
        Keycloak kc = getKeycloakClientAsNormalUser(username, password);
        TokenManager tokenmanager = kc.tokenManager();
       return tokenmanager.getAccessTokenString();
    }

    /**
     * Add a normal user to Keycloak
     * @param name The username of the new user
     * @return userId, internal Keycloak id of the created user
     */
    public String addNormalUser(String name, String password) {
        String userId = addUserWithName(name);
        addRoleToUserWithId(userId, NORMAL_USER_ROLE);
        setPasswordForUser(userId, password);
        return userId;
    }

    public String addSuperUser(String name, String password) {
        String userId = addUserWithName(name);
        addRoleToUserWithId(userId, SUPER_USER_ROLE);
        setPasswordForUser(userId, password);
        return userId;
    }

    private String addUserWithName(String name) {
        UserRepresentation user = createUser(name);
        RealmResource realmResource = getKeycloakClientAsAdmin().realm(VAKANTIEPLANNER_REALM);
        UsersResource usersRessource = realmResource.users();

        Response response = usersRessource.create(user);
        assertCreated(response);

        System.out.printf("Response: %s %s%n", response.getStatus(), response.getStatusInfo());
        System.out.println(response.getLocation());
        String userId = CreatedResponseUtil.getCreatedId(response);
        System.out.printf("User created with userId: %s%n", userId);
        return userId;
    }

    private void setPasswordForUser(String userId, String password) {
        // Define password credential
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);

        RealmResource realmResource = getKeycloakClientAsAdmin().realm(VAKANTIEPLANNER_REALM);
        UsersResource usersRessource = realmResource.users();
        UserResource userResource = usersRessource.get(userId);

        // Set password credential
        userResource.resetPassword(passwordCred);
    }

    private void addRoleToUserWithId(String userId, String roleName) {
        RealmResource realmResource = getKeycloakClientAsAdmin().realm(VAKANTIEPLANNER_REALM);
        RoleRepresentation normalUserRealmRole = realmResource.roles().get(roleName).toRepresentation();
        UsersResource usersResource = realmResource.users();
        UserResource userResource = usersResource.get(userId);
        userResource.roles().realmLevel().add(singletonList(normalUserRealmRole));
    }

    private UserRepresentation createUser(String name) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(name);
        return user;
    }

    public void showUsersInKeycloak() {
        Keycloak kc = getKeycloakClientAsAdmin();
        int numberOfUsers = kc.realm(VAKANTIEPLANNER_REALM).users().count();
        System.out.printf("Vakantieplanner-realm has %s user(s)", numberOfUsers);
    }

    public String getAuthServerUrl() {
        return keycloak.getAuthServerUrl();
    }

    public String getAdminUsername() {
        return keycloak.getAdminUsername();
    }

    public String getAdminPassword() {
        return keycloak.getAdminPassword();
    }

    private void assertCreated(Response response) {
        if (201 != response.getStatus()) {
            throw new AssertionError("Result of call is expected to be 201, not " + response.getStatus());
        }
    }
}
