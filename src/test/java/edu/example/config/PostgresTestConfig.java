package edu.example.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;

@SpringBootTest
@ContextConfiguration
public class PostgresTestConfig {

    private static volatile PostgreSQLContainer<?> postgreSQLContainer = null;

    private static PostgreSQLContainer<?> getPostgresContainer() {
        PostgreSQLContainer<?> instance = postgreSQLContainer;
        if (instance == null) {
            synchronized (PostgreSQLContainer.class) {
                instance = postgreSQLContainer;
                if (instance == null) {
                    postgreSQLContainer = instance = new PostgreSQLContainer<>("postgres:14.7")
                            .withDatabaseName("local")
                            .withUsername("username")
                            .withPassword("password")
                            .withStartupTimeout(Duration.ofSeconds(60))
                            .withReuse(true);
                    postgreSQLContainer.start();
                }
            }
        }
        return instance;
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            var postgresContainer = getPostgresContainer();

            var jdbcUrl = postgresContainer.getJdbcUrl();
            var username = postgresContainer.getUsername();
            var password = postgresContainer.getPassword();

            TestPropertyValues.of(
                    "spring.datasource.url=" + jdbcUrl,
                    "spring.datasource.username=" + username,
                    "spring.datasource.password=" + password,
                    "spring.datasource.driverClassName=" + "org.hibernate.dialect.PostgreSQLDialect"
            ).applyTo(applicationContext.getEnvironment());
        }

    }

}
