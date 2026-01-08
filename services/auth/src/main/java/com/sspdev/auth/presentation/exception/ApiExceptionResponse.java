package com.sspdev.auth.presentation.exception;

import com.sspdev.auth.domain.exception.DomainExceptionCode;
import lombok.Builder;

@Builder
public record ApiExceptionResponse(DomainExceptionCode errorCode,
                                   String message,
                                   Object... args) {
}