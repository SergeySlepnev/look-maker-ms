package com.sspdev.auth.application.service;

import com.sspdev.auth.domain.exception.DomainErrorCode;
import com.sspdev.auth.domain.exception.DomainException;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {

    private static final String DEFAULT_HASH_ALGO = "bcrypt";
    private static final String UNIQUE_EMAIL_CONSTRAINT_NAME = "unique_user_email";
    private static final String UNIQUE_PHONE_CONSTRAINT_NAME = "unique_user_phone";

    private final UserIdentityRepository userIdentityRepository;
    private final PasswordHasher passwordHasher;

    @Override
    @Transactional
    public RegisterUserResponse register(RegisterUserCommand registerUserCommand) {
        var email = registerUserCommand.email();
        if (email != null && userIdentityRepository.findByEmail(email).isPresent()) {
            throw new UserByEmailAlreadyExistsException(email);
        }
        var phone = registerUserCommand.phone();
        if (phone != null && userIdentityRepository.findByPhone(phone).isPresent()) {
            throw new UserByPhoneAlreadyExistsException(phone);
        }

        var hashedPassword = passwordHasher.hash(registerUserCommand.rawPassword());

        var domain = UserIdentity.createNew(
                UUID.randomUUID(),
                email,
                phone,
                hashedPassword,
                DEFAULT_HASH_ALGO);

        try {
            var savedUserIdentity = userIdentityRepository.save(domain);
            return new RegisterUserResponse(savedUserIdentity.getId());
        } catch (DataIntegrityViolationException violationException) {
            var constraintName = extractConstraintName(violationException);
            if (UNIQUE_EMAIL_CONSTRAINT_NAME.equals(constraintName) && email != null) {
                throw new UserByEmailAlreadyExistsException(email);
            }
            if (UNIQUE_PHONE_CONSTRAINT_NAME.equals(constraintName) && phone != null) {
                throw new UserByPhoneAlreadyExistsException(phone);
            } else {
                throw new DomainException(DomainErrorCode.INVALID_CREATE_USER_REQUEST_DATA);
            }
        }
    }

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