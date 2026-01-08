package com.sspdev.auth.testutil;

import com.sspdev.auth.domain.model.UserIdentity;
import com.sspdev.auth.domain.port.in.RegisterUserCommand;
import com.sspdev.auth.domain.port.in.RegisterUserResponse;
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

    public static UserIdentity getUserIdentityDomainModelWithNullId() {
        return UserIdentity.builder()
                .email("test_user@gmail.com")
                .phone("8-925-869-96-98")
                .passwordHash("dummyPasswordHash")
                .passwordAlgo("dummyPasswordAlgo")
                .build();
    }

    public static UserIdentity getUserIdentityDomainModelWithNullEmail() {
        return UserIdentity.builder()
                .id(USER_IDENTITY_ID)
                .phone("8-925-869-96-98")
                .passwordHash("dummyPasswordHash")
                .passwordAlgo("dummyPasswordAlgo")
                .build();
    }

    public static UserIdentity getUserIdentityDomainModelWithNullPhone() {
        return UserIdentity.builder()
                .id(USER_IDENTITY_ID)
                .email("test_user@gmail.com")
                .passwordHash("dummyPasswordHash")
                .passwordAlgo("dummyPasswordAlgo")
                .build();
    }

    public static UserIdentity getUserIdentityDomainModelWithNullPassword() {
        return UserIdentity.builder()
                .id(USER_IDENTITY_ID)
                .email("test_user@gmail.com")
                .phone("8-925-869-96-98")
                .passwordAlgo("dummyPasswordAlgo")
                .build();
    }

    public static UserIdentity getUserIdentityDomainModelWithNullPasswordAlgo() {
        return UserIdentity.builder()
                .id(USER_IDENTITY_ID)
                .email("test_user@gmail.com")
                .phone("8-925-869-96-98")
                .passwordHash("dummyPasswordHash")
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

    public static RegisterUserResponse getRegisterUserResponse() {
        return new RegisterUserResponse(USER_IDENTITY_ID);
    }
}