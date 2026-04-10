package com.sspdev.auth.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import com.sspdev.auth.domain.model.UserIdentity;
import com.sspdev.auth.infrastructure.persistence.entity.UserIdentityJpaEntity;

@Component
public class UserIdentityJpaMapper {

    public UserIdentity toDomain(UserIdentityJpaEntity jpaUserIdentity) {
        if (jpaUserIdentity == null) {
            return null;
        }
        return UserIdentity.builder()
                .id(jpaUserIdentity.getId())
                .email(jpaUserIdentity.getEmail())
                .phone(jpaUserIdentity.getPhone())
                .passwordHash(jpaUserIdentity.getPasswordHash())
                .passwordAlgo(jpaUserIdentity.getPasswordAlgo())
                .roles(jpaUserIdentity.getRoles())
                .build();
    }

    public UserIdentityJpaEntity toEntity(UserIdentity domainUserIdentity) {
        if (domainUserIdentity == null) {
            return null;
        }
        return UserIdentityJpaEntity.builder()
                .id(domainUserIdentity.getId())
                .email(domainUserIdentity.getEmail())
                .phone(domainUserIdentity.getPhone())
                .passwordHash(domainUserIdentity.getPasswordHash())
                .passwordAlgo(domainUserIdentity.getPasswordAlgo())
                .roles(domainUserIdentity.getRoles())
                .build();
    }
}