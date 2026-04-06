package com.sspdev.auth.infrastructure.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.sspdev.auth.infrastructure.persistence.entity.Role;

public class JwtTokenProviderTest {

    public static final UUID EXISTING_USER_ID = UUID.randomUUID();

    private JwtTokenProvider tokenProvider;
    private Set<String> roles;

    @BeforeEach
    void setUp() {
        String base64Secret = Base64.getEncoder()
                .encodeToString("TestSecretString123456789987654321".getBytes(StandardCharsets.UTF_8));
        long validityMs = 60_000;
        tokenProvider = new JwtTokenProvider(base64Secret, validityMs);
        roles = Set.of(Role.ADMIN.name(), Role.USER.name());
    }

    @Test
    void generateToken_shouldGenerateToken_andValidateAndExtractRoles() {
        var token = tokenProvider.generateToken(EXISTING_USER_ID, roles);

        assertThat(token).isNotNull();
        assertThat(tokenProvider.validateToken(token)).isTrue();
        assertThat(tokenProvider.getUserId(token)).isEqualTo(EXISTING_USER_ID);

        var actualRoles = tokenProvider.getRoles(token);
        assertThat(actualRoles).contains("ADMIN");
        assertThat(actualRoles).contains("USER");
        assertThat(actualRoles.size()).isEqualTo(2);
    }

    @Test
    void validateToken_shouldRejectTamperedToken() {
        var generateToken = tokenProvider.generateToken(EXISTING_USER_ID, roles);
        var temperedToken = generateToken.substring(0, generateToken.length() - 1) + "sss";

        assertThat(tokenProvider.validateToken(temperedToken)).isFalse();
    }

    @Test
    void validateToken_shouldRejectExpiredToken() throws InterruptedException {
        String base64Secret = Base64.getEncoder()
                .encodeToString("TestSecretString123456789987654321".getBytes(StandardCharsets.UTF_8));
        long validityMs = 10;
        tokenProvider = new JwtTokenProvider(base64Secret, validityMs);
        var tenMsLivedToken = tokenProvider.generateToken(EXISTING_USER_ID, roles);

        Thread.sleep(20);

        assertThat(tokenProvider.validateToken(tenMsLivedToken)).isFalse();
    }
}