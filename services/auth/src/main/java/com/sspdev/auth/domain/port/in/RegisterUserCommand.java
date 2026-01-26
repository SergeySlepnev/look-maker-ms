package com.sspdev.auth.domain.port.in;

import lombok.Builder;

@Builder
public record RegisterUserCommand(String email,
                                  String phone,
                                  String rawPassword) {
}