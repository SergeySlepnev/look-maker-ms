package com.sspdev.auth.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import com.sspdev.auth.domain.model.UserIdentity;
import com.sspdev.auth.domain.port.out.UserIdentityRepository;
import com.sspdev.auth.infrastructure.persistence.mapper.UserIdentityJpaMapper;
import com.sspdev.auth.infrastructure.persistence.repository.UserIdentityEntityRepository;

@Repository
@RequiredArgsConstructor
public class UserIdentityJpaRepositoryAdapter implements UserIdentityRepository {

    private final UserIdentityEntityRepository userJpaRepository;
    private final UserIdentityJpaMapper userIdentityJpaMapper;

    @Override
    public Optional<UserIdentity> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(userIdentityJpaMapper::toDomain);
    }

    @Override
    public Optional<UserIdentity> findByPhone(String phone) {
        return userJpaRepository.findByPhone(phone)
                .map(userIdentityJpaMapper::toDomain);
    }

    @Override
    public UserIdentity save(UserIdentity domainIdentity) {
        var jpaEntity = userIdentityJpaMapper.toEntity(domainIdentity);
        var savedJpaEntity = userJpaRepository.saveAndFlush(jpaEntity);
        return userIdentityJpaMapper.toDomain(savedJpaEntity);
    }
}