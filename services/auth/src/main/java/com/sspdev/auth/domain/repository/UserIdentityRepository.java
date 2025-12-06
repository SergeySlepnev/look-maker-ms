package com.sspdev.auth.domain.repository;

import com.sspdev.auth.domain.model.UserIdentityDomain;

import java.util.Optional;

public interface UserIdentityRepository {

    Optional<UserIdentityDomain> findByEmail(String email);

    Optional<UserIdentityDomain> findByPhone(String phone);

    UserIdentityDomain save(UserIdentityDomain user);
}