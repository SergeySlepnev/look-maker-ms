package com.sspdev.auth.setup;

import org.jetbrains.annotations.NotNull;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Sql("/sql/testData.sql")
@Testcontainers
public abstract class PostgresTestContainerConfiguration {

    public static final String POSTGRES_IMAGE_NAME = "postgres:18.1";

    @Container
    private static final PostgreSQLContainer<?> CONTAINER = new PostgreSQLContainer<>(POSTGRES_IMAGE_NAME);

    @DynamicPropertySource
    static void setPostgresProperties(@NotNull DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", CONTAINER::getJdbcUrl);
        properties.add("spring.datasource.username", CONTAINER::getUsername);
        properties.add("spring.datasource.password", CONTAINER::getPassword);
    }
}