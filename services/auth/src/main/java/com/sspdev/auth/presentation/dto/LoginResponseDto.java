package com.sspdev.auth.presentation.dto;

import lombok.Builder;

@Builder
public record LoginResponseDto(

        String accessToken,
        String tokenType) {
}