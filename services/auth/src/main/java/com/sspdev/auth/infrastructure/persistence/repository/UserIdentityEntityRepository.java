package com.sspdev.auth.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sspdev.auth.infrastructure.persistence.entity.UserIdentityJpaEntity;

@Repository
public interface UserIdentityEntityRepository extends JpaRepository<UserIdentityJpaEntity, UUID> {

    Optional<UserIdentityJpaEntity> findByEmail(String email);

    Optional<UserIdentityJpaEntity> findByPhone(String phone);
}