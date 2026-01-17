package com.sspdev.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequestDto(

        @NotBlank(message = "{error.email.must_not_be_null}")
        String email,

        @NotBlank(message = "{error.password.must_not_be_null}")
        String password) {
}