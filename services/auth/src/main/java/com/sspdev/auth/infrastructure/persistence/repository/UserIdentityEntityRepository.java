package com.sspdev.auth.infrastructure.persistence.repository;

import com.sspdev.auth.infrastructure.persistence.entity.UserIdentityJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserIdentityEntityRepository extends JpaRepository<UserIdentityJpaEntity, UUID> {

    Optional<UserIdentityJpaEntity> findByEmail(String email);

    Optional<UserIdentityJpaEntity> findByPhone(String phone);
}