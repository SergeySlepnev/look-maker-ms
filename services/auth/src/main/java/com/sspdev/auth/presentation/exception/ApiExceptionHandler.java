package com.sspdev.auth.presentation.exception;

import com.sspdev.auth.domain.exception.DomainException;
import com.sspdev.auth.domain.exception.DomainExceptionCode;
import com.sspdev.auth.domain.port.out.MessageResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {

    private final MessageResolver messageResolver;

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiExceptionResponse> handleDomainException(DomainException exception, Locale locale) {
        var messageKey = exception.getErrorCode().getMessageKey();
        var message = messageResolver.resolve(messageKey, exception.getArgs(), locale);
        var httpStatus = mapCodeToHttpStatus(exception.getErrorCode());
        var errorResponse = ApiExceptionResponse.builder()
                .errorCode(exception.getErrorCode())
                .message(message)
                .args(exception.getArgs())
                .build();

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    private HttpStatus mapCodeToHttpStatus(DomainExceptionCode errorCode) {
        return switch (errorCode) {
            case INVALID_ID,
                 INVALID_PASSWORD,
                 INVALID_PASSWORD_NULL,
                 INVALID_PASSWORD_BLANK,
                 CONTACT_REQUIRED,
                 PASSWORD_REQUIRED,
                 INVALID_CREATE_USER_REQUEST_DATA,
                 INVALID_CREDENTIALS -> HttpStatus.BAD_REQUEST;
            case USER_BY_EMAIL_ALREADY_EXISTS,
                 USER_BY_PHONE_ALREADY_EXISTS -> HttpStatus.CONFLICT;
        };
    }
}