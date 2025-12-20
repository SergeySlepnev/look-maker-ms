package com.sspdev.auth.domain.exception;

public class UserByPhoneAlreadyExistsException extends DomainException {

    public UserByPhoneAlreadyExistsException(String phone) {
        super(DomainExceptionCode.USER_BY_PHONE_ALREADY_EXISTS, phone);
    }
}