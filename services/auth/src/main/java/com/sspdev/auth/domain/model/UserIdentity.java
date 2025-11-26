package com.sspdev.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "passwordHash")
@Builder
@Getter
public class UserIdentity {

    @EqualsAndHashCode.Include
    private final UUID id;

    private final String email;

    private final String phone;

    private final String passwordHash;

    private final String passwordAlgo;
}