package com.sspdev.auth.domain.port.in;

import com.sspdev.auth.infrastructure.persistence.entity.Role;
import lombok.Builder;

import java.util.Set;

@Builder
public record RegisterUserCommand(String email,
                                  String phone,
                                  String rawPassword,
                                  Set<Role> roles) {
}