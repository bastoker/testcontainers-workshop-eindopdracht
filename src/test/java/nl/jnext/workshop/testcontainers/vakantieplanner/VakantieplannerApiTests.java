package nl.jnext.workshop.testcontainers.vakantieplanner;

import helper.KeycloakTestcontainer;
import io.restassured.http.ContentType;
import nl.jnext.workshop.testcontainers.vakantieplanner.dao.VakantieRepository;
import nl.jnext.workshop.testcontainers.vakantieplanner.model.Holiday;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ComponentScan
class VakantieplannerApiTests {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:15"))
            .withDatabaseName("integration-tests-db")
            .withUsername("admin")
            .withPassword("@dm1n");

    // Default admin username/password is admin/admin
    static KeycloakTestcontainer keycloak = new KeycloakTestcontainer();

    @LocalServerPort
    protected int localServerPort;

    @Autowired
    VakantieRepository vakantieRepository;

    @DynamicPropertySource
    static void configureApplicationContext(DynamicPropertyRegistry registry) {
        postgres.start();
        keycloak.start();
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("keycloak.auth-server-url", keycloak::getAuthServerUrl);

        // Add some users to Keycloak to play with:
        keycloak.addNormalUser("pete", "secret");
        keycloak.addNormalUser("sam", "secret");
        keycloak.addNormalUser("bob", "secret");
    }

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("Call to API without bearer token fails")
    void loginWithoutTokenFails() {
        when().
                get(String.format("http://localhost:%d/userinfo", localServerPort)).
                then().
                statusCode(401);
    }

    @Test
    @DisplayName("First login as a normal user adds user to application database")
    void firstLoginAsNormalUser() {
        // Logged in as pete
        String bearerToken = keycloak.getBearerTokenFor("pete", "secret");

        System.out.printf("http://localhost:%d/userinfo%n", localServerPort);

        // Call our API using this bearer token:
        given().headers(
                        "Authorization",
                        "Bearer " + bearerToken).
                when().
                get(String.format("http://localhost:%d/userinfo", localServerPort)).
                then().
                statusCode(200).
                body("name", equalTo("pete"));
    }

    @Test
    @DisplayName("Een ingelogde gebruiker kan zijn vakanties opvragen")
    void retrieveHolidaysForUser() {
        // Logged in as sam
        String bearerToken = keycloak.getBearerTokenFor("sam", "secret");

        // Voeg een vakantie toe en bewaar het technische id
        int idOfHoliday = vakantieRepository.addHoliday("sam", new Holiday(
                -1,
                "Zomervakantie",
                LocalDate.of(2019, 7, 2),
                LocalDate.of(2019, 7, 22)
        )).id();

        // Call our API using this bearer token:
        given()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken)
                .when()
                .get(String.format("http://localhost:%d/holiday/sam", localServerPort))
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("findAll{i -> i.id == %s && i.description == \"%s\"}".formatted(idOfHoliday, "Zomervakantie"), not(empty()))
        ;
    }

    @DisplayName("Een vakantie toevoegen lukt als deze niet overlapt met een bestaande vakantie")
    @Test
    void addingASecondHolidaySucceeds() {
        // Logged in as bob
        String bearerToken = keycloak.getBearerTokenFor("bob", "secret");

        // Voeg een vakantie toe en bewaar het technische id
        vakantieRepository.addHoliday("sam", new Holiday(
                -1,
                "Zomervakantie",
                LocalDate.of(2018, 7, 2),
                LocalDate.of(2018, 7, 22)
        ));

        // Tweede vakantie om toe te voegen:
        Holiday holiday2 = new Holiday(
                123,
                "Zomervakantie",
                LocalDate.of(2017, 7, 20),
                LocalDate.of(2017, 12, 30)
        );

        // Call our API using this bearer token:
        given()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken)
                .contentType(ContentType.JSON)
                .body(holiday2)
                .when()
                .post(String.format("http://localhost:%d/holiday/bob", localServerPort))
                .then()
                .log().ifValidationFails()
                .statusCode(200);
    }

    @DisplayName("Een vakantie toevoegen kan niet als deze overlapt met een bestaande vakantie")
    @Test
    void addingHolidayThatConflictsWithExistingHolidayIsRejected() {
        // Logged in as bob
        String bearerToken = keycloak.getBearerTokenFor("bob", "secret");

        // Voeg een vakantie toe en bewaar het technische id
        vakantieRepository.addHoliday("sam", new Holiday(
                -1,
                "Zomervakantie",
                LocalDate.of(2022, 7, 2),
                LocalDate.of(2022, 7, 22)
        ));

        // 2nd holiday to post:
        Holiday holiday2 = new Holiday(
                123,
                "Zomervakantie",
                LocalDate.of(2022, 7, 20),
                LocalDate.of(2022, 8, 30)
        );

        // Call our API using this bearer token:
        given()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken)
                .contentType(ContentType.JSON)
                .body(holiday2)
                .when()
                .post(String.format("http://localhost:%d/holiday/bob", localServerPort))
                .then()
                .log().ifValidationFails()
                .statusCode(409);
    }
}
