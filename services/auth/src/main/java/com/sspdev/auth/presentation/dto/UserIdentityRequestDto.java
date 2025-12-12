package com.sspdev.auth.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserIdentityRequestDto(

        @Email(message = "{error.email.must_have_email_format}")
        @NotBlank(message = "{error.email.must_not_be_null}")
        @Size(min = 5, max = 128, message = "{error.email.must_be_between.max_min.length}")
        String email,

        @NotBlank(message = "{error.phone.must_not_be_null}")
        @Pattern(regexp = "^(?:\\\\+7|8)?[\\\\s-]?(?:\\$\\\\d{3}\\$|\\\\d{3})[\\\\s-]?\\\\d{3}[\\\\s-]?\\\\d{2}[\\\\s-]?\\\\d{2}$",
                message = "{error.phone.must_be_like_pattern}")
        String phone,

        @NotBlank(message = "{error.password.must_not_be_null}")
        @Size(min = 6, max = 128, message = "{error.password.min_max.length}")
        String password) {
}