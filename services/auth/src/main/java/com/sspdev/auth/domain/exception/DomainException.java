package com.sspdev.auth.domain.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {

    private DomainExceptionCode errorCode;
    private Object[] args;

    public DomainException(DomainExceptionCode messageKey, Object... args) {
        this.errorCode = messageKey;
        this.args = args == null ? new Object[0] : args;
    }

    public DomainException(String message) {
        super(message);
    }
}