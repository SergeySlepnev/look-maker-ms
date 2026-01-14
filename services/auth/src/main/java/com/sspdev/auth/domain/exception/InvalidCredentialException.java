package com.sspdev.auth.domain.exception;

public class InvalidCredentialException extends DomainException {

    public InvalidCredentialException() {
        super(DomainExceptionCode.INVALID_CREDENTIALS);
    }
}