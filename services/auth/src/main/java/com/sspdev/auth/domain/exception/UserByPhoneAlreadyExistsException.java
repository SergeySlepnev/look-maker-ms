package com.sspdev.auth.domain.exception;

public class UserByPhoneAlreadyExistsException extends DomainException {

    public UserByPhoneAlreadyExistsException(String phone) {
        super(DomainErrorCode.USER_BY_PHONE_ALREADY_EXISTS, phone);
    }
}