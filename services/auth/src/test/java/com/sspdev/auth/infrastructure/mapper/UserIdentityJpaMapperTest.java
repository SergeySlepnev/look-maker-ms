package com.sspdev.auth.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import com.sspdev.auth.domain.model.UserIdentity;
import com.sspdev.auth.infrastructure.persistence.entity.UserIdentityJpaEntity;
import com.sspdev.auth.infrastructure.persistence.mapper.UserIdentityJpaMapper;
import com.sspdev.auth.testutil.TestDataUtil;

public class UserIdentityJpaMapperTest {

    private final UserIdentityJpaMapper identityMapper = new UserIdentityJpaMapper();

    @Test
    void toDomain_shouldMapFromJpaEntity_toDomainModel() {
        var sourceJpaEntity = TestDataUtil.getUserIdentityJpaEntity();
        var expectedUserIdentityDomainModel = TestDataUtil.getUserIdentityDomainModel();

        var actualUserIdentityDomainModel = identityMapper.toDomain(sourceJpaEntity);

        assertThat(actualUserIdentityDomainModel)
                .usingRecursiveComparison()
                .isEqualTo(expectedUserIdentityDomainModel);
    }

    @Test
    void toDomain_shouldMapFromJpaEntity_toDomainModel_whenJpaEntityContainsNullFields() {
        UUID id = UUID.randomUUID();
        UserIdentityJpaEntity entityWithNullFields = UserIdentityJpaEntity.builder()
                .id(id)
                .email(null)
                .phone(null)
                .passwordHash(null)
                .passwordAlgo(null)
                .roles(null)
                .build();

        UserIdentity expectedDomainModel = UserIdentity.builder()
                .id(id)
                .email(null)
                .phone(null)
                .passwordHash(null)
                .passwordAlgo(null)
                .roles(null)
                .build();

        var actualUserIdentityDomainModel = identityMapper.toDomain(entityWithNullFields);
        assertThat(actualUserIdentityDomainModel)
                .usingRecursiveComparison()
                .isEqualTo(expectedDomainModel);
    }

    @Test
    void toDomain_shouldReturnNull_forNullJpaEntity() {
        assertThat(identityMapper.toDomain(null)).isNull();
    }

    @Test
    void toEntity_shouldMapFromDomainModel_toJpaEntity() {
        var sourceDomainModel = TestDataUtil.getUserIdentityDomainModel();
        var expectedJpaUserIdentityEntity = TestDataUtil.getUserIdentityJpaEntity();
        var actualJpaUserIdentityEntity = identityMapper.toEntity(sourceDomainModel);

        assertThat(actualJpaUserIdentityEntity)
                .usingRecursiveComparison()
                .isEqualTo(expectedJpaUserIdentityEntity);
    }

    @Test
    void toEntity_shouldMapFromDomainModel_toJpaEntity_whenDomainModelContainsNullFields() {
        UUID id = UUID.randomUUID();
        UserIdentity domainModelWithNulls = UserIdentity.builder()
                .id(id)
                .email(null)
                .phone(null)
                .passwordHash(null)
                .passwordAlgo(null)
                .roles(null)
                .build();

        UserIdentityJpaEntity expectedJpaEntity = UserIdentityJpaEntity.builder()
                .id(id)
                .email(null)
                .phone(null)
                .passwordHash(null)
                .passwordAlgo(null)
                .roles(null)
                .build();

        var actualJpaUserIdentityEntity = identityMapper.toEntity(domainModelWithNulls);

        assertThat(actualJpaUserIdentityEntity)
                .usingRecursiveComparison()
                .isEqualTo(expectedJpaEntity);
    }

    @Test
    void toEntity_shouldReturnNull_forNullDomainModel() {
        assertThat(identityMapper.toEntity(null)).isNull();
    }
}