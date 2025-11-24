--liquibase formated sql

--changeset sspdev:005-remove_default_id
ALTER TABLE auth.user_identity
    ALTER COLUMN id DROP DEFAULT;
--rollback ALTER TABLE auth.user_identity ALTER COLUMN id SET DEFAULT gen_random_uuid();

--changeset sspdev:006-remove_default_password_algo
ALTER TABLE auth.user_identity
    ALTER COLUMN password_algo DROP DEFAULT;
--rollback ALTER TABLE auth.user_identity ALTER COLUMN password_algo SET DEFAULT 'bcrypt';