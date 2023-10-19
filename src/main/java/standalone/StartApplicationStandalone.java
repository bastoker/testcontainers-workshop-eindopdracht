package standalone;


import nl.jnext.workshop.testcontainers.vakantieplanner.VakantieplannerApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;

@SpringBootApplication
public class StartApplicationStandalone {

    public static void main(String[] args) {
        System.out.println("**** Starting the the Vakantieplanner Backend Application");
        args = new String[]{"--spring.config.location=classpath:application-standalone.yaml"};
        ConfigurableApplicationContext context = SpringApplication.run(VakantieplannerApplication.class, args);
        System.out.println();
        System.out.println("**** Vakantieplanner backend API is started.");
        System.out.println("**** Go to http://localhost:8080/actuator/health for \uD83C\uDF31 health info");
    }
}
