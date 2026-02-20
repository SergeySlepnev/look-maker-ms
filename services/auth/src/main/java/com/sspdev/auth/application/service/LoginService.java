package com.sspdev.auth.application.service;

import com.sspdev.auth.domain.exception.InvalidCredentialException;
import com.sspdev.auth.domain.port.out.UserIdentityRepository;
import com.sspdev.auth.infrastructure.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserIdentityRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    public String login(String email, String rawPassword) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialException::new);

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new InvalidCredentialException();
        }

        var roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return jwtTokenProvider.generateToken(user.getId(), roles);
    }
}