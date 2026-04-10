package com.sspdev.auth.infrastructure.security.jwt;

import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import com.sspdev.auth.domain.model.UserIdentity;
import com.sspdev.auth.domain.port.out.UserIdentityRepository;
import com.sspdev.auth.infrastructure.persistence.entity.Role;
import com.sspdev.auth.setup.IntegrationTestBase;
import com.sspdev.auth.testutil.IntegrationTestDataUtil;

@RequiredArgsConstructor
public class JwtTokenProviderIT extends IntegrationTestBase {

    private final JwtTokenProvider tokenProvider;
    private final UserIdentityRepository userIdentityRepository;
    private UserIdentity savedUserIdentity;
    private String generatedToken;

    @DynamicPropertySource
    static void dynamicJwtProperties(DynamicPropertyRegistry registry) {
        byte[] secretBytes = new byte[32];
        new SecureRandom().nextBytes(secretBytes);
        var base64Secret = Base64.getEncoder().encodeToString(secretBytes);

        registry.add("jwt.secret", () -> base64Secret);
        registry.add("jwt.validity-ms", () -> 2000L);
    }

    @BeforeEach
    void saveUserIdentity() {
        var userIdentityToSave = IntegrationTestDataUtil.getUserIdentityDomainModel();
        userIdentityRepository.save(userIdentityToSave);

        savedUserIdentity = userIdentityRepository.findByEmail(userIdentityToSave.getEmail())
                .orElseThrow(() -> new AssertionError(
                        "UserIdentity with email " + userIdentityToSave.getEmail() + " was not found after save."));

        generatedToken = tokenProvider.generateToken(savedUserIdentity.getId(),
                savedUserIdentity.getRoles().stream().map(Role::name).collect(Collectors.toSet()));
    }

    @Test
    void generateToken_shouldGenerateToken_andValidateAndExtractRoles() {
        assertThat(generatedToken).isNotNull();
        assertThat(tokenProvider.validateToken(generatedToken)).isTrue();
        assertThat(tokenProvider.getUserId(generatedToken)).isEqualTo(savedUserIdentity.getId());

        var roles = tokenProvider.getRoles(generatedToken);
        assertThat(roles).contains("USER", "ADMIN");
        assertThat(roles.size()).isEqualTo(2);
    }

    @Test
    void validateToken_shouldRejectTemperedToken() {
        var temperedToken = generatedToken.substring(1, generatedToken.length() - 1) + '!';

        assertThat(tokenProvider.validateToken(temperedToken)).isFalse();
    }

    @Test
    void validateToken_shouldRejectExpiredToken() throws InterruptedException {
        Thread.sleep(2500);
        assertThat(tokenProvider.validateToken(generatedToken)).isFalse();
    }
}