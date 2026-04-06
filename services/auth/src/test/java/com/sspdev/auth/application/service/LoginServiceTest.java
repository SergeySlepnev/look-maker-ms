package com.sspdev.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.sspdev.auth.application.util.DataMasker;
import com.sspdev.auth.domain.exception.DomainExceptionCode;
import com.sspdev.auth.domain.exception.InvalidCredentialException;
import com.sspdev.auth.domain.port.out.UserIdentityRepository;
import com.sspdev.auth.infrastructure.security.jwt.JwtTokenProvider;
import com.sspdev.auth.testutil.TestDataUtil;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @Mock
    private UserIdentityRepository userIdentityRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private DataMasker dataMasker;
    @InjectMocks
    private LoginService loginService;
    @Captor
    private ArgumentCaptor<Set<String>> rolesCaptor;

    @Test
    void login_shouldFindByEmail_andReturnJwtToken_whenUserInDb() {
        var domainModel = TestDataUtil.getUserIdentityDomainModel();
        var expectedRoles = domainModel.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        when(userIdentityRepository.findByEmail(domainModel.getEmail())).thenReturn(Optional.of(domainModel));
        when(passwordEncoder.matches("dummyRawPassword", domainModel.getPasswordHash())).thenReturn(true);
        when(tokenProvider.generateToken(domainModel.getId(), expectedRoles)).thenReturn("generatedJwtToken");

        var generatedToken = loginService.login(domainModel.getEmail(), "dummyRawPassword");

        assertThat(generatedToken).isNotNull();
        assertThat(generatedToken).isEqualTo("generatedJwtToken");
        verify(userIdentityRepository, times(1)).findByEmail(domainModel.getEmail());
        verify(passwordEncoder, times(1)).matches("dummyRawPassword", domainModel.getPasswordHash());
        verify(tokenProvider, times(1)).generateToken(domainModel.getId(), expectedRoles);

        verify(tokenProvider).generateToken(eq(domainModel.getId()), rolesCaptor.capture());

        var actualRoles = rolesCaptor.getValue();
        assertThat(actualRoles).isEqualTo(expectedRoles);
    }

    @Test
    void login_shouldTrowInvalidCredentialException_whenEmailIsIncorrectPasswordCorrect() {
        when(userIdentityRepository.findByEmail("incorrectEmail@gmail.com")).thenReturn(Optional.empty());

        var credentialException = assertThrows(InvalidCredentialException.class,
                () -> loginService.login("incorrectEmail@gmail.com", "correctPassword"));

        assertThat(credentialException.getErrorCode()).isEqualTo(DomainExceptionCode.INVALID_CREDENTIALS);
        verify(userIdentityRepository, times(1)).findByEmail("incorrectEmail@gmail.com");
        verifyNoInteractions(passwordEncoder, tokenProvider);
    }

    @Test
    void login_shouldThrowInvalidCredentialException_whenPasswordIncorrectEmailCorrect() {
        var domainModel = TestDataUtil.getUserIdentityDomainModel();

        when(userIdentityRepository.findByEmail(domainModel.getEmail())).thenReturn(Optional.of(domainModel));
        when(passwordEncoder.matches("incorrectPassword", domainModel.getPasswordHash())).thenReturn(false);

        var credentialException = assertThrows(InvalidCredentialException.class,
                () -> loginService.login(domainModel.getEmail(), "incorrectPassword"));

        assertThat(credentialException.getErrorCode()).isEqualTo(DomainExceptionCode.INVALID_CREDENTIALS);
        verify(userIdentityRepository, times(1)).findByEmail(domainModel.getEmail());
        verify(passwordEncoder, times(1)).matches("incorrectPassword", domainModel.getPasswordHash());
        verifyNoInteractions(tokenProvider);
    }
}