package com.sspdev.auth.application.service;

import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import com.sspdev.auth.domain.exception.UserByEmailAlreadyExistsException;
import com.sspdev.auth.domain.exception.UserByPhoneAlreadyExistsException;
import com.sspdev.auth.domain.port.in.RegisterUserCommand;
import com.sspdev.auth.setup.IntegrationTestBase;
import com.sspdev.auth.testutil.IntegrationTestDataUtil;

@RequiredArgsConstructor
public class RegisterUserServiceIT extends IntegrationTestBase {

    public static final String EXISTING_EMAIL = "user01@example.com";
    private static final String EXISTING_PHONE = "+7 900 111 11 01";

    private final RegisterUserService userService;

    @Test
    void register_shouldReturnRegisterUserResponse_whenRegisterUserCommandValid() {
        var userCommand = IntegrationTestDataUtil.getValidRegisterUserCommand();

        var registerUserResponse = userService.register(userCommand);

        assertThat(registerUserResponse).isNotNull();
        assertThat(registerUserResponse.id()).isExactlyInstanceOf(UUID.class);
    }

    @Test
    void register_shouldThrowNPE_whenRegisterUserCommandNull() {
        assertThrows(NullPointerException.class, () -> userService.register(null));
    }

    @Test
    void register_shouldThrowUserByEmailAlreadyExist_whenUserWithEmailExistsInDb() {
        var userCommandWithExistingEmail = RegisterUserCommand.builder()
                .email(EXISTING_EMAIL)
                .phone("8-925-869-96-98")
                .rawPassword("dummyPassword")
                .build();

        assertThrows(UserByEmailAlreadyExistsException.class,
                () -> userService.register(userCommandWithExistingEmail));
    }

    @Test
    void register_shouldThrowUserByPhoneAlreadyExist_whenUserWithPhoneExistsInDb() {
        var userCommandWithExistingPhone = RegisterUserCommand.builder()
                .email("test_user@gmail.com")
                .phone(EXISTING_PHONE)
                .rawPassword("dummyPassword")
                .build();

        assertThrows(UserByPhoneAlreadyExistsException.class,
                () -> userService.register(userCommandWithExistingPhone));
    }
}