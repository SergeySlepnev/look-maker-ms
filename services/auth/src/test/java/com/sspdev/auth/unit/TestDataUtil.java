package com.sspdev.auth.unit;

import com.sspdev.auth.domain.model.UserIdentity;
import com.sspdev.auth.domain.port.in.RegisterUserCommand;
import com.sspdev.auth.infrastructure.persistence.entity.UserIdentityJpaEntity;
import com.sspdev.auth.presentation.dto.UserIdentityRequestDto;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class TestDataUtil {

    private static final UUID USER_IDENTITY_ID = UUID.randomUUID();

    public static UserIdentityJpaEntity getUserIdentityJpaEntity() {
        return UserIdentityJpaEntity.builder()
                .id(USER_IDENTITY_ID)
                .email("test_user@gmail.com")
                .phone("8-925-869-96-98")
                .passwordHash("dummyPasswordHash")
                .passwordAlgo("dummyPasswordAlgo")
                .build();
    }

    public static UserIdentity getUserIdentityDomainModel() {
        return UserIdentity.builder()
                .id(USER_IDENTITY_ID)
                .email("test_user@gmail.com")
                .phone("8-925-869-96-98")
                .passwordHash("dummyPasswordHash")
                .passwordAlgo("dummyPasswordAlgo")
                .build();

    }

    public static RegisterUserCommand getValidRegisterUserCommand() {
        return RegisterUserCommand.builder()
                .email("test_user@gmail.com")
                .phone("8-925-869-96-98")
                .rawPassword("dummyPassword")
                .build();
    }

    public static UserIdentityRequestDto getValidUserIdentityRequestDto() {
        return UserIdentityRequestDto.builder()
                .email("test_user@gmail.com")
                .phone("8-925-869-96-98")
                .rawPassword("dummyPassword")
                .build();
    }

    public static UserIdentityRequestDto getUserIdentityRequestDtoWithNullFields() {
        return UserIdentityRequestDto.builder().build();
    }
}