package com.sspdev.auth.presentation.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserIdentityResponseDto(UUID id, String message) {
}