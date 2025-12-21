package com.sspdev.auth.infrastructure.adapter;

import com.sspdev.auth.infrastructure.persistence.adapter.UserIdentityJpaRepositoryAdapter;
import com.sspdev.auth.infrastructure.persistence.mapper.UserIdentityJpaMapper;
import com.sspdev.auth.infrastructure.persistence.repository.UserIdentityEntityRepository;
import com.sspdev.auth.testutil.TestDataUtil;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserIdentityJpaRepositoryAdapterTest {

    private static final String EXISTING_EMAIL = "existing@gmail.com";
    private static final String NOT_EXISTING_EMAIL = "NotExisting@gmail.com";
    private static final String EXISTING_PHONE = "89247788959";
    private static final String NOT_EXISTING_PHONE = "0000000000";

    @Mock
    private UserIdentityEntityRepository userJpaRepository;
    @Mock
    private UserIdentityJpaMapper userIdentityJpaMapper;
    @InjectMocks
    private UserIdentityJpaRepositoryAdapter jpaRepositoryAdapter;

    @Test
    void findByEmail_shouldFindUserByEmail_whenEmailExists() {
        var jpaEntity = TestDataUtil.getUserIdentityJpaEntity();
        var domainModel = TestDataUtil.getUserIdentityDomainModel();

        when(userJpaRepository.findByEmail(EXISTING_EMAIL)).thenReturn(Optional.of(jpaEntity));
        when(userIdentityJpaMapper.toDomain(jpaEntity)).thenReturn(domainModel);

        var actualUserIdentity = jpaRepositoryAdapter.findByEmail(EXISTING_EMAIL);

        assertThat(actualUserIdentity).isPresent().get().isEqualTo(domainModel);
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailNotExist() {
        when(userJpaRepository.findByEmail(NOT_EXISTING_EMAIL)).thenReturn(Optional.empty());

        var actualUserIdentity = jpaRepositoryAdapter.findByEmail(NOT_EXISTING_EMAIL);

        assertThat(actualUserIdentity).isEmpty();
    }

    @Test
    void findByPhone_shouldFindUserByPhone_whenPhoneExists() {
        var jpaEntity = TestDataUtil.getUserIdentityJpaEntity();
        var domainModel = TestDataUtil.getUserIdentityDomainModel();

        when(userJpaRepository.findByEmail(EXISTING_PHONE)).thenReturn(Optional.of(jpaEntity));
        when(userIdentityJpaMapper.toDomain(jpaEntity)).thenReturn(domainModel);

        var actualUserIdentity = jpaRepositoryAdapter.findByPhone(EXISTING_PHONE);

        assertThat(actualUserIdentity).isPresent().get().isEqualTo(domainModel);
    }

    @Test
    void findByPhone_shouldReturnEmpty_whenPhoneNotExist() {
        when(userJpaRepository.findByEmail(NOT_EXISTING_PHONE)).thenReturn(Optional.empty());

        var actualUserIdentity = jpaRepositoryAdapter.findByPhone(NOT_EXISTING_PHONE);

        assertThat(actualUserIdentity).isEmpty();
    }

    @Test
    void save_shouldPersisEntityAndReturnDomain() {
        var domain = TestDataUtil.getUserIdentityDomainModel();
        var jpaEntity = TestDataUtil.getUserIdentityJpaEntity();

        when(userIdentityJpaMapper.toEntity(domain)).thenReturn(jpaEntity);
        when(userJpaRepository.saveAndFlush(jpaEntity)).thenReturn(jpaEntity);
        when(userIdentityJpaMapper.toDomain(jpaEntity)).thenReturn(domain);

        var actualResult = jpaRepositoryAdapter.save(domain);
        assertThat(actualResult).isEqualTo(domain);
    }
}