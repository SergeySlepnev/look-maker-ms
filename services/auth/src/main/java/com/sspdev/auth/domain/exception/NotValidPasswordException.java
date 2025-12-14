package com.sspdev.auth.domain.exception;

public class NotValidPasswordException extends DomainException {

    public NotValidPasswordException() {
        super(DomainExceptionCode.INVALID_PASSWORD);
    }

    public NotValidPasswordException(DomainExceptionCode errorCode) {
        super(errorCode);
    }
}