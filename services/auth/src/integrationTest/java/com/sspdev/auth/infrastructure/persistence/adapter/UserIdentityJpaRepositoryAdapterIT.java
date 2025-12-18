package com.sspdev.auth.infrastructure.persistence.adapter;

import com.sspdev.auth.testutil.TestDataUtil;
import com.sspdev.auth.domain.model.UserIdentity;
import com.sspdev.auth.setup.IntegrationTestBase;
import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

@RequiredArgsConstructor
class UserIdentityJpaRepositoryAdapterIT extends IntegrationTestBase {

    public static final String EXISTING_EMAIL = "user01@example.com";
    public static final String EXISTING_PHONE = "+7 900 111 11 01";
    public static final String NOT_EXISTING_EMAIL = "dummy@example.com";
    public static final String NOT_EXISTING_PHONE = "+7 000 000 00 00";

    private final UserIdentityJpaRepositoryAdapter jpaRepositoryAdapter;

    @Test
    void findByEmail_shouldReturnUserIdentity_whenExist() {
        var foundUserIdentity = jpaRepositoryAdapter.findByEmail(EXISTING_EMAIL);

        assertThat(foundUserIdentity).isPresent().hasValueSatisfying(user -> {
            assertThat(user.getEmail()).isEqualTo(EXISTING_EMAIL);
            assertThat(user.getPhone()).isEqualTo(EXISTING_PHONE);
            assertThat(user.getId()).isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111"));
            assertThat(user.getPasswordAlgo()).isEqualTo("bcrypt");
        });
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenNotExist() {
        var foundUserIdentity = jpaRepositoryAdapter.findByEmail(NOT_EXISTING_EMAIL);

        assertThat(foundUserIdentity).isEmpty();
    }

    @Test
    void findByPhone_shouldReturnUserIdentity_whenExist() {
        var foundUserIdentity = jpaRepositoryAdapter.findByPhone(EXISTING_PHONE);

        assertThat(foundUserIdentity).isPresent().hasValueSatisfying(user -> {
            assertThat(user.getEmail()).isEqualTo(EXISTING_EMAIL);
            assertThat(user.getPhone()).isEqualTo(EXISTING_PHONE);
            assertThat(user.getId()).isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111"));
            assertThat(user.getPasswordAlgo()).isEqualTo("bcrypt");
        });
    }

    @Test
    void findByPhone_shouldReturnEmpty_whenNotExist() {
        var foundUserIdentity = jpaRepositoryAdapter.findByPhone(NOT_EXISTING_PHONE);

        assertThat(foundUserIdentity).isEmpty();
    }

    @Test
    void save_shouldSaveAndReturnDomain_when2InputDomainValid() {
        var validDomainModel = TestDataUtil.getUserIdentityDomainModel();
        var savedUserIdentity = jpaRepositoryAdapter.save(validDomainModel);

        assertThat(savedUserIdentity).isNotNull();
        assertThat(savedUserIdentity).usingRecursiveComparison().isEqualTo(validDomainModel);
    }

    @Test
    void save_shouldSaveSetIdAndReturnDomain_whenIdNull() {
        var withNullId = TestDataUtil.getUserIdentityDomainModelWithNullId();
        var savedUserIdentity = jpaRepositoryAdapter.save(withNullId);

        assertThat(savedUserIdentity.getId()).isNotNull().isExactlyInstanceOf(UUID.class);
    }

    @Test
    void save_shouldSaveAndReturnDomain_whenEmailNull() {
        var withNullEmail = TestDataUtil.getUserIdentityDomainModelWithNullEmail();
        var savedUserIdentity = jpaRepositoryAdapter.save(withNullEmail);

        assertThat(savedUserIdentity).isNotNull().usingRecursiveComparison().isEqualTo(withNullEmail);
    }

    @Test
    void save_shouldTSaveAndReturnDomain_whenPhoneNull() {
        var withNullPhone = TestDataUtil.getUserIdentityDomainModelWithNullPhone();
        var savedUserIdentity = jpaRepositoryAdapter.save(withNullPhone);

        assertThat(savedUserIdentity).isNotNull().usingRecursiveComparison().isEqualTo(withNullPhone);
    }

    @Test
    void save_shouldThrowDataIntegrityViolationException_whenEmailAndPhoneNull() {
        var withNullEmailAndPhone = TestDataUtil.getUserIdentityDomainModelWithNullPassword();

        assertThrows(DataIntegrityViolationException.class, () -> jpaRepositoryAdapter.save(withNullEmailAndPhone));
    }

    @Test
    void save_shouldThrowDataIntegrityViolationException_whenPasswordNull() {
        var withNullPassword = TestDataUtil.getUserIdentityDomainModelWithNullPassword();

        assertThrows(DataIntegrityViolationException.class, () -> jpaRepositoryAdapter.save(withNullPassword));
    }

    @Test
    void save_shouldSaveAndSetDefaultPasswordAlgo_whenAlgoNull() {
        var withNullPasswordAlgo = TestDataUtil.getUserIdentityDomainModelWithNullPasswordAlgo();
        var savedUserIdentity = jpaRepositoryAdapter.save(withNullPasswordAlgo);

        assertThat(savedUserIdentity.getPasswordAlgo()).isEqualTo("bcrypt");
    }

    @Test
    void save_shouldSaveAndSetDefaultPasswordAlgo_whenAlgoNull_withinSameTransaction() {
        var withNullPasswordAlgo = TestDataUtil.getUserIdentityDomainModelWithNullPasswordAlgo();

        jpaRepositoryAdapter.save(withNullPasswordAlgo);
        var foundUserIdentity = jpaRepositoryAdapter.findByEmail(withNullPasswordAlgo.getEmail());

        assertThat(foundUserIdentity).isPresent();
        assertThat(foundUserIdentity.get().getPasswordAlgo()).isEqualTo("bcrypt");
    }

    @Test
    void save_shouldThrowDataIntegrityViolationException_whenEmailAlreadyInDb() {
        var userIdentityWithExistingEmail = UserIdentity.builder()
                .id(UUID.randomUUID())
                .email(EXISTING_EMAIL)
                .phone("8-925-869-96-98")
                .passwordHash("dummyPasswordHash")
                .passwordAlgo("dummyPasswordAlgo")
                .build();

        var violationException = assertThrows(DataIntegrityViolationException.class,
                () -> jpaRepositoryAdapter.save(userIdentityWithExistingEmail));
        var exceptionMessage = violationException.getMessage();

        assertThat(exceptionMessage).contains("user_identity_email_key");
    }

    @Test
    void save_shouldThrowDataIntegrityViolationException_whenPhoneAlreadyInDb() {
        var userIdentityWithExistingPhone = UserIdentity.builder()
                .id(UUID.randomUUID())
                .email("dummy@gmail.com")
                .phone(EXISTING_PHONE)
                .passwordHash("dummyPasswordHash")
                .passwordAlgo("dummyPasswordAlgo")
                .build();

        var violationException = assertThrows(DataIntegrityViolationException.class,
                () -> jpaRepositoryAdapter.save(userIdentityWithExistingPhone));
        var exceptionMessage = violationException.getMessage();

        assertThat(exceptionMessage).contains("user_identity_phone_key");
    }
}