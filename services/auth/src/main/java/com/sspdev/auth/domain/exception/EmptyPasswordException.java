package com.sspdev.auth.domain.exception;

public class EmptyPasswordException extends DomainException {

    public EmptyPasswordException() {
        super(DomainExceptionCode.PASSWORD_REQUIRED);
    }
}