package com.sspdev.auth.infrastructure.persistence.adapter;

import com.sspdev.auth.domain.model.UserIdentity;
import com.sspdev.auth.domain.repository.UserIdentityRepository;
import com.sspdev.auth.infrastructure.persistence.adapter.mapper.UserIdentityMapper;
import com.sspdev.auth.infrastructure.persistence.repository.UserIdentityEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserIdentityJpaRepositoryAdapter implements UserIdentityRepository {

    private final UserIdentityEntityRepository userJpaRepository;
    private final UserIdentityMapper userIdentityMapper;

    @Override
    public Optional<UserIdentity> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(userIdentityMapper::toDomain);
    }

    @Override
    public Optional<UserIdentity> findByPhone(String phone) {
        return userJpaRepository.findByPhone(phone)
                .map(userIdentityMapper::toDomain);
    }

    @Override
    public UserIdentity save(UserIdentity domainIdentity) {
        var jpaEntity = userIdentityMapper.toEntity(domainIdentity);
        var savedJpaEntity = userJpaRepository.save(jpaEntity);
        return userIdentityMapper.toDomain(savedJpaEntity);
    }
}