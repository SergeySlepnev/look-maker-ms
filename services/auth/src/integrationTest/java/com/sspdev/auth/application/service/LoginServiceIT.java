package com.sspdev.auth.application.service;

import com.sspdev.auth.domain.exception.DomainExceptionCode;
import com.sspdev.auth.domain.exception.InvalidCredentialException;
import com.sspdev.auth.domain.port.in.RegisterUserCommand;
import com.sspdev.auth.domain.port.in.RegisterUserUseCase;
import com.sspdev.auth.setup.IntegrationTestBase;
import com.sspdev.auth.testutil.IntegrationTestDataUtil;
import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@RequiredArgsConstructor
public class LoginServiceIT extends IntegrationTestBase {

    private final RegisterUserUseCase registerUserService;
    private final LoginService loginService;
    private RegisterUserCommand userCommand;

    @BeforeEach
    void saveUserIdentity() {
        userCommand = IntegrationTestDataUtil.getValidRegisterUserCommand();
        registerUserService.register(userCommand);
    }

    @Test
    void login_shouldFindByEmail_andReturnJwtToken_whenUserInDb() {
        var token = loginService.login(userCommand.email(), userCommand.rawPassword());

        assertThat(token).isNotNull();
    }

    @Test
    void login_shouldTrowInvalidCredentialException_whenEmailIsIncorrectPasswordCorrect() {
        var credentialException = assertThrows(InvalidCredentialException.class,
                () -> loginService.login("incorrect@gmail.com", userCommand.rawPassword()));

        assertThat(credentialException.getErrorCode()).isEqualTo(DomainExceptionCode.INVALID_CREDENTIALS);
    }

    @Test
    void login_shouldThrowInvalidCredentialException_whenPasswordIncorrectEmailCorrect() {
        var credentialException = assertThrows(InvalidCredentialException.class,
                () -> loginService.login(userCommand.email(), "incorrectRawPassword"));

        assertThat(credentialException.getErrorCode()).isEqualTo(DomainExceptionCode.INVALID_CREDENTIALS);
    }
}