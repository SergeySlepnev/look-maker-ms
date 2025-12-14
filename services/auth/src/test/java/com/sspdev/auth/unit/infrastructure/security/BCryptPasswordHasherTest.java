package com.sspdev.auth.unit.infrastructure.security;

import com.sspdev.auth.domain.exception.DomainExceptionCode;
import com.sspdev.auth.domain.exception.NotValidPasswordException;
import com.sspdev.auth.infrastructure.security.BCryptPasswordHasher;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptPasswordHasherTest {

    private BCryptPasswordHasher passwordHasher;

    @BeforeEach
    void setup() {
        var passwordEncoder = new BCryptPasswordEncoder();
        passwordHasher = new BCryptPasswordHasher(passwordEncoder);
    }

    @Test
    void hash_shouldHash_whenRawPasswordValid() {
        var validRawPassword = "ValidPassword123";

        var actualResult = passwordHasher.hash(validRawPassword);

        assertThat(actualResult).isNotNull();
        var isVerified = passwordHasher.verify(validRawPassword, actualResult);
        assertThat(isVerified).isTrue();
    }

    @Test
    void hash_shouldThrowNotValidPasswordExceptionWith_whenRawPasswordBlank() {
        var blankRawPassword = "";
        var notValidPasswordException = assertThrows(NotValidPasswordException.class, () -> passwordHasher.hash(blankRawPassword));
        var errorCode = notValidPasswordException.getErrorCode();
        assertThat(errorCode).isEqualTo(DomainExceptionCode.INVALID_PASSWORD);
    }

    @Test
    void hash_shouldThrowNotValidPasswordExceptionWithNull_whenRawPasswordNull() {
        var notValidPasswordException = assertThrows(NotValidPasswordException.class, () -> passwordHasher.hash(null));
        var errorCode = notValidPasswordException.getErrorCode();
        assertThat(errorCode).isEqualTo(DomainExceptionCode.INVALID_PASSWORD);
    }

    @Test
    void verify_shouldReturnTrue_whenRawPasswordAndHashedPasswordAreValid() {
        var validRawPassword = "ValidPassword123";
        var hashedPassword = passwordHasher.hash(validRawPassword);

        var actualResult = passwordHasher.verify(validRawPassword, hashedPassword);

        assertThat(actualResult).isTrue();
    }

    @Test
    void verify_shouldThrowNotValidPasswordExceptionWithNull_whenRawPasswordNull() {
        var validHashedPassword = "ValidHashedPassword";
        var notValidPasswordException = assertThrows(NotValidPasswordException.class, () -> passwordHasher.verify(null, validHashedPassword));
        var errorCode = notValidPasswordException.getErrorCode();

        assertThat(errorCode).isEqualTo(DomainExceptionCode.INVALID_PASSWORD_NULL);
    }

    @Test
    void verify_shouldThrowNotValidPasswordExceptionWithNull_whenHashedPasswordNull() {
        var validRawPassword = "ValidPassword123";
        var notValidPasswordException = assertThrows(NotValidPasswordException.class, () -> passwordHasher.verify(validRawPassword, null));
        var errorCode = notValidPasswordException.getErrorCode();

        assertThat(errorCode).isEqualTo(DomainExceptionCode.INVALID_PASSWORD_NULL);
    }

    @Test
    void verify_shouldThrowNotValidPasswordExceptionWithBlank_whenRawPasswordBlank() {
        var blankRawPassword = "";
        var validHashedPassword = "ValidHashedPassword";
        var notValidPasswordException = assertThrows(NotValidPasswordException.class, () -> passwordHasher.verify(blankRawPassword, validHashedPassword));
        var errorCode = notValidPasswordException.getErrorCode();

        assertThat(errorCode).isEqualTo(DomainExceptionCode.INVALID_PASSWORD_BLANK);
    }

    @Test
    void verify_shouldThrowNotValidPasswordExceptionWithBlank_whenHashedPasswordBlank() {
        var validRawPassword = "ValidPassword123";
        var blankHashedPassword = "";
        var notValidPasswordException = assertThrows(NotValidPasswordException.class, () -> passwordHasher.verify(validRawPassword, blankHashedPassword));
        var errorCode = notValidPasswordException.getErrorCode();

        assertThat(errorCode).isEqualTo(DomainExceptionCode.INVALID_PASSWORD_BLANK);
    }

    @Test
    void verify_shouldReturnFalse_whenRawPasswordAndHashedPasswordAreNotMatch() {
        var validRawPassword = "ValidPassword123";
        var validHashedPassword = "NotMatchValidHashedPassword";

        var isVerified = passwordHasher.verify(validRawPassword, validHashedPassword);
        assertThat(isVerified).isFalse();
    }
}