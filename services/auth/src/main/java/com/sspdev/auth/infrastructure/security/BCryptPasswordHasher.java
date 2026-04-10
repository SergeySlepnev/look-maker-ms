package com.sspdev.auth.infrastructure.security;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.sspdev.auth.domain.exception.DomainExceptionCode;
import com.sspdev.auth.domain.exception.NotValidPasswordException;
import com.sspdev.auth.domain.port.out.PasswordHasher;

@Component
@RequiredArgsConstructor
public class BCryptPasswordHasher implements PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String hash(String rawPassword) {
        return Optional.ofNullable(rawPassword)
                .filter(StringUtils::hasText)
                .map(passwordEncoder::encode)
                .orElseThrow(NotValidPasswordException::new);
    }

    @Override
    public boolean verify(String rawPassword, String hashedPassword) {
        if (rawPassword == null) {
            throw new NotValidPasswordException(DomainExceptionCode.INVALID_PASSWORD_NULL);
        }
        if (hashedPassword == null) {
            throw new NotValidPasswordException(DomainExceptionCode.INVALID_PASSWORD_NULL);
        }
        if (!StringUtils.hasText(rawPassword) || !StringUtils.hasText(hashedPassword)) {
            throw new NotValidPasswordException(DomainExceptionCode.INVALID_PASSWORD_BLANK);
        }
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}