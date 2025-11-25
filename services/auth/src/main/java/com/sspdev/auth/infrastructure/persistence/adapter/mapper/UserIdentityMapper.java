package com.sspdev.auth.infrastructure.persistence.adapter.mapper;

import com.sspdev.auth.domain.model.UserIdentity;
import com.sspdev.auth.infrastructure.persistence.entity.UserIdentityEntity;
import org.springframework.stereotype.Component;

@Component
public class UserIdentityMapper {

    public UserIdentity toDomain(UserIdentityEntity jpaUserIdentity) {
        if (jpaUserIdentity == null) {
            return null;
        }
        return UserIdentity.builder()
                .id(jpaUserIdentity.getId())
                .email(jpaUserIdentity.getEmail())
                .phone(jpaUserIdentity.getPhone())
                .passwordHash(jpaUserIdentity.getPasswordHash())
                .passwordAlgo(jpaUserIdentity.getPasswordAlgo())
                .build();
    }

    public UserIdentityEntity toEntity(UserIdentity domainUserIdentity) {
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