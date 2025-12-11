package com.sspdev.auth.domain.exception;

public class NotValidPasswordException extends DomainException {

    public NotValidPasswordException() {
        super(DomainErrorCode.INVALID_PASSWORD);
    }

    public NotValidPasswordException(DomainErrorCode errorCode) {
        super(errorCode);
    }
}