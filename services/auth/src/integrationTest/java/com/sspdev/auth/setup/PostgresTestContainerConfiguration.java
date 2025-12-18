package com.sspdev.auth.setup;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;

@Sql("/sql/testData.sql")
public abstract class PostgresTestContainerConfiguration {

    public static final String POSTGRES_IMAGE_NAME = "postgres:18.1";

    private static final PostgreSQLContainer<?> CONTAINER = new PostgreSQLContainer<>(POSTGRES_IMAGE_NAME);

    @BeforeAll
    static void startContainer() {
        CONTAINER.start();
    }

    @DynamicPropertySource
    static void setPostgresProperties(@NotNull DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", CONTAINER::getJdbcUrl);
        properties.add("spring.datasource.username", CONTAINER::getUsername);
        properties.add("spring.datasource.password", CONTAINER::getPassword);
    }
}