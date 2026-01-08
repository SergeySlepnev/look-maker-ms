package com.sspdev.auth.setup;

import org.springframework.security.test.context.support.WithMockUser;

@IT
@WithMockUser(username = "testuser@gmail.com", password = "test", authorities = {"ADMIN", "USER", "OWNER"})
public abstract class IntegrationTestBase extends PostgresTestContainerConfiguration {
}