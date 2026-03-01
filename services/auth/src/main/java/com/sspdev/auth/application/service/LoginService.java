package com.sspdev.auth.application.service;

import com.sspdev.auth.application.util.DataMasker;
import com.sspdev.auth.domain.exception.InvalidCredentialException;
import com.sspdev.auth.domain.port.out.UserIdentityRepository;
import com.sspdev.auth.infrastructure.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Сервис аутентификации, отвечающий за проверку учётных данных пользователя и выдачу JWT.
 *
 * <p>Основная ответственность:
 * <ul>
 *   <li>Поиск пользователя по email через {@link UserIdentityRepository}.</li>
 *   <li>Проверка пароля с помощью {@link PasswordEncoder}.</li>
 *   <li>Генерация JWT через {@link JwtTokenProvider} на основе id пользователя и набора ролей.</li>
 * </ul>
 *
 * <p>Особенности и рекомендации:
 * <ul>
 *   <li>Сервис по сути является stateless и безопасен для многопоточной работы при условии,
 *       что его зависимости (репозиторий, passwordEncoder, jwtTokenProvider) потокобезопасны.</li>
 *   <li>Метод не логирует пароли и не возвращает подробностей о том, какая часть
 *       учётных данных неверна (внешняя точка видит общее {@link InvalidCredentialException}).</li>
 *   <li>Рассмотрите внедрение механизма ограничения числа попыток (rate-limiting / account lockout)
 *       и защиты от атак по таймингу на более высоком уровне.</li>
 * </ul>
 *
 * @author Sergey Slepnev
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LoginService {

    private final UserIdentityRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final DataMasker dataMasker;

    /**
     * Выполняет попытку входа пользователя и возвращает JWT при успешной аутентификации.
     *
     * <p>Алгоритм:
     * <ol>
     *   <li>Ищет пользователя по email. Если пользователь не найден — выбрасывает {@link InvalidCredentialException}.</li>
     *   <li>Сравнивает предоставленный rawPassword с хешем пароля пользователя с помощью {@link PasswordEncoder}.</li>
     *   <li>Если пароль совпадает — собирает набор ролей пользователя и генерирует JWT через {@link JwtTokenProvider}.</li>
     * </ol>
     *
     * <p>Важно: метод ожидает, что входные параметры корректны (email и rawPassword).
     * Поведение при null/пустых значениях зависит от реализации зависимостей и может приводить к исключениям.
     *
     * @param email       электронная почта пользователя; должна однозначно идентифицировать пользователя в хранилище
     * @param rawPassword незашифрованный пароль пользователя
     * @return JWT токен в виде строки при успешной аутентификации
     * @throws InvalidCredentialException если пользователь не найден или пароль неверен
     */
    public String login(String email, String rawPassword) {

        var maskedEmail = dataMasker.maskEmail(email);

        if (email == null || email.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            log.warn("Login failed. Invalid input data");
            throw new InvalidCredentialException();
        }

        log.debug("Login attempt for email: {}", maskedEmail);

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login failed: user not found for email: {}", email);
                    return new InvalidCredentialException();
                });

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            log.warn("Login failed: invalid password for email: {}", maskedEmail);
            throw new InvalidCredentialException();
        }

        var roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
        var token = jwtTokenProvider.generateToken(user.getId(), roles);

        log.info("Login successful for user id: {}, email: {}, roles: {}", user.getId(), maskedEmail, roles);

        return token;
    }
}