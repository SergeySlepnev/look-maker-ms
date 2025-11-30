package com.sspdev.auth.unit;

import com.sspdev.auth.domain.model.UserIdentity;
import com.sspdev.auth.infrastructure.persistence.entity.UserIdentityEntity;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class TestDataUtil {

    private static final UUID USER_IDENTITY_ID = UUID.randomUUID();

    public static UserIdentityEntity getUserIdentityJpaEntity() {
        return UserIdentityEntity.builder()
                .id(USER_IDENTITY_ID)
                .email("test_user@gmail.com")
                .phone("8-925-869-96-98")
                .passwordHash("dummyPasswordHash")
                .passwordAlgo("dummyPasswordAlgo")
                .build();
    }

    public UserIdentity getUserIdentityDomainModel() {
        return UserIdentity.builder()
                .id(USER_IDENTITY_ID)
                .email("test_user@gmail.com")
                .phone("8-925-869-96-98")
                .passwordHash("dummyPasswordHash")
                .passwordAlgo("dummyPasswordAlgo")
                .build();

    }
}