package com.sspdev.auth.presentation.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import com.sspdev.auth.domain.exception.DomainException;
import com.sspdev.auth.domain.exception.EmptyPasswordException;
import com.sspdev.auth.domain.exception.EmptyUserContactsException;
import com.sspdev.auth.domain.exception.EmptyUserIdException;
import com.sspdev.auth.domain.exception.NotValidPasswordException;
import com.sspdev.auth.domain.exception.UserByEmailAlreadyExistsException;
import com.sspdev.auth.domain.exception.UserByPhoneAlreadyExistsException;
import com.sspdev.auth.domain.port.out.MessageResolver;

@ExtendWith(MockitoExtension.class)
class ApiExceptionHandlerTest {

    @Mock
    private MessageResolver messageResolver;
    @InjectMocks
    private ApiExceptionHandler exceptionHandler;
    @Captor
    private ArgumentCaptor<Object[]> argsCaptor;

    static Stream<Arguments> getExceptionsForBadRequest() {
        return Stream.of(
                Arguments.of(new EmptyPasswordException()),
                Arguments.of(new EmptyUserContactsException()),
                Arguments.of(new EmptyUserIdException()),
                Arguments.of(new NotValidPasswordException()
                ));
    }

    static Stream<Arguments> getExceptionsForConflictStatus() {
        return Stream.of(
                Arguments.of(new UserByEmailAlreadyExistsException("dymmy@gmail.com")),
                Arguments.of(new UserByPhoneAlreadyExistsException("8989898989890")
                ));
    }

    @ParameterizedTest
    @MethodSource("getExceptionsForBadRequest")
    @DisplayName("(BAD_REQUEST) with English")
    void handleDomainException_shouldReturnBadRequest_andResolveEnglishMessage(DomainException domainException) {
        var messageKey = domainException.getErrorCode().getMessageKey();
        var args = domainException.getArgs();

        when(messageResolver.resolve(eq(messageKey), eq(args), eq(Locale.ENGLISH))).thenReturn("Resolved message");
        var response = exceptionHandler.handleDomainException(domainException, Locale.ENGLISH);
        var body = response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.errorCode()).isEqualTo(domainException.getErrorCode());
        assertThat(body.message()).isEqualTo("Resolved message");
        assertThat(body.args()).isEqualTo(args);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        verify(messageResolver, times(1)).resolve(eq(messageKey), eq(args), eq(Locale.ENGLISH));
    }

    @ParameterizedTest
    @MethodSource("getExceptionsForBadRequest")
    @DisplayName("(BAD_REQUEST) with Russian")
    void handleDomainException_shouldReturnBadRequest_andResolveRussianMessage(DomainException domainException) {
        var messageKey = domainException.getErrorCode().getMessageKey();
        var args = domainException.getArgs();
        var russianLocale = new Locale.Builder().setLanguage("ru").build();

        when(messageResolver.resolve(eq(messageKey), eq(args), eq(russianLocale))).thenReturn("Сообщение");
        var response = exceptionHandler.handleDomainException(domainException, russianLocale);
        var body = response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.errorCode()).isEqualTo(domainException.getErrorCode());
        assertThat(body.message()).isEqualTo("Сообщение");
        assertThat(body.args()).isEqualTo(args);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        verify(messageResolver, times(1)).resolve(eq(messageKey), eq(args), eq(russianLocale));
    }

    @ParameterizedTest
    @MethodSource("getExceptionsForConflictStatus")
    @DisplayName("(CONFLICT) with English")
    void handleDomainException_shouldReturnConflictStatus_andResolveEnglishMessage(DomainException domainException) {
        var messageKey = domainException.getErrorCode().getMessageKey();
        var args = domainException.getArgs();

        when(messageResolver.resolve(messageKey, args, Locale.ENGLISH)).thenReturn("Resolved message");
        var response = exceptionHandler.handleDomainException(domainException, Locale.ENGLISH);
        var body = response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.errorCode()).isEqualTo(domainException.getErrorCode());
        assertThat(body.message()).isEqualTo("Resolved message");
        assertThat(body.args()).isEqualTo(args);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        verify(messageResolver, times(1)).resolve(eq(messageKey), eq(args), eq(Locale.ENGLISH));
    }

    @ParameterizedTest
    @MethodSource("getExceptionsForConflictStatus")
    @DisplayName("(CONFLICT) with Russian")
    void handleDomainException_shouldReturnConflictStatus_andResolveRussianMessage(DomainException domainException) {
        var messageKey = domainException.getErrorCode().getMessageKey();
        var args = domainException.getArgs();
        var russianLocale = new Locale.Builder().setLanguage("ru").build();

        when(messageResolver.resolve(messageKey, args, russianLocale)).thenReturn("Сообщение");
        var response = exceptionHandler.handleDomainException(domainException, russianLocale);
        var body = response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.errorCode()).isEqualTo(domainException.getErrorCode());
        assertThat(body.message()).isEqualTo("Сообщение");
        assertThat(body.args()).isEqualTo(args);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        verify(messageResolver, times(1)).resolve(eq(messageKey), eq(args), eq(russianLocale));
    }

    @Test
    void handleDomainException_shouldReturnEmptyMessage_whenNoMessageKey() {
        var passwordException = new EmptyPasswordException();
        var messageKey = passwordException.getErrorCode().getMessageKey();
        when(messageResolver.resolve(eq(messageKey), any(), eq(Locale.ENGLISH))).thenReturn(null);

        var response = exceptionHandler.handleDomainException(passwordException, Locale.ENGLISH);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        var body = response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.message()).isBlank();
    }

    @ParameterizedTest
    @MethodSource("getExceptionsForBadRequest")
    void handleDomainException_shouldCaptureArgs_whenExceptionHasNoArgs(DomainException domainException) {
        var messageKey = domainException.getErrorCode().getMessageKey();
        var expectedArgs = domainException.getArgs();
        when(messageResolver.resolve(eq(messageKey), argsCaptor.capture(), eq(Locale.ENGLISH))).thenReturn("Resolved message");

        exceptionHandler.handleDomainException(domainException, Locale.ENGLISH);

        verify(messageResolver).resolve(eq(messageKey), eq(expectedArgs), eq(Locale.ENGLISH));
        var actualArgs = argsCaptor.getValue();
        assertThat(actualArgs).isEqualTo(expectedArgs);
        assertThat(actualArgs.length).isZero();
    }

    @ParameterizedTest
    @MethodSource("getExceptionsForConflictStatus")
    void handleDomainException_shouldCaptureArgs_whenExceptionHasArg(DomainException domainException) {
        var messageKey = domainException.getErrorCode().getMessageKey();
        var expectedArgs = domainException.getArgs();
        when(messageResolver.resolve(eq(messageKey), argsCaptor.capture(), eq(Locale.ENGLISH))).thenReturn("Resolved message");

        exceptionHandler.handleDomainException(domainException, Locale.ENGLISH);

        verify(messageResolver).resolve(eq(messageKey), eq(expectedArgs), eq(Locale.ENGLISH));
        var actualArgs = argsCaptor.getValue();
        assertThat(actualArgs).isEqualTo(expectedArgs);
        assertThat(actualArgs.length).isEqualTo(1);
        assertThat(actualArgs).isEqualTo(expectedArgs);
    }
}