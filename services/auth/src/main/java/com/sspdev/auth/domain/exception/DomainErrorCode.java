package com.sspdev.auth.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DomainErrorCode {

    INVALID_CREATE_USER_REQUEST_DATA("domain.INVALID_CREATE_USER_REQUEST_DATA"),
    INVALID_ID("domain.INVALID_ID"),
    CONTACT_REQUIRED("domain.CONTACT_REQUIRED"),
    PASSWORD_REQUIRED("domain.PASSWORD_REQUIRED"),
    USER_BY_EMAIL_ALREADY_EXISTS("domain.USER_BY_EMAIL_ALREADY_EXISTS"),
    USER_BY_PHONE_ALREADY_EXISTS("domain.USER_BY_PHONE_ALREADY_EXISTS");

    private final String messageKey;
}