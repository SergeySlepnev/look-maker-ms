package com.sspdev.auth.domain.exception;

public class EmptyUserContactsException extends DomainException {

    public EmptyUserContactsException() {
        super(DomainExceptionCode.CONTACT_REQUIRED);
    }
}