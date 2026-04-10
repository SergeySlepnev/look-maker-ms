package com.sspdev.auth.presentation.exception;

import lombok.Builder;

import com.sspdev.auth.domain.exception.DomainExceptionCode;

@Builder
public record ApiExceptionResponse(DomainExceptionCode errorCode,
                                   String message,
                                   Object... args) {
}