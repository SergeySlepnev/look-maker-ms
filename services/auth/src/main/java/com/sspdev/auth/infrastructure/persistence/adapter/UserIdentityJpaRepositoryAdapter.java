package com.sspdev.auth.infrastructure.persistence.adapter;

import com.sspdev.auth.domain.model.UserIdentityDomain;
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
    public Optional<UserIdentityDomain> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(userIdentityMapper::toDomain);
    }

    @Override
    public Optional<UserIdentityDomain> findByPhone(String phone) {
        return userJpaRepository.findByPhone(phone)
                .map(userIdentityMapper::toDomain);
    }

    @Override
    public UserIdentityDomain save(UserIdentityDomain domainIdentity) {
        var jpaEntity = userIdentityMapper.toEntity(domainIdentity);
        var savedJpaEntity = userJpaRepository.save(jpaEntity);
        return userIdentityMapper.toDomain(savedJpaEntity);
    }
}