package com.sspdev.auth.domain.exception;

public class EmptyUserIdException extends DomainException {

    public EmptyUserIdException() {
        super(DomainErrorCode.INVALID_ID);
    }
}