package standalone;

import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import java.util.Scanner;

public class StartFrontendStandalone {

    @SuppressWarnings("deprecation")
    private static GenericContainer<?> ui =
            new FixedHostPortGenericContainer<>("suffix/testcontainers-workshop-ui:1.1.1")
                    .withFixedExposedPort(8081, 8080).withExposedPorts(8080);

    public static void main(String... args) {
        ui.start();

        System.out.println("**** UI is started.");
        System.out.println("**** UI is accessible on " + ui.getFirstMappedPort());
        loopEndlessly();
    }

    private static void loopEndlessly() {
        System.out.println("**** Press q and RETURN to stop the UI");
        Scanner userInput = new Scanner(System.in);
        while (!userInput.next().startsWith("q")) ;
        System.out.println("**** UI server stopped");
    }
}
