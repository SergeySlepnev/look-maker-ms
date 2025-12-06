package com.sspdev.auth.infrastructure.persistence.adapter.mapper;

import com.sspdev.auth.domain.model.UserIdentityDomain;
import com.sspdev.auth.infrastructure.persistence.entity.UserIdentityEntity;
import org.springframework.stereotype.Component;

@Component
public class UserIdentityMapper {

    public UserIdentityDomain toDomain(UserIdentityEntity jpaUserIdentity) {
        if (jpaUserIdentity == null) {
            return null;
        }
        return UserIdentityDomain.builder()
                .id(jpaUserIdentity.getId())
                .email(jpaUserIdentity.getEmail())
                .phone(jpaUserIdentity.getPhone())
                .passwordHash(jpaUserIdentity.getPasswordHash())
                .passwordAlgo(jpaUserIdentity.getPasswordAlgo())
                .build();
    }

    public UserIdentityEntity toEntity(UserIdentityDomain domainUserIdentity) {
        if (domainUserIdentity == null) {
            return null;
        }
        return UserIdentityEntity.builder()
                .id(domainUserIdentity.getId())
                .email(domainUserIdentity.getEmail())
                .phone(domainUserIdentity.getPhone())
                .passwordHash(domainUserIdentity.getPasswordHash())
                .passwordAlgo(domainUserIdentity.getPasswordAlgo())
                .build();
    }
}