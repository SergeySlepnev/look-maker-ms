package com.sspdev.auth.domain.exception;

public class EmptyPasswordException extends DomainException {

    public EmptyPasswordException() {
        super(DomainErrorCode.PASSWORD_REQUIRED);
    }
}