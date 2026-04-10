package com.sspdev.auth.domain.port.out;

import java.util.Optional;

import com.sspdev.auth.domain.model.UserIdentity;

public interface UserIdentityRepository {

    Optional<UserIdentity> findByEmail(String email);

    Optional<UserIdentity> findByPhone(String phone);

    UserIdentity save(UserIdentity user);
}