package com.sspdev.auth.domain.port.in;

import lombok.Builder;

import java.util.Set;

import com.sspdev.auth.infrastructure.persistence.entity.Role;

@Builder
public record RegisterUserCommand(String email,
                                  String phone,
                                  String rawPassword,
                                  Set<Role> roles) {
}