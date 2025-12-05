package com.sspdev.auth.domain.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {

    private DomainErrorCode errorCode;
    private Object[] args;

    public DomainException(DomainErrorCode messageKey, Object... args) {
        this.errorCode = messageKey;
        this.args = args == null ? new Object[0] : args;
    }

    public DomainException(String message) {
        super(message);
    }
}