package nl.jnext.workshop.testcontainers.vakantieplanner.dao;

import nl.jnext.workshop.testcontainers.vakantieplanner.model.Holiday;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ComponentScan("nl.jnext.workshop.testcontainers.vakantieplanner.dao")
@JooqTest
public class VakantieRepositoryTest {

    private static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withCommand("log_statement=all");

    @Autowired
    private VakantieRepository repository;

    @DynamicPropertySource
    static void configureContext(DynamicPropertyRegistry registry) {
        // Doe hier iets:

        // En maak de Spring configuratie af:
//        registry.add("spring.datasource.url", () -> ???);
//        registry.add("spring.datasource.username", () -> ???);
//        registry.add("spring.datasource.password", () -> ???);
    }

    @Test
    public void canQueryDatabase() {
        int idOfPete = repository.getIdForUsername("pete");
        assertThat(idOfPete).isGreaterThan(0);
    }

    @Test
    public void canAddAndRetrieveHoliday() {
        Holiday persistedHoliday = repository.addHoliday("pete", new Holiday(
                -1,
                "Zomervakantie",
                LocalDate.of(2023, 8, 6),
                LocalDate.of(2023, 8, 25)));
        assertThat(persistedHoliday.id()).isGreaterThan(0);

        List<Holiday> holidaysOfPete = repository.retrieveHolidaysForMemberWithName("pete");
        assertThat(holidaysOfPete).hasSize(1);
        assertThat(holidaysOfPete.get(0).description()).isEqualTo("Zomervakantie");
    }

    @Test
    public void whenHolidayOverlapsItIsDetected() {
        Holiday persistedHoliday = repository.addHoliday("john", new Holiday(
                -1,
                "Zomervakantie",
                LocalDate.of(2020, 8, 6),
                LocalDate.of(2020, 8, 25)));

        boolean resultOverlapping = repository.checkIfHolidayIsPossible(
                LocalDate.of(2020, 7, 7),
                LocalDate.of(2020, 8, 7));

        assertThat(resultOverlapping).isEqualTo(false);

        boolean resultNotOverlapping = repository.checkIfHolidayIsPossible(
                LocalDate.of(2023, 7, 7),
                LocalDate.of(2023, 8, 7));

        assertThat(resultNotOverlapping).isEqualTo(true);

        boolean resultNotOverlappingEdgeCase = repository.checkIfHolidayIsPossible(
                LocalDate.of(2020, 8, 5),
                LocalDate.of(2020, 8, 5));

        assertThat(resultNotOverlappingEdgeCase).isEqualTo(true);
    }


}
