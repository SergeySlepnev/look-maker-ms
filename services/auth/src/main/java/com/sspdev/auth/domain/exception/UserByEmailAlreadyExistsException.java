package com.sspdev.auth.domain.exception;

public class UserByEmailAlreadyExistsException extends DomainException {

    public UserByEmailAlreadyExistsException(String email) {
        super(DomainErrorCode.USER_BY_EMAIL_ALREADY_EXISTS, email);
    }
}