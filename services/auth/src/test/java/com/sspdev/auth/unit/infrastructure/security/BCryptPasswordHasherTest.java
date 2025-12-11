package com.sspdev.auth.unit.infrastructure.security;

import org.junit.jupiter.api.Test;
import static org.springframework.test.util.AssertionErrors.fail;

public class BCryptPasswordHasherTest {

    @Test
    void hash_shouldHash_whenRawPasswordValid() {
        fail("skeleton");
    }

    @Test
    void hash_shouldThrowNotValidPasswordExceptionWithBlank_whenRawPasswordBlank() {
        fail("skeleton");
    }

    @Test
    void hash_shouldThrowNotValidPasswordExceptionWithNull_whenRawPasswordNull() {
        fail("skeleton");
    }

    @Test
    void verify_shouldReturnTrue_whenRawPasswordAndHashedPasswordAreValid() {
        fail("skeleton");
    }

    @Test
    void verify_shouldThrowNotValidPasswordExceptionWithNull_whenRawPasswordNull() {
        fail("skeleton");
    }

    @Test
    void verify_shouldThrowNotValidPasswordExceptionWithNull_whenHashedPasswordNull() {
        fail("skeleton");
    }

    @Test
    void verify_shouldThrowNotValidPasswordExceptionWithBlank_whenRawPasswordBlank() {
        fail("skeleton");
    }

    @Test
    void verify_shouldThrowNotValidPasswordExceptionWithBlank_whenHashedPasswordBlank() {
        fail("skeleton");
    }
}