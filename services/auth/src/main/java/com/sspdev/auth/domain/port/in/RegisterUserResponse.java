package com.sspdev.auth.domain.port.in;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RegisterUserResponse(UUID id) {
}