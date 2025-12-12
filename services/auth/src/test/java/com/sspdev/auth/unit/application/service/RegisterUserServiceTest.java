package com.sspdev.auth.unit.application.service;

import com.sspdev.auth.application.service.RegisterUserService;
import com.sspdev.auth.domain.exception.DomainErrorCode;
import com.sspdev.auth.domain.exception.DomainException;
import com.sspdev.auth.domain.exception.UserByEmailAlreadyExistsException;
import com.sspdev.auth.domain.exception.UserByPhoneAlreadyExistsException;
import com.sspdev.auth.domain.model.UserIdentity;
import com.sspdev.auth.domain.port.in.RegisterUserResponse;
import com.sspdev.auth.domain.port.out.PasswordHasher;
import com.sspdev.auth.domain.port.out.UserIdentityRepository;
import com.sspdev.auth.unit.TestDataUtil;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@ExtendWith(MockitoExtension.class)
public class RegisterUserServiceTest {

    @Mock
    private UserIdentityRepository userIdentityRepository;
    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private RegisterUserService registerUserService;

    @Test
    void register_shouldRegisterNewUser_whenRequestDtoValid() {
        var registerUserCommand = TestDataUtil.getValidRegisterUserCommand();
        var userIdentityId = UUID.randomUUID();

        when(userIdentityRepository.findByEmail(registerUserCommand.email())).thenReturn(Optional.empty());
        when(userIdentityRepository.findByPhone(registerUserCommand.phone())).thenReturn(Optional.empty());
        when(passwordHasher.hash(registerUserCommand.rawPassword())).thenReturn("hashedPassword");

        when(userIdentityRepository.save(any(UserIdentity.class))).thenAnswer(invocation -> {
            UserIdentity argument = invocation.getArgument(0);
            return new UserIdentity(userIdentityId,
                    argument.getEmail(),
                    argument.getPhone(),
                    argument.getPasswordHash(),
                    argument.getPasswordHash());
        });

        var actualResponseDto = registerUserService.register(registerUserCommand);

        assertThat(actualResponseDto.id()).isEqualTo(userIdentityId);
        verify(userIdentityRepository, times(1)).findByEmail(registerUserCommand.email());
        verify(userIdentityRepository, times(1)).findByPhone(registerUserCommand.phone());
        verify(passwordHasher, times(1)).hash(registerUserCommand.rawPassword());
    }

    @Test
    void register_shouldThrowEmailAlreadyExists_whenEmailIsInDb() {
        var validRegisterUserCommand = TestDataUtil.getValidRegisterUserCommand();

        when(userIdentityRepository.findByEmail(validRegisterUserCommand.email())).thenReturn(Optional.of(UserIdentity.builder().build()));

        assertThrows(UserByEmailAlreadyExistsException.class, () -> registerUserService.register(validRegisterUserCommand));
    }

    @Test
    void register_shouldThrowPhoneAlreadyExists_whenPhoneIsInDb() {
        var validRegisterUserCommand = TestDataUtil.getValidRegisterUserCommand();

        when(userIdentityRepository.findByPhone(validRegisterUserCommand.phone())).thenReturn(Optional.of(UserIdentity.builder().build()));

        assertThrows(UserByPhoneAlreadyExistsException.class, () -> registerUserService.register(validRegisterUserCommand));
    }

    @Test
    void register_shouldThrowDataIntegrityViolationException_whenEmailIsInDb() {
        var validRegisterUserCommand = TestDataUtil.getValidRegisterUserCommand();

        when(userIdentityRepository.findByEmail(validRegisterUserCommand.email())).thenReturn(Optional.empty());
        when(userIdentityRepository.findByPhone(validRegisterUserCommand.phone())).thenReturn(Optional.empty());
        when(passwordHasher.hash(validRegisterUserCommand.rawPassword())).thenReturn("hashedPassword");
        when(userIdentityRepository.save(any(UserIdentity.class))).thenThrow(new DataIntegrityViolationException("error", new Throwable("unique_user_email")));

        var emailAlreadyExistsException = assertThrows(UserByEmailAlreadyExistsException.class, () -> registerUserService.register(validRegisterUserCommand));

        var errorCode = emailAlreadyExistsException.getErrorCode();
        assertThat(errorCode).isEqualTo(DomainErrorCode.USER_BY_EMAIL_ALREADY_EXISTS);
    }

    @Test
    void register_shouldThrowDataIntegrityViolationException_whenPhoneIsInDb() {
        var validRegisterUserCommand = TestDataUtil.getValidRegisterUserCommand();

        when(userIdentityRepository.findByEmail(validRegisterUserCommand.email())).thenReturn(Optional.empty());
        when(userIdentityRepository.findByPhone(validRegisterUserCommand.phone())).thenReturn(Optional.empty());
        when(passwordHasher.hash(validRegisterUserCommand.rawPassword())).thenReturn("hashedPassword");
        when(userIdentityRepository.save(any(UserIdentity.class))).thenThrow(new DataIntegrityViolationException("error", new Throwable("unique_user_phone")));

        var phoneAlreadyExistsException = assertThrows(UserByPhoneAlreadyExistsException.class, () -> registerUserService.register(validRegisterUserCommand));
        var errorCode = phoneAlreadyExistsException.getErrorCode();
        assertThat(errorCode).isEqualTo(DomainErrorCode.USER_BY_PHONE_ALREADY_EXISTS);
    }

    @Test
    void register_shouldThrowDataIntegrityViolationException_whenTrySaveSameEmailsInTwoThreads() throws Exception {
        var validRegisterUserCommand = TestDataUtil.getValidRegisterUserCommand();
        var userIdentityId = UUID.randomUUID();

        when(userIdentityRepository.findByEmail(validRegisterUserCommand.email())).thenReturn(Optional.empty());
        when(userIdentityRepository.findByPhone(validRegisterUserCommand.phone())).thenReturn(Optional.empty());
        when(passwordHasher.hash(validRegisterUserCommand.rawPassword())).thenReturn("hashedPassword");

        AtomicBoolean firstCall = new AtomicBoolean(true);
        when(userIdentityRepository.save(any(UserIdentity.class))).thenAnswer(invocation -> {
            if (firstCall.compareAndSet(true, false)) {
                UserIdentity arg = invocation.getArgument(0);
                return new UserIdentity(
                        userIdentityId,
                        arg.getEmail(),
                        arg.getPhone(),
                        arg.getPasswordHash(),
                        arg.getPasswordHash()
                );
            } else {
                throw new DataIntegrityViolationException("error", new Throwable("unique_user_email"));
            }
        });

        List<RegisterUserResponse> successes;
        List<Throwable> errors;
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Callable<RegisterUserResponse> task = () -> registerUserService.register(validRegisterUserCommand);
            Future<RegisterUserResponse> firstResponseFuture = executor.submit(task);
            Future<RegisterUserResponse> secondResponseFuture = executor.submit(task);

            successes = new ArrayList<>();
            errors = new ArrayList<>();
            List<Future<RegisterUserResponse>> futures = new ArrayList<>();
            futures.add(firstResponseFuture);
            futures.add(secondResponseFuture);

            for (Future<RegisterUserResponse> future : futures) {
                try {
                    successes.add(future.get());
                } catch (ExecutionException exception) {
                    errors.add(exception.getCause());
                }
            }
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }

        assertThat(successes.size()).isEqualTo(1);
        assertThat(errors.size()).isEqualTo(1);
        assertThat(errors.getFirst()).isInstanceOf(UserByEmailAlreadyExistsException.class);
    }

    @Test
    void register_shouldThrowDataIntegrityViolationException_whenTrySaveSamePhonesInTwoThreads() throws Exception {
        var validRegisterUserCommand = TestDataUtil.getValidRegisterUserCommand();
        var userIdentityId = UUID.randomUUID();

        when(userIdentityRepository.findByEmail(validRegisterUserCommand.email())).thenReturn(Optional.empty());
        when(userIdentityRepository.findByPhone(validRegisterUserCommand.phone())).thenReturn(Optional.empty());
        when(passwordHasher.hash(validRegisterUserCommand.rawPassword())).thenReturn("hashedPassword");

        AtomicBoolean firstCall = new AtomicBoolean(true);
        when(userIdentityRepository.save(any(UserIdentity.class))).thenAnswer(invocation -> {
            if (firstCall.compareAndSet(true, false)) {
                UserIdentity arg = invocation.getArgument(0);
                return new UserIdentity(
                        userIdentityId,
                        arg.getEmail(),
                        arg.getPhone(),
                        arg.getPasswordHash(),
                        arg.getPasswordHash()
                );
            } else {
                throw new DataIntegrityViolationException("error", new Throwable("unique_user_phone"));
            }
        });

        List<RegisterUserResponse> successes;
        List<Throwable> errors;
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Callable<RegisterUserResponse> task = () -> registerUserService.register(validRegisterUserCommand);
            Future<RegisterUserResponse> firstResponseFuture = executor.submit(task);
            Future<RegisterUserResponse> secondResponseFuture = executor.submit(task);

            successes = new ArrayList<>();
            errors = new ArrayList<>();
            List<Future<RegisterUserResponse>> futures = new ArrayList<>();
            futures.add(firstResponseFuture);
            futures.add(secondResponseFuture);

            for (Future<RegisterUserResponse> future : futures) {
                try {
                    successes.add(future.get());
                } catch (ExecutionException exception) {
                    errors.add(exception.getCause());
                }
            }
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }

        assertThat(successes.size()).isEqualTo(1);
        assertThat(errors.size()).isEqualTo(1);
        assertThat(errors.getFirst()).isInstanceOf(UserByPhoneAlreadyExistsException.class);
    }

    @Test
    void register_shouldThrowDomainException_whenDataIntegrityViolationUnknownConstraint() {
        var registerUserCommand = TestDataUtil.getValidRegisterUserCommand();

        when(userIdentityRepository.findByEmail(registerUserCommand.email())).thenReturn(Optional.empty());
        when(userIdentityRepository.findByPhone(registerUserCommand.phone())).thenReturn(Optional.empty());
        when(passwordHasher.hash(registerUserCommand.rawPassword())).thenReturn("hashedPassword");

        when(userIdentityRepository.save(any(UserIdentity.class))).thenThrow(new DataIntegrityViolationException("error", new Throwable("unknown_constraint")));

        var domainException = assertThrows(DomainException.class, () -> registerUserService.register(registerUserCommand));
        var errorCode = domainException.getErrorCode();
        assertThat(errorCode).isEqualTo(DomainErrorCode.INVALID_CREATE_USER_REQUEST_DATA);
    }

    @Test
    void register_shouldThrowDomainException_whenDataIntegrityViolationNullRootCause() {
        var registerUserCommand = TestDataUtil.getValidRegisterUserCommand();

        when(userIdentityRepository.findByEmail(registerUserCommand.email())).thenReturn(Optional.empty());
        when(userIdentityRepository.findByPhone(registerUserCommand.phone())).thenReturn(Optional.empty());
        when(passwordHasher.hash(registerUserCommand.rawPassword())).thenReturn("hashedPassword");

        when(userIdentityRepository.save(any(UserIdentity.class))).thenThrow(new DataIntegrityViolationException("error", null));

        var domainException = assertThrows(DomainException.class, () -> registerUserService.register(registerUserCommand));
        var errorCode = domainException.getErrorCode();
        assertThat(errorCode).isEqualTo(DomainErrorCode.INVALID_CREATE_USER_REQUEST_DATA);
    }
}