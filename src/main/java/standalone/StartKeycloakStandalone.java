package standalone;

import helper.KeycloakTestcontainer;

import java.util.Scanner;

public class StartKeycloakStandalone {
    // Default admin username/password is admin/admin
    static KeycloakTestcontainer keycloak = new KeycloakTestcontainer();

    public static void main(String... args) {
        keycloak.start();
        keycloak.addNormalUser("bob", "bob");
        keycloak.addSuperUser("alice", "alice");

        System.out.println("**** Keycloak is started, with 1 normal user bob,  and superuser alice.");
        System.out.println("**** Keycloak is accessible on " + keycloak.getAuthServerUrl());
        loopEndlessly();
    }

    private static void loopEndlessly() {
        System.out.println("**** Press q and RETURN to stop Keycloak");
        Scanner userInput = new Scanner(System.in);
        while(!userInput.next().startsWith("q"));
        System.out.println("**** Keycloak server stopped");
    }
}
