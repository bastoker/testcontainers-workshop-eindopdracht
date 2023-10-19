package tools.database;

import org.flywaydb.core.Flyway;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.jooq.meta.jaxb.*;

/**
 * This standalone application uses Testcontainers, Flyway and jOOQ to generate the
 * jOOQ API classes for our database schema specifically.
 *
 * First a Docker PostgreSQL database is started, subsequently the Flyway migration
 * is triggered and then the jOOQ codegen will run against this live database in order
 * to generate all typesafe database mapping classes.
 *
 * This class is inspired by a jOOQ blog:
 * https://blog.jooq.org/using-testcontainers-to-generate-jooq-code/
 * https://github.com/jOOQ/jOOQ/blob/main/jOOQ-examples/jOOQ-testcontainers-flyway-example/pom.xml
 *
 * TODO: maybe do this using JBang in the future?
 * https://www.jbang.dev
 */
public class MigrateDatabaseAndGenerateJooqClasses {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:15"));

    public static void main(String... args) {
        // Start DB
        postgres.start();
        String jdbcUrl = postgres.getJdbcUrl();
        String username = postgres.getUsername();
        String password = postgres.getPassword();

        // Execute migration
        Flyway flyway = Flyway.configure().dataSource(jdbcUrl, username, password).load();
        flyway.migrate();

        // Generate jOOQ code
        generateJooqClasses(jdbcUrl, username, password);
    }

    private static void generateJooqClasses(String jdbcUrl, String username, String password) {
        Configuration configuration = new Configuration()
                .withJdbc(new Jdbc()
                        .withDriver("org.postgresql.Driver")
                        .withUrl(jdbcUrl)
                        .withUser(username)
                        .withPassword(password))
                .withGenerator(new Generator()
                        .withDatabase(new Database()
                                .withName("org.jooq.meta.postgres.PostgresDatabase")
                                .withIncludes(".*")
                                .withExcludes("")
                                .withInputSchema("public"))
                        .withTarget(new Target()
                                .withPackageName("nl.jnext.workshop.testcontainers.vakantieplanner.jooq")
                                .withDirectory("src/main/java")));

        try {
            GenerationTool.generate(configuration);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(41);
        }
    }

}
