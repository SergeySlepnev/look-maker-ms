package com.sspdev.auth.domain.repository;

import com.sspdev.auth.domain.model.UserIdentity;

import java.util.Optional;

public interface UserIdentityRepository {

    Optional<UserIdentity> findByEmail(String email);

    Optional<UserIdentity> findByPhone(String phone);

    UserIdentity save(UserIdentity user);
}