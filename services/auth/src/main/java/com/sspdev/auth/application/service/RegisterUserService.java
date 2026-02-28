package com.sspdev.auth.application.service;

import com.sspdev.auth.application.util.DataMasker;
import com.sspdev.auth.domain.exception.DomainException;
import com.sspdev.auth.domain.exception.DomainExceptionCode;
import com.sspdev.auth.domain.exception.UserByEmailAlreadyExistsException;
import com.sspdev.auth.domain.exception.UserByPhoneAlreadyExistsException;
import com.sspdev.auth.domain.model.UserIdentity;
import com.sspdev.auth.domain.port.in.RegisterUserCommand;
import com.sspdev.auth.domain.port.in.RegisterUserResponse;
import com.sspdev.auth.domain.port.in.RegisterUserUseCase;
import com.sspdev.auth.domain.port.out.PasswordHasher;
import com.sspdev.auth.domain.port.out.UserIdentityRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Сервис для регистрации новых пользователей в системе.
 *
 * <p>Реализует use-case регистрации пользователя, включая:
 * <ul>
 *   <li>Валидацию уникальности email и телефона</li>
 *   <li>Хеширование пароля</li>
 *   <li>Создание и сохранение сущности пользователя</li>
 *   <li>Обработку конфликтов уникальности на уровне БД</li>
 * </ul>
 *
 * <p>Использует bcrypt как алгоритм хеширования по умолчанию.
 * Операция регистрации выполняется в транзакции для обеспечения атомарности.
 *
 * @author Sergey Slepnev
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {

    @Value("${auth.password.hash-algo}")
    private static String DEFAULT_HASH_ALGO;
    private static final String UNIQUE_EMAIL_CONSTRAINT_NAME = "unique_user_email";
    private static final String UNIQUE_PHONE_CONSTRAINT_NAME = "unique_user_phone";

    private final UserIdentityRepository userIdentityRepository;
    private final PasswordHasher passwordHasher;
    private final DataMasker dataMasker;

    /**
     * Регистрирует нового пользователя в системе.
     *
     * <p>Метод выполняет следующие шаги:
     * <ol>
     *   <li>Проверяет уникальность email (если указан)</li>
     *   <li>Проверяет уникальность телефона (если указан)</li>
     *   <li>Хеширует пароль с использованием {@link PasswordHasher}</li>
     *   <li>Создает сущность {@link UserIdentity}</li>
     *   <li>Сохраняет пользователя в репозитории</li>
     * </ol>
     *
     * <p>В случае нарушения ограничений уникальности на уровне БД,
     * исключение преобразуется в соответствующее доменное исключение.
     *
     * @param registerUserCommand команда с данными для регистрации пользователя
     * @return ответ, содержащий ID зарегистрированного пользователя
     * @throws UserByEmailAlreadyExistsException если пользователь с таким email уже существует
     * @throws UserByPhoneAlreadyExistsException если пользователь с таким телефоном уже существует
     * @throws DomainException                   при ошибке создания пользователя с неспецифированной причиной
     */
    @Override
    @Transactional
    public RegisterUserResponse register(RegisterUserCommand registerUserCommand) {
        log.info("Starting user registration with email={} and phone={}",
                dataMasker.maskEmail(registerUserCommand.email()),
                dataMasker.maskPhone(registerUserCommand.phone()));

        validateEmailUniqueness(registerUserCommand.email());
        validatePhoneUniqueness(registerUserCommand.phone());

        var hashedPassword = passwordHasher.hash(registerUserCommand.rawPassword());

        var domain = UserIdentity.createNew(
                UUID.randomUUID(),
                registerUserCommand.email(),
                registerUserCommand.phone(),
                hashedPassword,
                DEFAULT_HASH_ALGO,
                registerUserCommand.roles());

        try {
            var savedUserIdentity = userIdentityRepository.save(domain);
            log.info("User successfully registered with id={}", savedUserIdentity.getId());
            return new RegisterUserResponse(savedUserIdentity.getId());
        } catch (DataIntegrityViolationException violationException) {
            var mappedException = mapConstraintViolation(violationException, registerUserCommand);
            log.error("Data integrity violation while saving user with email: {}, phone: {}",
                    dataMasker.maskEmail(registerUserCommand.email()),
                    dataMasker.maskPhone(registerUserCommand.phone()),
                    violationException);
            throw mappedException;
        }
    }

    /**
     * Проверяет уникальность email адреса.
     *
     * <p>Если email не null и пользователь с таким email уже существует,
     * выбрасывается исключение {@link UserByEmailAlreadyExistsException}.
     *
     * @param email email адрес для проверки (может быть null)
     * @throws UserByEmailAlreadyExistsException если пользователь с таким email уже существует
     */
    private void validateEmailUniqueness(String email) {
        if (email != null && userIdentityRepository.findByEmail(email).isPresent()) {
            log.warn("Attempted registration with existing email: {}.", dataMasker.maskEmail(email));
            throw new UserByEmailAlreadyExistsException(email);
        }
    }

    /**
     * Проверяет уникальность номера телефона.
     *
     * <p>Если телефон не null и пользователь с таким телефоном уже существует,
     * выбрасывается исключение {@link UserByPhoneAlreadyExistsException}.
     *
     * @param phone номер телефона для проверки (может быть null)
     * @throws UserByPhoneAlreadyExistsException если пользователь с таким телефоном уже существует
     */
    private void validatePhoneUniqueness(String phone) {
        if (phone != null && userIdentityRepository.findByPhone(phone).isPresent()) {
            log.warn("Attempted registration with existing phone: {}.", dataMasker.maskPhone(phone));
            throw new UserByPhoneAlreadyExistsException(phone);
        }
    }

    /**
     * Преобразует исключение нарушения целостности данных в доменное исключение.
     *
     * <p>Метод анализирует имя нарушенного ограничения и возвращает соответствующее
     * исключение. Если ограничение не распознано, возвращается общее исключение
     * {@link DomainException}.
     *
     * @param exception   исходное исключение нарушения целостности
     * @param userCommand команда с данными пользователя для формирования сообщения об ошибке
     * @return доменное исключение, соответствующее типу нарушения
     */
    private DomainException mapConstraintViolation(DataIntegrityViolationException exception,
                                                   RegisterUserCommand userCommand) {
        var constraintName = extractConstraintName(exception);
        if (UNIQUE_EMAIL_CONSTRAINT_NAME.equals(constraintName) && userCommand.email() != null) {
            return new UserByEmailAlreadyExistsException(userCommand.email());
        }
        if (UNIQUE_PHONE_CONSTRAINT_NAME.equals(constraintName) && userCommand.phone() != null) {
            return new UserByPhoneAlreadyExistsException(userCommand.phone());
        }
        return new DomainException(DomainExceptionCode.INVALID_CREATE_USER_REQUEST_DATA);
    }

    /**
     * Извлекает имя ограничения уникальности из исключения.
     *
     * <p>Метод анализирует сообщение корневой причины исключения и ищет
     * в нем известные имена ограничений.
     *
     * @param violationException исключение нарушения целостности данных
     * @return имя ограничения или {@code null}, если ограничение не распознано
     */
    @Nullable
    private String extractConstraintName(DataIntegrityViolationException violationException) {
        var rootCause = violationException.getRootCause();
        if (rootCause != null) {
            var causeMessage = rootCause.getMessage();
            if (causeMessage.contains(UNIQUE_EMAIL_CONSTRAINT_NAME)) {
                return UNIQUE_EMAIL_CONSTRAINT_NAME;
            }
            if (causeMessage.contains(UNIQUE_PHONE_CONSTRAINT_NAME)) {
                return UNIQUE_PHONE_CONSTRAINT_NAME;
            }
        }
        return null;
    }
}