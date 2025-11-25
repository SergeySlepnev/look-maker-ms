package com.sspdev.auth.infrastructure.persistence.repository;

import com.sspdev.auth.infrastructure.persistence.entity.UserIdentityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserIdentityEntityRepository extends JpaRepository<UserIdentityEntity, UUID> {

    Optional<UserIdentityEntity> findByEmail(String email);

    Optional<UserIdentityEntity> findByPhone(String phone);
}