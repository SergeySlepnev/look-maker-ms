package com.sspdev.auth.domain.model;

import com.sspdev.auth.domain.exception.EmptyPasswordException;
import com.sspdev.auth.domain.exception.EmptyUserContactsException;
import com.sspdev.auth.domain.exception.EmptyUserIdException;
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

    public static UserIdentity createNew(UUID id, String email, String phone, String passwordHash, String passwordAlgo) {
        if (id == null) throw new EmptyUserIdException();
        if ((email == null || email.isBlank()) && (phone == null || phone.isBlank()))
            throw new EmptyUserContactsException();
        if (passwordHash == null || passwordHash.isBlank())
            throw new EmptyPasswordException();

        return UserIdentity.builder()
                .id(id)
                .email(email)
                .phone(phone)
                .passwordHash(passwordHash)
                .passwordAlgo(passwordAlgo)
                .build();
    }
}