package com.sspdev.auth.unit.mapper;

import com.sspdev.auth.domain.model.UserIdentity;
import com.sspdev.auth.infrastructure.persistence.adapter.mapper.UserIdentityMapper;
import com.sspdev.auth.infrastructure.persistence.entity.UserIdentityEntity;
import com.sspdev.auth.unit.TestDataUtil;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class UserIdentityMapperTest {

    private final UserIdentityMapper identityMapper = new UserIdentityMapper();

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
        UserIdentityEntity entityWithNullFields = UserIdentityEntity.builder()
                .id(id)
                .email(null)
                .phone(null)
                .passwordHash(null)
                .passwordAlgo(null)
                .build();

        UserIdentity expectedDomainModel = UserIdentity.builder()
                .id(id)
                .email(null)
                .phone(null)
                .passwordHash(null)
                .passwordAlgo(null)
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
                .build();

        UserIdentityEntity expectedJpaEntity = UserIdentityEntity.builder()
                .id(id)
                .email(null)
                .phone(null)
                .passwordHash(null)
                .passwordAlgo(null)
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